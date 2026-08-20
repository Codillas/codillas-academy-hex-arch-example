package com.codillas.academy.commerce.inventory.adapter.inbound.web;

import com.codillas.academy.commerce.inventory.api.GetInventoryUseCase;
import com.codillas.academy.commerce.inventory.api.RestockInventoryUseCase;
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

    private final RestockInventoryUseCase restockInventory;
    private final GetInventoryUseCase getInventory;

    InventoryController(RestockInventoryUseCase restockInventory, GetInventoryUseCase getInventory) {
        this.restockInventory = restockInventory;
        this.getInventory = getInventory;
    }

    @PostMapping("/{productId}/restocks")
    @ResponseStatus(HttpStatus.OK)
    InventoryResponse restock(
            @PathVariable UUID productId,
            @Valid @RequestBody RestockInventoryRequest request
    ) {
        return InventoryResponse.from(restockInventory.restock(productId, request.quantity()));
    }

    @GetMapping("/{productId}")
    InventoryResponse get(@PathVariable UUID productId) {
        return InventoryResponse.from(getInventory.get(productId));
    }
}
