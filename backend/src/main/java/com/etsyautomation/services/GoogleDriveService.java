package com.etsyautomation.services;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "Etsy Image Uploader";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String GOOGLE_DRIVE_FOLDER_ID = "1Oy_rsIerRNCdSugpJQUFbC1B0TIk4Ou3";

    private final Drive driveService;

    public GoogleDriveService() throws GeneralSecurityException, IOException {
        this.driveService = initializeDriveService();
    }

    private Drive initializeDriveService() throws GeneralSecurityException, IOException {
        String jsonTemplate = Files.readString(Paths.get("src/main/resources/credentials.json"));
        jsonTemplate = jsonTemplate
                .replace("${PROJECT_ID}", System.getenv("PROJECT_ID"))
                .replace("${PRIVATE_KEY_ID}", System.getenv("PRIVATE_KEY_ID"))
                .replace("${PRIVATE_KEY}", System.getenv("PRIVATE_KEY").replace("\\n", "\\n"))
                .replace("${CLIENT_EMAIL}", System.getenv("CLIENT_EMAIL"))
                .replace("${CLIENT_ID}", System.getenv("CLIENT_ID"));

        System.out.println("Ersetzte Credentials: " + jsonTemplate);

        ByteArrayInputStream credentialsStream = new ByteArrayInputStream(jsonTemplate.getBytes(StandardCharsets.UTF_8));
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singletonList(DriveScopes.DRIVE));

        return new Drive.Builder(new NetHttpTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void uploadFile(java.io.File filePath, String parentFolderId) {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(filePath.getName());
            fileMetadata.setParents(Collections.singletonList(parentFolderId));

            String mimeType = Files.probeContentType(filePath.toPath());
            FileContent mediaContent = new FileContent(mimeType != null ? mimeType : "application/octet-stream", filePath);

            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, name, webViewLink")
                    .execute();

            System.out.println("✅ Uploaded file ID: " + uploadedFile.getId());
            System.out.println("🔗 View: " + uploadedFile.getWebViewLink());

        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to upload file: " + filePath.getAbsolutePath(), e);
        }
    }

    public void uploadFiles(List<java.io.File> filenames) {
        for (java.io.File filePath: filenames) {
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
    }
    public String createOrGetDriveFolder(String folderName) throws IOException {
        String query = String.format("mimeType='application/vnd.google-apps.folder' and name='%s' and trashed=false and '%s' in parents", folderName, GOOGLE_DRIVE_FOLDER_ID);

        FileList result = driveService.files().list()
                .setQ(query)
                .setFields("files(id, name)")
                .execute();

        List<File> folders = result.getFiles();
        if (folders != null && !folders.isEmpty()) {
            return folders.get(0).getId(); // folder already exists
        }

        File metadata = new File();
        metadata.setName(folderName);
        metadata.setMimeType("application/vnd.google-apps.folder");
        metadata.setParents(Collections.singletonList(GOOGLE_DRIVE_FOLDER_ID));

        File folder = driveService.files().create(metadata)
                .setFields("id")
                .execute();

        return folder.getId(); // return new folder ID
    }

}
