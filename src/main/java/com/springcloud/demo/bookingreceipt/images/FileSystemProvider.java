package com.springcloud.demo.bookingreceipt.images;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnExpression("'${cloud.aws.s3.bucket.name}'.equals('filesystem')")
public class FileSystemProvider implements ImageProviderService {

    @Override
    public String uploadFile(byte[] file, String key) {
        try {
            Path folderPath = Paths.get("images").resolve("receipts");
            Files.createDirectories(folderPath);
            Path targetPath = Paths.get("images").resolve(key);
            Files.write(targetPath, file);
            return "http://localhost:8080/" + key;
        } catch (Exception e) {
            return null;
        }
    }
}
