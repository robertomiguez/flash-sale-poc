package com.example.flashsale.config;

import com.example.flashsale.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
public class StockDataInitializer {
    private static final Logger log = LoggerFactory.getLogger(StockDataInitializer.class);

    @Bean
    public ApplicationRunner seedInitialStock(
            InventoryService inventoryService,
            Environment environment,
            @Value("${app.stock.item-id:1}") Long itemId,
            @Value("${app.stock.quantity:10}") Long quantity) {
        return args -> {
            boolean devProfile = environment.acceptsProfiles(Profiles.of("dev"));
            Long stockValue = inventoryService.seedStartupStock(itemId, quantity, devProfile).getAvailableQuantity();

            log.info("{} inventory and Redis stock item:stock:{} = {}",
                    devProfile ? "Dev profile active. Reset" : "Synced", itemId, stockValue);
        };
    }
}
