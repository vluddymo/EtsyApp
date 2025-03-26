package com.etsyautomation.controller;

import com.etsyautomation.services.ImageProcessingService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:5173"})
public class ImageProcessingController {

    private final ImageProcessingService imageProcessingService;
    private static final String UPLOAD_FOLDER = System.getProperty("user.home") + "/Desktop/EtsyUploads/";

    public ImageProcessingController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    //  API to process an uploaded image
    @PostMapping("/process")
    public ResponseEntity<String> processImage(@RequestParam("file") MultipartFile file) {
        try {
            // Ensure upload folder exists
            Files.createDirectories(Paths.get(UPLOAD_FOLDER));

            // Preserve original file format
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest().body("❌ Invalid file name.");
            }

            File savedFile = new File(UPLOAD_FOLDER + originalFilename);
            file.transferTo(savedFile);

            // Process the image
            String result = imageProcessingService.processImage(savedFile);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error processing image: " + e.getMessage());
        }
    }

    //  API to check if the backend is running
    @GetMapping("/status")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok(Map.of("message", "✅ Images processed successfully!").toString());
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, String>>> listProcessedImages() {
        File outputDir = new File(System.getProperty("user.home") + "/Desktop/EtsyProcessed/");

        if (!outputDir.exists() || !outputDir.isDirectory()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Map<String, String>> imageList = new ArrayList<>();

        for (File subfolder : Objects.requireNonNull(outputDir.listFiles(File::isDirectory))) {
            for (File image : Objects.requireNonNull(subfolder.listFiles((dir, name) -> name.toLowerCase().endsWith("preview.jpeg") ))) {
                imageList.add(Map.of(
                        "filename", image.getName(),
                        "path", "/api/images/file/" + subfolder.getName() + "/" + image.getName(), // API-URL zum Abrufen der Datei,
                        "absolutePath", image.getPath().substring(0, image.getPath().lastIndexOf('/'))
                ));
            }
        }

        return ResponseEntity.ok(imageList);
    }

    @GetMapping("/file/{folder}/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String folder, @PathVariable String filename) {
        try {
            Path imagePath = Paths.get(System.getProperty("user.home") + "/Desktop/EtsyProcessed/", folder, filename);
            Resource fileResource = new UrlResource(imagePath.toUri());

            if (fileResource.exists() && fileResource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Stelle sicher, dass es für PNG auch funktioniert
                        .body(fileResource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}