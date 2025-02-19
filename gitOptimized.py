import os
import javalang
import requests
import subprocess
import json
from typing import List, Dict

OLLAMA_API_URL = "http://127.0.0.1:11434/api/generate"

def is_git_repo(path: str = ".") -> bool:
    """Check if directory is a git repository by looking for .git folder"""
    return os.path.isdir(os.path.join(path, ".git"))

def get_git_config(key: str) -> str:
    """Get git configuration value"""
    try:
        return subprocess.check_output(["git", "config", "--get", key], 
                                     stderr=subprocess.DEVNULL).decode().strip()
    except subprocess.CalledProcessError:
        return ""

def get_remote_repos() -> List[Dict]:
    """Get list of remote repositories from git remotes"""
    try:
        remotes = subprocess.check_output(["git", "remote", "-v"], 
                                        stderr=subprocess.DEVNULL).decode().strip()
        repos = []
        for line in remotes.split("\n"):
            if line.endswith("(fetch)"):
                url = line.split()[1]
                if url.startswith("https://"):
                    parts = url.rstrip('/').split('/')
                    repos.append({
                        "name": parts[-1].replace(".git", ""),
                        "url": url
                    })
        return repos
    except subprocess.CalledProcessError:
        return []

def select_repository(repos: List[Dict]) -> Dict:
    """Prompt user to select a repository"""
    print("\nAvailable Repositories:")
    for i, repo in enumerate(repos):
        print(f"{i + 1}. {repo['name']}")
    while True:
        try:
            choice = int(input("\nSelect a repository (enter number): ")) - 1
            if 0 <= choice < len(repos):
                return repos[choice]
            print("Invalid selection. Please try again.")
        except ValueError:
            print("Please enter a valid number.")

def clone_repository(repo_url: str, target_dir: str) -> None:
    """Clone repository if it doesn't exist"""
    if not os.path.exists(target_dir):
        print(f"\nCloning repository to {target_dir}...")
        subprocess.run(["git", "clone", repo_url, target_dir], check=True)
    else:
        print(f"\nUsing existing directory: {target_dir}")
        subprocess.run(["git", "pull"], cwd=target_dir, check=True)

def select_module(base_dir: str) -> str:
    """Prompt user to select a module from available directories"""
    modules = [d for d in os.listdir(base_dir) 
              if os.path.isdir(os.path.join(base_dir, d)) and not d.startswith('.')]
    
    if not modules:
        print("\nNo modules found in the repository.")
        exit(1)
        
    print("\nAvailable Modules:")
    for i, module in enumerate(modules):
        print(f"{i + 1}. {module}")
    while True:
        try:
            choice = int(input("\nSelect a module (enter number): ")) - 1
            if 0 <= choice < len(modules):
                return os.path.join(base_dir, modules[choice])
            print("Invalid selection. Please try again.")
        except ValueError:
            print("Please enter a valid number.")

def process_java_files(module_path: str) -> None:
    """Process Java files in the selected module"""
    for root, _, files in os.walk(module_path):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, "r", encoding="utf-8") as f:
                        tree = javalang.parse.parse(f.read())
                    
                    for _, node in tree.filter(javalang.tree.ClassDeclaration):
                        if node.name.endswith("Service"):
                            class_info = {
                                "name": node.name,
                                "methods": [{
                                    "name": m.name,
                                    "return_type": m.return_type.name if m.return_type else "void",
                                    "parameters": [{"type": p.type.name, "name": p.name} for p in m.parameters]
                                } for m in node.methods]
                            }
                            add_javadoc_to_methods(file_path, class_info)
                except Exception as e:
                    print(f"Skipped {file}: {str(e)}")

def create_pr(base_dir: str) -> None:
    """Create a new branch and push changes"""
    branch_name = input("\nEnter new branch name: ").strip()
    commit_message = input("Enter commit message: ").strip()
    
    try:
        # Create and push branch
        subprocess.run(["git", "checkout", "-b", branch_name], cwd=base_dir, check=True)
        subprocess.run(["git", "add", "."], cwd=base_dir, check=True)
        subprocess.run(["git", "commit", "-m", commit_message], cwd=base_dir, check=True)
        subprocess.run(["git", "push", "origin", branch_name], cwd=base_dir, check=True)
        
        print("\n✅ Changes pushed successfully!")
        print("You can now create a PR manually using:")
        print(f"1. Visit your repository on GitHub")
        print(f"2. Click 'Compare & pull request' for branch '{branch_name}'")
        print(f"3. Add details and create the PR")
    except subprocess.CalledProcessError as e:
        print(f"\n❌ Error creating PR: {e}")
        print("You can manually create a PR using:")
        print(f"1. git checkout {branch_name}")
        print(f"2. git push origin {branch_name}")
        print(f"3. Create PR on GitHub")

def main():
    original_dir = os.getcwd()
    
    if is_git_repo():
        print("\n🔍 Found local Git repository")
        base_dir = original_dir
    else:
        print("\n🔍 No local Git repository found. Checking remotes...")
        repos = get_remote_repos()
        if not repos:
            print("No remote repositories found. Please clone a repository first.")
            exit(1)
        repo = select_repository(repos)
        base_dir = repo['name']
        clone_repository(repo['url'], base_dir)
        os.chdir(base_dir)

    print("\n🏗 Starting documentation generation...")
    module_path = select_module(base_dir)
    process_java_files(module_path)
    
    if input("\n🚀 Create and push changes? (y/n): ").lower() == "y":
        create_pr(base_dir)
    
    os.chdir(original_dir)
    print("\n🎉 Documentation generation complete!")

if __name__ == "__main__":
    main()