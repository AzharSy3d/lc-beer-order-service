package com.azsyed.brewery.model.events;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateOrderResult {
    private UUID orderId;
    @Getter
    @Accessors(fluent = true)
    private Boolean isValid;
}
