package com.springcloud.demo.bookingreceipt.images;

public interface ImageProviderService {
    public String uploadFile(byte[] file, String key);
}
