package com.inkwell.post.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStorageUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {

        try {
            File dir = new File(uploadDir);

            // 🔥 IMPORTANT FIX
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create upload directory");
                }
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destination = new File(dir, fileName);

            file.transferTo(destination);

            return "/uploads/posts/" + fileName;

        } catch (IOException e) {
            e.printStackTrace(); // 🔥 add this for debugging
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}