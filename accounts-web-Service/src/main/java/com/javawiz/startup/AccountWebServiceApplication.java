package com.javawiz.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import com.javawiz.controller.WebAccountsController;
import com.javawiz.service.WebAccountsService;

/**
 * Accounts web-service. Works as a microservice client, fetching data from the
 * Account-Service. Uses the Discovery Server (Eureka) to find the microservice.
 * 
 */
@SpringBootApplication
@EnableEurekaClient 
@ComponentScan(value = "com.javawiz")
public class AccountWebServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountWebServiceApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
