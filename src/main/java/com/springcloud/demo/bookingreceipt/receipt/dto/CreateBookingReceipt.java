package com.springcloud.demo.bookingreceipt.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingReceipt {
    private String roomId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String userId;
}
