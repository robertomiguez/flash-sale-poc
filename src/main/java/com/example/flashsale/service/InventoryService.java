package com.example.flashsale.service;

import com.example.flashsale.model.InventoryEntity;
import com.example.flashsale.repository.InventoryRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StringRedisTemplate redisTemplate;

    public InventoryService(InventoryRepository inventoryRepository, StringRedisTemplate redisTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.redisTemplate = redisTemplate;
    }

    public InventoryEntity upsertStock(Long itemId, Long quantity) {
        InventoryEntity inventory = inventoryRepository.findById(itemId)
                .orElseGet(() -> new InventoryEntity(itemId, quantity, Instant.now()));
        inventory.setAvailableQuantity(quantity);
        inventory.setUpdatedAt(Instant.now());
        inventoryRepository.save(inventory);
        redisTemplate.opsForValue().set(stockKey(itemId), String.valueOf(quantity));
        return inventory;
    }

    public InventoryEntity seedStartupStock(Long itemId, Long defaultQuantity, boolean forceReset) {
        InventoryEntity inventory = inventoryRepository.findById(itemId)
                .orElseGet(() -> new InventoryEntity(itemId, defaultQuantity, Instant.now()));
        Long quantity = forceReset ? defaultQuantity : inventory.getAvailableQuantity();
        inventory.setAvailableQuantity(quantity);
        inventory.setUpdatedAt(Instant.now());
        inventoryRepository.save(inventory);
        redisTemplate.opsForValue().set(stockKey(itemId), String.valueOf(quantity));
        return inventory;
    }

    public void restoreStock(Long itemId) {
        redisTemplate.opsForValue().increment(stockKey(itemId));
    }

    public Optional<InventoryEntity> findInventory(Long itemId) {
        return inventoryRepository.findById(itemId);
    }

    public Long getRedisStock(Long itemId) {
        String value = redisTemplate.opsForValue().get(stockKey(itemId));
        return value == null ? null : Long.valueOf(value);
    }

    private String stockKey(Long itemId) {
        return "item:stock:" + itemId;
    }
}
