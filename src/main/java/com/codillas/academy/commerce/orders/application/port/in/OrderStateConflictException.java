package com.codillas.academy.commerce.orders.application.port.in;

public final class OrderStateConflictException extends RuntimeException {

    public OrderStateConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
