package com.codillas.academy.commerce.inventory.adapter.in.web;

import jakarta.validation.constraints.Positive;

record RestockInventoryRequest(@Positive int quantity) {
}
