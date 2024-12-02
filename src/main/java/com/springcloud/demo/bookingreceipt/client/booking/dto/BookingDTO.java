package com.springcloud.demo.bookingreceipt.client.booking.dto;

import com.springcloud.demo.bookingreceipt.client.rooms.dto.RoomDTO;
import com.springcloud.demo.bookingreceipt.client.users.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private String id;
    private String createdAt;
    private String receiptUrl;
    private String checkIn;
    private String checkOut;
    private UserDTO user;
    private RoomDTO room;
}
