package com.etsyautomation.services;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "Etsy Image Uploader";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String GOOGLE_DRIVE_FOLDER_ID = "1Oy_rsIerRNCdSugpJQUFbC1B0TIk4Ou3"; // Replace with your folder ID

    private final Drive driveService;

    public GoogleDriveService() throws GeneralSecurityException, IOException {
        this.driveService = initializeDriveService();
    }

    private Drive initializeDriveService() throws GeneralSecurityException, IOException {
        // ✅ Debug: Check if credentials file is loaded and non-empty
        InputStream credentialsStream = new ClassPathResource("credentials.json").getInputStream();
        System.out.println("✅ Loaded credentials.json: " + credentialsStream.available() + " bytes");

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singletonList(DriveScopes.DRIVE));

        return new Drive.Builder(new NetHttpTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void uploadFile(java.io.File filePath) {
        try {
            System.out.println("📤 Starting upload for: " + filePath.getAbsolutePath());

            File fileMetadata = new File();
            fileMetadata.setName(filePath.getName());
            fileMetadata.setParents(Collections.singletonList(GOOGLE_DRIVE_FOLDER_ID));

            String mimeType = Files.probeContentType(filePath.toPath());
            System.out.println("📦 Detected MIME type: " + mimeType);

            FileContent mediaContent = new FileContent(mimeType != null ? mimeType : "application/octet-stream", filePath);

            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, name, webViewLink")
                    .execute();

            System.out.println("✅ Uploaded file ID: " + uploadedFile.getId());
            System.out.println("🔗 View: " + uploadedFile.getWebViewLink());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Failed to upload file: " + filePath.getAbsolutePath() + " → " + GOOGLE_DRIVE_FOLDER_ID, e);
        }
    }

    public void uploadFiles(List<String> filenames) {
        for (String filename : filenames) {
            java.io.File fileToUpload = new java.io.File(System.getProperty("user.home") + "/Desktop/EtsyProcessed/" + "coolPigletAndShapes/" +filename);
            System.out.println(fileToUpload.getAbsolutePath());
            if (fileToUpload.exists()) {
                uploadFile(fileToUpload);
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        }
    }
}
