package com.codillas.academy.commerce.inventory.adapter.web;

import com.codillas.academy.commerce.inventory.api.InsufficientStockException;
import com.codillas.academy.commerce.inventory.api.InventoryUnavailableException;
import com.codillas.academy.commerce.inventory.application.InventoryNotFoundException;
import com.codillas.academy.commerce.inventory.application.ProductUnavailableForInventoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = InventoryController.class)
class InventoryExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    ProblemDetail handleNotFound(InventoryNotFoundException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Inventory not found");
        problem.setProperty("productId", exception.productId());
        return problem;
    }

    @ExceptionHandler(InventoryUnavailableException.class)
    ProblemDetail handleUnavailableInventory(InventoryUnavailableException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Inventory unavailable");
        problem.setProperty("productId", exception.productId());
        return problem;
    }

    @ExceptionHandler(ProductUnavailableForInventoryException.class)
    ProblemDetail handleUnavailableProduct(ProductUnavailableForInventoryException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, exception.getMessage());
        problem.setTitle("Product unavailable");
        return problem;
    }

    @ExceptionHandler(InsufficientStockException.class)
    ProblemDetail handleInsufficientStock(InsufficientStockException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Insufficient stock");
        problem.setProperty("productId", exception.productId());
        problem.setProperty("requestedQuantity", exception.requestedQuantity());
        problem.setProperty("availableQuantity", exception.availableQuantity());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidArgument(IllegalArgumentException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Invalid inventory request");
        return problem;
    }
}
