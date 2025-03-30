package com.etsyautomation.controller;

import com.etsyautomation.services.GoogleDriveService;
import com.etsyautomation.utils.PdfGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:3000")
public class ImageUploadController {

    private final GoogleDriveService googleDriveService;

    public ImageUploadController(GoogleDriveService googleDriveService, PdfGenerator pdfGenerator) {
        this.googleDriveService = googleDriveService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadByFolderMetadata(@RequestBody List<String> paths) {
        try {
            for (String path : paths) {
                File localFolder = new File(path);
                String folderName = localFolder.getName(); // e.g., "coolPigletAndShapes"
                String driveFolderId = googleDriveService.createOrGetDriveFolder(folderName);
                String driveFolderLink = "https://drive.google.com/drive/folders/" + driveFolderId;

                File[] allFiles = localFolder.listFiles();

                if (allFiles != null) {
                    for (File file : allFiles) {
                        googleDriveService.uploadFile(file, driveFolderId); // Upload all files
                    }

                    // PREVIEW FILE FINDEN
                    File preview = Arrays.stream(allFiles)
                            .filter(f -> f.getName().contains("preview"))
                            .findFirst()
                            .orElse(null);

                    if (preview != null) {
                        // PDF generieren
                        File pdf = PdfGenerator.generateDownloadLinkPdf(preview, driveFolderLink);

                        // PDF hochladen
                        googleDriveService.uploadFile(pdf, driveFolderId);
                    }
                }
            }

            return ResponseEntity.ok("✅ All folders and PDFs uploaded!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Upload failed: " + e.getMessage());
        }
    }
}