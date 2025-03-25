package com.etsyautomation.controller;

import com.etsyautomation.services.GoogleDriveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:3000")
public class ImageUploadController {

    private static final String TEMP_UPLOAD_PATH = System.getProperty("java.io.tmpdir") + "/etsy-upload/";
    private final GoogleDriveService googleDriveService;

    public ImageUploadController(GoogleDriveService googleDriveService) {
        this.googleDriveService = googleDriveService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            File uploadDir = new File(TEMP_UPLOAD_PATH);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            List<File> tempFiles = new ArrayList<>();

            for (MultipartFile multipartFile : files) {
                File tempFile = new File(TEMP_UPLOAD_PATH + multipartFile.getOriginalFilename());
                multipartFile.transferTo(tempFile);
                tempFiles.add(tempFile);
            }

            // Upload alle Dateien auf einmal
            googleDriveService.uploadFiles(tempFiles);

            return ResponseEntity.ok("✅ Uploaded " + files.length + " file(s) to Google Drive.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Upload failed: " + e.getMessage());
        }
    }
}