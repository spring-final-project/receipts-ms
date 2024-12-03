package com.springcloud.demo.bookingreceipt.receipt.service;

import com.springcloud.demo.bookingreceipt.client.booking.dto.BookingDTO;
import com.springcloud.demo.bookingreceipt.client.rooms.dto.RoomDTO;
import com.springcloud.demo.bookingreceipt.client.users.dto.UserDTO;
import static org.assertj.core.api.Assertions.*;

import com.springcloud.demo.bookingreceipt.images.ImageProviderService;
import net.sf.jasperreports.engine.JRException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.BDDMockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BookingReceiptServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private ImageProviderService imagesProviderService;

    @InjectMocks
    private BookingReceiptService bookingReceiptService;

//    @Test
    void createReceipt() throws JRException {
        UserDTO ownerDTO = UserDTO.builder()
                .id(UUID.randomUUID())
                .name("Owner")
                .build();
        RoomDTO roomDTO = RoomDTO.builder()
                .id(UUID.randomUUID())
                .name("Suite")
                .description("Lorem ipsum dolor sit amet")
                .owner(ownerDTO)
                .build();
        UserDTO userDTO = UserDTO.builder()
                .id(UUID.randomUUID())
                .name("Customer")
                .build();
        BookingDTO bookingDTO = BookingDTO.builder()
                .id(UUID.randomUUID().toString())
                .checkIn(OffsetDateTime.now().plusDays(1).toString())
                .checkOut(OffsetDateTime.now().plusDays(3).toString())
                .createdAt(OffsetDateTime.now().toString())
                .room(roomDTO)
                .user(userDTO)
                .build();

        byte[] report = new byte[0];

        given(reportService.generateReport(any(InputStream.class), anyMap())).willReturn(report);
        given(imagesProviderService.uploadFile(any(byte[].class), anyString())).willReturn("s3_url");

        String url = bookingReceiptService.createReceipt(bookingDTO);

        verify(reportService).generateReport(any(InputStream.class), anyMap());
        verify(imagesProviderService).uploadFile(eq(report), anyString());
        assertThat(url).isNotNull();
    }

}