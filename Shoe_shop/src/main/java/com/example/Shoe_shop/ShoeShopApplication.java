package com.example.Shoe_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShoeShopApplication {

	public static void main(String[] args) {
        SpringApplication.run(ShoeShopApplication.class, args
        );
	}

}
