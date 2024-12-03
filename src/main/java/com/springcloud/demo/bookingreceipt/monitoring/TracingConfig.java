package com.springcloud.demo.bookingreceipt.monitoring;

import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    public AWSXRayServletFilter tracingFilter() {
        return new AWSXRayServletFilter("receipts-ms");
    }
}
