package com.springcloud.demo.bookingreceipt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingReceiptApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingReceiptApplication.class, args);
	}

}
