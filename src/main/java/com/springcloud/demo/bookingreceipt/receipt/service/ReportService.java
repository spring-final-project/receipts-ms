package com.springcloud.demo.bookingreceipt.receipt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    byte[] generateReport(InputStream template, Map<String, Object> params) {

        try {
            System.out.println("--- generateReport ---");
            System.out.println("template = " + template);
            System.out.println("template.toString() = " + template.toString());
            JasperReport jasperReport = JasperCompileManager.compileReport(template);
            System.out.println("--- compiled ---");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
            System.out.println("--- filled ---");
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
