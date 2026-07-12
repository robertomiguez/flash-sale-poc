package com.example.flashsale.controller;

import com.example.flashsale.dto.StockUpdateRequest;
import com.example.flashsale.dto.StockResetRequest;
import com.example.flashsale.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@Validated
@RequestMapping("/api/admin/stock")
public class AdminStockController {
    private final InventoryService inventoryService;

    public AdminStockController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<Void> setStock(@PathVariable Long itemId, @RequestBody StockUpdateRequest request) {
        inventoryService.upsertStock(itemId, request.availableQuantity());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/reset")
    public ResponseEntity<Void> resetStock(@PathVariable Long itemId, @RequestBody StockResetRequest request) {
        inventoryService.upsertStock(itemId, request.quantity());
        return ResponseEntity.noContent().build();
    }
}
