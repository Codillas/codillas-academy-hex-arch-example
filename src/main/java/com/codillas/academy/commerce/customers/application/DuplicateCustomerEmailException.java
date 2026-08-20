package com.codillas.academy.commerce.customers.application;

public final class DuplicateCustomerEmailException extends RuntimeException {

    public DuplicateCustomerEmailException(String email) {
        super("A customer with email %s already exists".formatted(email));
    }
}
