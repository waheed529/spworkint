package com.intellect.fna;

import javax.ws.rs.core.MediaType;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
public class FnaCustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FnaCustomerServiceApplication.class, args);
	}
}
