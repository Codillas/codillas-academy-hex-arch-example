package com.codillas.academy.commerce.customers.adapter.in.web;

import com.codillas.academy.commerce.customers.application.port.in.CustomerNotFoundException;
import com.codillas.academy.commerce.customers.application.port.in.DuplicateCustomerEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = CustomerController.class)
class CustomerExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleNotFound(CustomerNotFoundException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Customer not found");
        problem.setProperty("customerId", exception.customerId());
        return problem;
    }

    @ExceptionHandler(DuplicateCustomerEmailException.class)
    ProblemDetail handleDuplicateEmail(DuplicateCustomerEmailException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Customer already exists");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidArgument(IllegalArgumentException exception) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Invalid customer request");
        return problem;
    }
}
