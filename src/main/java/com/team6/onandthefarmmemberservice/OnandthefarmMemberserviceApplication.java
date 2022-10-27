package com.team6.onandthefarmmemberservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OnandthefarmMemberserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnandthefarmMemberserviceApplication.class, args);
	}

}
