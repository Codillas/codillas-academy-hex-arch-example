package com.codillas.academy.commerce.catalog.adapter.inbound.web;

import com.codillas.academy.commerce.catalog.api.CreateProductCommand;
import com.codillas.academy.commerce.catalog.api.CreateProductUseCase;
import com.codillas.academy.commerce.catalog.api.DeactivateProductUseCase;
import com.codillas.academy.commerce.catalog.api.GetProductUseCase;
import com.codillas.academy.commerce.catalog.api.ListProductsUseCase;
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

    private final CreateProductUseCase createProduct;
    private final GetProductUseCase getProduct;
    private final ListProductsUseCase listProducts;
    private final DeactivateProductUseCase deactivateProduct;

    ProductController(
            CreateProductUseCase createProduct,
            GetProductUseCase getProduct,
            ListProductsUseCase listProducts,
            DeactivateProductUseCase deactivateProduct
    ) {
        this.createProduct = createProduct;
        this.getProduct = getProduct;
        this.listProducts = listProducts;
        this.deactivateProduct = deactivateProduct;
    }

    @PostMapping
    ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        var result = createProduct.create(new CreateProductCommand(request.name(), request.price()));
        return ResponseEntity.created(URI.create("/api/products/" + result.id()))
                .body(ProductResponse.from(result));
    }

    @GetMapping("/{productId}")
    ProductResponse get(@PathVariable UUID productId) {
        return ProductResponse.from(getProduct.get(productId));
    }

    @GetMapping
    List<ProductResponse> list() {
        return listProducts.list().stream().map(ProductResponse::from).toList();
    }

    @PutMapping("/{productId}/deactivation")
    ProductResponse deactivate(@PathVariable UUID productId) {
        return ProductResponse.from(deactivateProduct.deactivate(productId));
    }
}
