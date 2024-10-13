package com.springcloud.demo.bookingreceipt.receipt.service;

import com.springcloud.demo.bookingreceipt.client.booking.dto.BookingDTO;
import com.springcloud.demo.bookingreceipt.client.rooms.dto.RoomDTO;
import com.springcloud.demo.bookingreceipt.client.users.dto.UserDTO;
import com.springcloud.demo.bookingreceipt.images.ImageProviderService;
import com.springcloud.demo.bookingreceipt.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingReceiptService {

    private final ReportService reportService;
    private final ImageProviderService imagesProviderService;

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    public String createReceipt(BookingDTO bookingDTO, RoomDTO roomDTO, UserDTO ownerDTO, UserDTO userDTO) {
        Map<String, Object> params = new HashMap<>();

        params.put("current_date", DateUtils.formatDate(LocalDateTime.parse(bookingDTO.getCreatedAt())));
        params.put("room_name", roomDTO.getName() == null ? roomDTO.getNum().toString() : roomDTO.getName());
        params.put("room_description", roomDTO.getDescription() == null ? "Sin descripción" : roomDTO.getDescription());
        params.put("room_owner", ownerDTO.getName());
        params.put("check_in", DateUtils.formatDate(LocalDateTime.parse(bookingDTO.getCheckIn())));
        params.put("check_out", DateUtils.formatDate(LocalDateTime.parse(bookingDTO.getCheckOut())));
        params.put("user_name", userDTO.getName());
        params.put("check_img", getClass().getResourceAsStream("/templates/static/check.svg"));
        params.put("logo_img", getClass().getResourceAsStream("/templates/static/spring-logo.svg"));

        InputStream template = getClass().getResourceAsStream("/templates/Receipt.jrxml");
        if(template == null){
            log.error("Template not found");
            return null;
        }

        byte [] report = reportService.generateReport(template,params);

        String key = imagesProviderService.uploadFile(report, "receipts/" + bookingDTO.getId() + ".pdf");
        return "https://" + bucketName + ".s3.sa-east-1.amazonaws.com/" + key;
    }
}
