package com.codillas.academy.commerce.customers.adapter.in.web;

import com.codillas.academy.commerce.customers.application.port.in.CustomerUseCases;
import com.codillas.academy.commerce.customers.application.port.in.RegisterCustomerCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
class CustomerController {

    private final CustomerUseCases customers;

    CustomerController(CustomerUseCases customers) {
        this.customers = customers;
    }

    @PostMapping
    ResponseEntity<CustomerResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        var result = customers.register(new RegisterCustomerCommand(request.name(), request.email()));
        return ResponseEntity.created(URI.create("/api/customers/" + result.id()))
                .body(CustomerResponse.from(result));
    }

    @GetMapping("/{customerId}")
    CustomerResponse get(@PathVariable UUID customerId) {
        return CustomerResponse.from(customers.get(customerId));
    }

    @GetMapping
    List<CustomerResponse> list() {
        return customers.list().stream().map(CustomerResponse::from).toList();
    }
}
