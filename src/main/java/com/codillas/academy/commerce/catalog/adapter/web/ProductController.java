package com.codillas.academy.commerce.catalog.adapter.web;

import com.codillas.academy.commerce.catalog.api.CreateProductCommand;
import com.codillas.academy.commerce.catalog.api.ProductUseCases;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
class ProductController {

    private final ProductUseCases products;

    ProductController(ProductUseCases products) {
        this.products = products;
    }

    @PostMapping
    ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        var result = products.create(new CreateProductCommand(request.name(), request.price()));
        return ResponseEntity.created(URI.create("/api/products/" + result.id()))
                .body(ProductResponse.from(result));
    }

    @GetMapping("/{productId}")
    ProductResponse get(@PathVariable UUID productId) {
        return ProductResponse.from(products.get(productId));
    }

    @GetMapping
    List<ProductResponse> list() {
        return products.list().stream().map(ProductResponse::from).toList();
    }

    @PutMapping("/{productId}/deactivation")
    ProductResponse deactivate(@PathVariable UUID productId) {
        return ProductResponse.from(products.deactivate(productId));
    }
}
