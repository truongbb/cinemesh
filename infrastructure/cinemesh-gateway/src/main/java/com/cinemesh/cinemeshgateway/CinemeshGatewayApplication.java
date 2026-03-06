package com.cinemesh.cinemeshgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CinemeshGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemeshGatewayApplication.class, args);
    }

}
