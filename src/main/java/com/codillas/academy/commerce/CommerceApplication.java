package com.codillas.academy.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.Modulithic;

import java.time.Clock;

@Modulithic(systemName = "Commerce")
@SpringBootApplication
public class CommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommerceApplication.class, args);
    }

    @Bean
    Clock applicationClock() {
        return Clock.systemUTC();
    }
}
