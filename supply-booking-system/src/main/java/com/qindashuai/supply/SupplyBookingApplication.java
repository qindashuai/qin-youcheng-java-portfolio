package com.qindashuai.supply;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SupplyBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyBookingApplication.class, args);
    }
}
