package com.railway.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    @org.springframework.cloud.client.loadbalancer.LoadBalanced
    public org.springframework.web.client.RestTemplate restTemplate() {
        org.springframework.web.client.RestTemplate rt = new org.springframework.web.client.RestTemplate();
        rt.setRequestFactory(new org.springframework.http.client.JdkClientHttpRequestFactory());
        return rt;
    }
}