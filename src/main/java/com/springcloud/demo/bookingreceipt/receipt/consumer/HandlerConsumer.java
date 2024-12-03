package com.springcloud.demo.bookingreceipt.receipt.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerConsumer {

    private final HandlerService handlerService;

    @KafkaListener(topics = "${spring.kafka.topics.BOOKING_CREATED_TOPIC}")
    public void handleNewBooking(String booking) {
        System.out.println("booking = " + booking);
        log.info("booking = {}", booking);
        handlerService.handleBookingCreated(booking);
    }
}
