package com.springcloud.demo.bookingreceipt.messaging;

public interface MessagingProducer {
    void sendMessage(String topic, String message);
}
