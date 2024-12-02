package com.springcloud.demo.bookingreceipt.receipt.consumer;

import com.amazonaws.services.lambda.runtime.events.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class HandlerConsumer {

    private final HandlerService handlerService;

    /**
     * Work in local, for development
     * It will be ignored in production
     */
    @KafkaListener(topics = "${spring.kafka.topics.BOOKING_CREATED_TOPIC}")
    public void handleNewBooking(String booking) {
        System.out.println("booking = " + booking);
        log.info("booking = {}", booking);
        handlerService.handleBookingCreated(booking);
    }

    /**
     * Work on lambda environment
     * It will be ignored in local
     */
    @Bean
    public Function<KafkaEvent, String> handleNewAskServerless() {
        return event -> {
            System.out.println("event = " + event);
            for (Map.Entry<String, java.util.List<KafkaEvent.KafkaEventRecord>> entry : event.getRecords().entrySet()) {
                String key = entry.getKey();
                System.out.println("Key: " + key);

                for (KafkaEvent.KafkaEventRecord record : entry.getValue()) {
                    System.out.println("Record: " + record);

                    byte[] value = Base64.getDecoder().decode(record.getValue());
                    String message = new String(value);
                    System.out.println("Message: " + message);
                    this.handlerService.handleBookingCreated(message);
                }
            }
            return "Receipt created";
        };
    }
}
