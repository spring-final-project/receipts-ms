package com.springcloud.demo.bookingreceipt.receipt.consumer;

import com.springcloud.demo.bookingreceipt.client.booking.dto.BookingDTO;
import com.springcloud.demo.bookingreceipt.messaging.MessagingProducer;
import com.springcloud.demo.bookingreceipt.receipt.service.BookingReceiptService;
import com.springcloud.demo.bookingreceipt.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HandlerService {

    private final BookingReceiptService bookingReceiptService;
    private final MessagingProducer messagingProducer;

    @Value("${spring.kafka.topics.BOOKING_RECEIPT_GENERATED_TOPIC}")
    private String bookingReceiptGeneratedTopic;

    public void handleBookingCreated(String bookingJson) {
        BookingDTO bookingDTO = JsonUtils.fromJson(bookingJson, BookingDTO.class);

        try {
            String url = bookingReceiptService.createReceipt(bookingDTO);

            if (url != null) {
                bookingDTO.setReceiptUrl(url);
                messagingProducer.sendMessage(bookingReceiptGeneratedTopic, JsonUtils.toJson(bookingDTO));
            }
        }
        catch (JRException e) {
            e.printStackTrace();
        }
    }
}
