package com.codillas.academy.commerce.inventory.adapter.inbound.web;

import jakarta.validation.constraints.Positive;

record RestockInventoryRequest(@Positive int quantity) {
}
