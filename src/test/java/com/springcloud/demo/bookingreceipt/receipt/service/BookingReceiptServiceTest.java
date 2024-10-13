package com.springcloud.demo.bookingreceipt.receipt.service;

import com.springcloud.demo.bookingreceipt.client.booking.dto.BookingDTO;
import com.springcloud.demo.bookingreceipt.client.rooms.dto.RoomDTO;
import com.springcloud.demo.bookingreceipt.client.users.dto.UserDTO;
import static org.assertj.core.api.Assertions.*;

import com.springcloud.demo.bookingreceipt.images.ImageProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.BDDMockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BookingReceiptServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private ImageProviderService imagesProviderService;

    @InjectMocks
    private BookingReceiptService bookingReceiptService;

    @Test
    void createReceipt() {
        BookingDTO bookingDTO = BookingDTO.builder()
                .id(UUID.randomUUID().toString())
                .checkIn(LocalDateTime.now().plusDays(1).toString())
                .checkOut(LocalDateTime.now().plusDays(3).toString())
                .createdAt(LocalDateTime.now().toString())
                .build();
        RoomDTO roomDTO = RoomDTO.builder()
                .id(UUID.randomUUID())
                .name("Suite")
                .description("Lorem ipsum dolor sit amet")
                .build();
        UserDTO ownerDTO = UserDTO.builder()
                .id(UUID.randomUUID())
                .name("Owner")
                .build();
        UserDTO userDTO = UserDTO.builder()
                .id(UUID.randomUUID())
                .name("Customer")
                .build();

        byte[] report = new byte[0];

        given(reportService.generateReport(any(InputStream.class), anyMap())).willReturn(report);
        given(imagesProviderService.uploadFile(any(byte[].class), anyString())).willReturn("s3_url");

        String url = bookingReceiptService.createReceipt(bookingDTO, roomDTO, ownerDTO, userDTO);

        verify(reportService).generateReport(any(InputStream.class), anyMap());
        verify(imagesProviderService).uploadFile(eq(report), anyString());
        assertThat(url).isNotNull();
    }

}