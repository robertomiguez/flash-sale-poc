package com.example.flashsale.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockResetRequest(@NotNull @Min(0) Long quantity) {
}
