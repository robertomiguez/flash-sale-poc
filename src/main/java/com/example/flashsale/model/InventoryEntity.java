package com.example.flashsale.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "inventory")
public class InventoryEntity {
    @Id
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "available_quantity", nullable = false)
    private Long availableQuantity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InventoryEntity() {
    }

    public InventoryEntity(Long itemId, Long availableQuantity, Instant updatedAt) {
        this.itemId = itemId;
        this.availableQuantity = availableQuantity;
        this.updatedAt = updatedAt;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Long availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
