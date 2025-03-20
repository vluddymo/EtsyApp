package com.etsyautomation.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executors;

@Service
public class FolderWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(FolderWatcherService.class);
    private static final String WATCH_FOLDER = System.getProperty("user.home") + "/Desktop/Transform_Booth";

    private final ImageProcessingService imageProcessingService;

    public FolderWatcherService(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
        startWatching();
    }

    private void startWatching() {
        Executors.newSingleThreadExecutor().submit(this::watchFolder);
    }

    private void watchFolder() {
        Path folderPath = Paths.get(WATCH_FOLDER);
        try {
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
                logger.info("✅ Created watch folder: {}", WATCH_FOLDER);
            }

            WatchService watchService = FileSystems.getDefault().newWatchService();
            folderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            logger.info("📂 Watching folder: {}", WATCH_FOLDER);

            while (!Thread.currentThread().isInterrupted()) { // Allows graceful exit
                WatchKey key;
                try {
                    key = watchService.take(); // Blocks until an event occurs
                } catch (InterruptedException e) {
                    logger.warn("⚠️ Folder watcher interrupted, shutting down...");
                    Thread.currentThread().interrupt(); // Re-set interrupt flag before exiting
                    break;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        File file = new File(WATCH_FOLDER, event.context().toString());
                        if (isImageFile(file)) {
                            logger.info("🖼 New image detected: {}", file.getName());
                            processImageFile(file);
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    logger.error("❌ Watch key no longer valid, stopping folder watcher.");
                    break; // Exit loop if the key is invalid
                }
            }
        } catch (IOException e) {
            logger.error("❌ Error watching folder: {}", WATCH_FOLDER, e);
        }
    }

    private boolean isImageFile(File file) {
        String[] imageExtensions = {"jpg", "jpeg", "png"};
        for (String ext : imageExtensions) {
            if (file.getName().toLowerCase().endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }

    private void processImageFile(File file) {
        try {
            logger.info("🔄 Processing file: {}", file.getAbsolutePath());

            if (!file.exists()) {
                logger.error("❌ File not found: {}", file.getAbsolutePath());
                return;
            }

            // Call the image processing service
            String result = imageProcessingService.processImage(file);
            logger.info("✅ Image processing completed: {}", result);

            // Move the file after processing
            moveFileToProcessedFolder(file);
        } catch (IOException e) {
            logger.error("❌ Error processing file: {}", file.getName(), e);
        }
    }

    private void moveFileToProcessedFolder(File file) {
        File processedFolder = new File(WATCH_FOLDER, "processed");

        // Ensure processed folder exists
        if (!processedFolder.exists() && !processedFolder.mkdirs()) {
            logger.error("❌ Failed to create processed folder: {}", processedFolder.getAbsolutePath());
            return; // Stop if folder creation fails
        }

        File newLocation = new File(processedFolder, file.getName());
        if (file.renameTo(newLocation)) {
            logger.info("✅ Moved processed image to: {}", newLocation.getAbsolutePath());
        } else {
            logger.warn("⚠️ Failed to move file: {}", file.getName());
        }
    }
}