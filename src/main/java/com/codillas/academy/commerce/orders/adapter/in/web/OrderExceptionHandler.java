package com.codillas.academy.commerce.orders.adapter.in.web;

import com.codillas.academy.commerce.inventory.application.port.in.InsufficientStockException;
import com.codillas.academy.commerce.inventory.application.port.in.InventoryUnavailableException;
import com.codillas.academy.commerce.orders.application.port.in.OrderNotFoundException;
import com.codillas.academy.commerce.orders.application.port.in.OrderPrerequisiteException;
import com.codillas.academy.commerce.orders.application.port.in.OrderStateConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = OrderController.class)
class OrderExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail handleNotFound(OrderNotFoundException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Order not found");
        problem.setProperty("orderId", exception.orderId());
        return problem;
    }

    @ExceptionHandler(OrderPrerequisiteException.class)
    ProblemDetail handlePrerequisite(OrderPrerequisiteException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, exception.getMessage());
        problem.setTitle("Order prerequisite not met");
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

    @ExceptionHandler({OrderStateConflictException.class, InventoryUnavailableException.class})
    ProblemDetail handleStateConflict(RuntimeException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Order state conflict");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidArgument(IllegalArgumentException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Invalid order request");
        return problem;
    }
}
