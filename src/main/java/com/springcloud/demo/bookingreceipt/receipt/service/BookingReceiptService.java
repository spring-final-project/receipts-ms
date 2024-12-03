package com.springcloud.demo.bookingreceipt.receipt.service;

import com.springcloud.demo.bookingreceipt.client.booking.dto.BookingDTO;
import com.springcloud.demo.bookingreceipt.images.ImageProviderService;
import com.springcloud.demo.bookingreceipt.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
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

    public String createReceipt(BookingDTO bookingDTO) throws JRException {
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

//        JasperDesign jd = JRXmlLoader.load(getClass().getResourceAsStream("/templates/Receipt.jrxml"));
//        System.out.println("jd = " + jd);

        InputStream template = getClass().getClassLoader().getResourceAsStream("templates/Receipt.jrxml");
        System.out.println("template = " + template);
        if(template == null){
            System.out.println("No encontró template con getClass().getClassLoader().getResourceAsStream(\"templates/Receipt.jrxml\")");
            template = getClass().getResourceAsStream("/templates/Receipt.jrxml");
        }

        if(template == null){
            System.out.println("No encontró template con getClass().getResourceAsStream(\"/templates/Receipt.jrxml\")");
            log.error("Template not found");
            return null;
        }

        System.out.println("template = " + template);

        byte [] report = reportService.generateReport(template,params);

        System.out.println("bytes generated of report");

        return imagesProviderService.uploadFile(report, "receipts/" + bookingDTO.getId() + ".pdf");
    }
}

//{
//        "eventSource": "aws:kafka",
//        "eventSourceArn": "arn:aws:kafka:us-east-1:123456789012:cluster/MyKafkaCluster/abcdefg-hijklmno",
//        "records": {
//        "BOOKING_CREATED_TOPIC-0": [
//        {
//        "topic": "BOOKING_CREATED_TOPIC",
//        "partition": 0,
//        "offset": 0,
//        "timestamp": 1698243200000,
//        "timestampType": "CREATE_TIME",
//        "key": null,
//        "value": "eyJpZCI6IjU2NTU5YjZhLTZmNjEtNGQ4ZS1hY2U4LWI1YmQ0NTBjNjZlMyIsImNyZWF0ZWRBdCI6IjIwMjQtMTItMDJUMTk6NTg6MjMuODcyNjk3LTAzOjAwIiwicmVjZWlwdFVybCI6bnVsbCwiY2hlY2tJbiI6IjIwMjQtMTItMDNUMTI6MDAtMDM6MDAiLCJjaGVja091dCI6IjIwMjQtMTItMDRUMTI6MDAtMDM6MDAiLCJzdGF0dXMiOiJCT09LRUQiLCJyYXRpbmciOm51bGwsInJldmlldyI6bnVsbCwidXNlciI6eyJpZCI6IjFmMzcxM2NhLTY2ZDctNGExMC1iNGI1LTI5NDg1YzkzMWJkYyIsImVtYWlsIjoiZ29uemFsb2plcmV6bkBnbWFpbC5jb20iLCJuYW1lIjoiR29uemEifSwicm9vbSI6eyJpZCI6IjAzYWM1NDQzLTUyZTYtNGNhOC05Y2I1LWU5YjJlMmIwZTkwNiIsIm51bSI6MSwibmFtZSI6IlN1aXRlIiwiZmxvb3IiOm51bGwsIm1heENhcGFjaXR5Ijo0LCJkZXNjcmlwdGlvbiI6bnVsbCwib3duZXJJZCI6IjI2YTdhOWI5LWEzZjktNDIxNy04Y2ZjLWZmYWFhMzI2ZmMyNCIsImltYWdlcyI6WyJodHRwczovL3NwcmluZy1maW5hbC1wcm9qZWN0LXJvb21zLnMzLnNhLWVhc3QtMS5hbWF6b25hd3MuY29tL3Jvb21zLzAzYWM1NDQzLTUyZTYtNGNhOC05Y2I1LWU5YjJlMmIwZTkwNi9kMDIwMWQ5MC02MjhhLTQ1OWYtYWVhNy0wOWFmYjMyZjY2ZDUuanBnIiwiaHR0cHM6Ly9zcHJpbmctZmluYWwtcHJvamVjdC1yb29tcy5zMy5zYS1lYXN0LTEuYW1hem9uYXdzLmNvbS9yb29tcy8wM2FjNTQ0My01MmU2LTRjYTgtOWNiNS1lOWIyZTJiMGU5MDYvMWVjNTU3NTEtMmE5OS00ZGNkLTliMTAtOWQ3MDZmODhjMWNlLmpwZyJdLCJzaW1wbGVCZWRzIjowLCJtZWRpdW1CZWRzIjowLCJkb3VibGVCZWRzIjoyLCJvd25lciI6eyJpZCI6IjI2YTdhOWI5LWEzZjktNDIxNy04Y2ZjLWZmYWFhMzI2ZmMyNCIsImVtYWlsIjoiamVyZXouZGV2ZWxvcG1lbnRAZ21haWwuY29tIiwibmFtZSI6IkdvbnphIn19fQ==",
//        "headers": []
//        }
//        ]
//        }
//        }