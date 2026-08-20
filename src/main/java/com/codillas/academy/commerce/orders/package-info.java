@org.springframework.modulith.ApplicationModule(
        displayName = "Orders",
        allowedDependencies = {
                "customers::api",
                "catalog::api",
                "inventory::api"
        }
)
package com.codillas.academy.commerce.orders;
