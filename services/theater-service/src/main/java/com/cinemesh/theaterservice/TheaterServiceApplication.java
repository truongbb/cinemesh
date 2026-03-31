package com.cinemesh.theaterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.cinemesh")
public class TheaterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheaterServiceApplication.class, args);
    }

}
