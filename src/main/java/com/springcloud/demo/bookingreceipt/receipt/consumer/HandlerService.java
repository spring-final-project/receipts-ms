package com.springcloud.demo.bookingreceipt.receipt.consumer;

import com.springcloud.demo.bookingreceipt.client.booking.dto.BookingDTO;
import com.springcloud.demo.bookingreceipt.client.rooms.RoomClient;
import com.springcloud.demo.bookingreceipt.client.rooms.dto.RoomDTO;
import com.springcloud.demo.bookingreceipt.client.users.UserClient;
import com.springcloud.demo.bookingreceipt.client.users.dto.UserDTO;
import com.springcloud.demo.bookingreceipt.messaging.MessagingProducer;
import com.springcloud.demo.bookingreceipt.receipt.service.BookingReceiptService;
import com.springcloud.demo.bookingreceipt.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HandlerService {

    private final BookingReceiptService bookingReceiptService;
    private final RoomClient roomClient;
    private final UserClient userClient;
    private final MessagingProducer messagingProducer;

    @Value("${spring.kafka.topics.BOOKING_RECEIPT_GENERATED_TOPIC}")
    private String bookingReceiptGeneratedTopic;

    public void handleBookingCreated(String bookingJson) {
        BookingDTO bookingDTO = JsonUtils.fromJson(bookingJson, BookingDTO.class);

        CompletableFuture<RoomDTO> roomResponse = CompletableFuture.supplyAsync(()-> roomClient.findById(bookingDTO.getRoomId()));
        CompletableFuture<UserDTO> userResponse = CompletableFuture.supplyAsync(()-> userClient.findById(bookingDTO.getUserId()));

        CompletableFuture<Void> completableFuture = CompletableFuture.allOf(roomResponse, userResponse);
        completableFuture.join();

        RoomDTO room = roomResponse.join();
        UserDTO user = userResponse.join();

        UserDTO owner = userClient.findById(room.getOwnerId().toString());

        String url = bookingReceiptService.createReceipt(bookingDTO, room, owner, user);

        if(url != null){
            bookingDTO.setReceiptUrl(url);
            messagingProducer.sendMessage(bookingReceiptGeneratedTopic, JsonUtils.toJson(bookingDTO));
        }
    }
}
