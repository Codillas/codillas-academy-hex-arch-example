package com.codillas.academy.commerce.inventory.adapter.web;

import com.codillas.academy.commerce.inventory.api.InventoryUseCases;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
class InventoryController {

    private final InventoryUseCases inventory;

    InventoryController(InventoryUseCases inventory) {
        this.inventory = inventory;
    }

    @PostMapping("/{productId}/restocks")
    @ResponseStatus(HttpStatus.OK)
    InventoryResponse restock(
            @PathVariable UUID productId,
            @Valid @RequestBody RestockInventoryRequest request
    ) {
        return InventoryResponse.from(inventory.restock(productId, request.quantity()));
    }

    @GetMapping("/{productId}")
    InventoryResponse get(@PathVariable UUID productId) {
        return InventoryResponse.from(inventory.get(productId));
    }
}
