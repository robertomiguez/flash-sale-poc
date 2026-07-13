package com.example.flashsale.dto;

import com.example.flashsale.model.InventoryEntity;

import java.time.Instant;

public record InventoryStatusResponse(Long itemId, Long availableQuantity, Long redisQuantity, Instant updatedAt) {
    public static InventoryStatusResponse from(InventoryEntity inventory, Long redisQuantity) {
        return new InventoryStatusResponse(inventory.getItemId(), inventory.getAvailableQuantity(), redisQuantity,
                inventory.getUpdatedAt());
    }
}
