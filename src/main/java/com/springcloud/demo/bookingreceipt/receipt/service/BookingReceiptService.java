package com.springcloud.demo.bookingreceipt.receipt.service;

import com.springcloud.demo.bookingreceipt.client.booking.dto.BookingDTO;
import com.springcloud.demo.bookingreceipt.images.ImageProviderService;
import com.springcloud.demo.bookingreceipt.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.OffsetDateTime;
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

    public String createReceipt(BookingDTO bookingDTO) {
        Map<String, Object> params = new HashMap<>();

        params.put("current_date", DateUtils.formatDate(OffsetDateTime.parse(bookingDTO.getCreatedAt())));
        params.put("room_name", bookingDTO.getRoom().getName() == null ? bookingDTO.getRoom().getNum().toString() : bookingDTO.getRoom().getName());
        params.put("room_description", bookingDTO.getRoom().getDescription() == null ? "Sin descripción" : bookingDTO.getRoom().getDescription());
        params.put("room_owner", bookingDTO.getRoom().getOwner().getName());
        params.put("check_in", DateUtils.formatDate(OffsetDateTime.parse(bookingDTO.getCheckIn())));
        params.put("check_out", DateUtils.formatDate(OffsetDateTime.parse(bookingDTO.getCheckOut())));
        params.put("user_name", bookingDTO.getUser().getName());
        params.put("check_img", getClass().getResourceAsStream("/templates/static/check.svg"));
        params.put("logo_img", getClass().getResourceAsStream("/templates/static/spring-logo.svg"));

        InputStream template = getClass().getResourceAsStream("/templates/Receipt.jrxml");
        if(template == null){
            log.error("Template not found");
            return null;
        }

        byte [] report = reportService.generateReport(template,params);

        return imagesProviderService.uploadFile(report, "receipts/" + bookingDTO.getId() + ".pdf");
    }
}
