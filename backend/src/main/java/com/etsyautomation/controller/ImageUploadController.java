package com.etsyautomation.controller;

import com.etsyautomation.services.GoogleDriveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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
    public ResponseEntity<String> uploadByFolderMetadata(@RequestBody List<String> paths) {
        try {
            for (String path : paths) {
                File localFolder = new File(path);
                String folderName = localFolder.getName(); // e.g., "coolPigletAndShapes"
                String driveFolderId = googleDriveService.createOrGetDriveFolder(folderName);

                File[] allFiles = localFolder.listFiles();

                if (allFiles != null) {
                    for (File file : allFiles) {
                        googleDriveService.uploadFile(file, driveFolderId); // now needs drive folder ID
                    }
                }
            }

            return ResponseEntity.ok("✅ All folders uploaded!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Upload failed: " + e.getMessage());
        }
    }
}