package com.codillas.academy.commerce.customers.adapter.inbound.web;

import com.codillas.academy.commerce.customers.api.GetCustomerUseCase;
import com.codillas.academy.commerce.customers.api.ListCustomersUseCase;
import com.codillas.academy.commerce.customers.api.RegisterCustomerCommand;
import com.codillas.academy.commerce.customers.api.RegisterCustomerUseCase;
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

    private final RegisterCustomerUseCase registerCustomer;
    private final GetCustomerUseCase getCustomer;
    private final ListCustomersUseCase listCustomers;

    CustomerController(
            RegisterCustomerUseCase registerCustomer,
            GetCustomerUseCase getCustomer,
            ListCustomersUseCase listCustomers
    ) {
        this.registerCustomer = registerCustomer;
        this.getCustomer = getCustomer;
        this.listCustomers = listCustomers;
    }

    @PostMapping
    ResponseEntity<CustomerResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        var result = registerCustomer.register(new RegisterCustomerCommand(request.name(), request.email()));
        return ResponseEntity.created(URI.create("/api/customers/" + result.id()))
                .body(CustomerResponse.from(result));
    }

    @GetMapping("/{customerId}")
    CustomerResponse get(@PathVariable UUID customerId) {
        return CustomerResponse.from(getCustomer.get(customerId));
    }

    @GetMapping
    List<CustomerResponse> list() {
        return listCustomers.list().stream().map(CustomerResponse::from).toList();
    }
}
