package com.vehicleoem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableTransactionManagement
public class VehicleOemServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(VehicleOemServerApplication.class, args);
    }
}
