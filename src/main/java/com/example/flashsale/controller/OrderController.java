package com.example.flashsale.controller;

import com.example.flashsale.dto.ReserveRequest;
import com.example.flashsale.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> reserve(@Valid @RequestBody ReserveRequest request) {
        boolean accepted = orderService.reserve(request);
        return accepted ? ResponseEntity.accepted().build() : ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
