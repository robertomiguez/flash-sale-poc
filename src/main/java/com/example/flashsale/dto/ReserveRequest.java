package com.example.flashsale.dto;

import jakarta.validation.constraints.NotNull;

public record ReserveRequest(@NotNull Long itemId, @NotNull Long userId, boolean forceFail) {
}
