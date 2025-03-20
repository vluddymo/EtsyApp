package com.etsyautomation.services;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageTypeSpecifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.w3c.dom.Element;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class ImageProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessingService.class);
    private static final String OUTPUT_FOLDER = System.getProperty("user.home") + "/Desktop/EtsyProcessed/";

    private static final List<AspectRatio> TARGET_FORMATS = List.of(
            new AspectRatio(5, 7, 5000, 7000, "_5x7", "jpeg", 300),
            new AspectRatio(4, 5, 4000, 5000, "_4x5", "jpeg", 300),
            new AspectRatio(3, 4, 4500, 6000, "_3x4", "jpeg", 300),
            new AspectRatio(2, 3, 4000, 6000, "_2x3", "jpeg", 300),
            new AspectRatio(2, 3, 4000, 6000, "_2x3_png", "png", 300),
            new AspectRatio(11, 14, 5500, 7000, "_11x14", "jpeg", 300),
            new AspectRatio(2, 3, 3000, 4500, "_2x3_preview", "jpeg", 72)
    );

    public String processImage(File inputFile) throws IOException {
        logger.info("🔄 Starting image processing: {}", inputFile.getAbsolutePath());

        if (!inputFile.exists()) {
            logger.error("❌ File does not exist: {}", inputFile.getAbsolutePath());
            return "File not found.";
        }

        // Create a subfolder for this image
        String imageNameWithoutExt = inputFile.getName().replaceFirst("\\.[^.]+$", ""); // Remove only the last extension
        String imageOutputFolder = OUTPUT_FOLDER + imageNameWithoutExt + "/";
        Files.createDirectories(Paths.get(imageOutputFolder));

        long fileSize = inputFile.length();
        logger.info("📏 File size: {} bytes", fileSize);

        logger.info("📡 Attempting to read image with OpenCV: {}", inputFile.getAbsolutePath());
        Mat matImage = opencv_imgcodecs.imread(inputFile.getAbsolutePath());

        if (matImage.empty()) {
            logger.error("❌ OpenCV could not read the image.");
            return "Error: OpenCV failed to read the image.";
        }

        logger.info("✅ OpenCV successfully loaded image with dimensions: {}x{}", matImage.cols(), matImage.rows());

        BufferedImage image = matToBufferedImage(matImage);
        logger.info("📏 Original Image dimensions: {}x{}", image.getWidth(), image.getHeight());

        for (AspectRatio format : TARGET_FORMATS) {
            try {
                logger.info("🔧 Processing format: {}", format.nameSuffix);
                double targetAspectRatio = (double) format.widthRatio / format.heightRatio;

                // Crop the image while maintaining the target aspect ratio
                BufferedImage croppedImage = cropToAspectRatio(image, targetAspectRatio);

                String outputFilePath = imageOutputFolder + imageNameWithoutExt + format.nameSuffix + "." + format.outputFormat;
                logger.info("💾 Saving file to {}", outputFilePath);

                saveImage(croppedImage, outputFilePath, format.outputFormat, format.dpi, format.targetWidth, format.targetHeight);
                logger.info("✅ Successfully saved {}", outputFilePath);
            } catch (Exception e) {
                logger.error("❌ Error processing format {}: {}", format.nameSuffix, e.getMessage());
            }
        }

        return "✅ Images processed successfully!";
    }

    private BufferedImage cropToAspectRatio(BufferedImage image, double targetAspectRatio) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        double originalAspectRatio = (double) originalWidth / originalHeight;

        int newWidth, newHeight;
        if (originalAspectRatio > targetAspectRatio) {
            // Crop width
            newHeight = originalHeight;
            newWidth = (int) (originalHeight * targetAspectRatio);
        } else {
            // Crop height
            newWidth = originalWidth;
            newHeight = (int) (originalWidth / targetAspectRatio);
        }

        int x = (originalWidth - newWidth) / 2;
        int y = (originalHeight - newHeight) / 2;
        logger.info("📏 Cropping to {}x{} (x={}, y={})", newWidth, newHeight, x, y);
        return image.getSubimage(x, y, newWidth, newHeight);
    }

    private void saveJPEGWithDPI(BufferedImage image, File file, int dpi) throws IOException {
        // Convert DPI to pixels per meter (DPI * 39.3701)
        int dpiPixelsPerMeter = (int) (dpi * 39.3701);

        // Set JPEG DPI metadata
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        try (ImageOutputStream output = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(output);

            IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
            Element tree = (Element) metadata.getAsTree("javax_imageio_jpeg_image_1.0");
            Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
            jfif.setAttribute("Xdensity", Integer.toString(dpi));
            jfif.setAttribute("Ydensity", Integer.toString(dpi));
            jfif.setAttribute("resUnits", "1");  // 1 = DPI (dots per inch)

            metadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);

            writer.write(metadata, new IIOImage(image, null, metadata), param);
            writer.dispose();
        }
    }

    private void savePNGWithDPI(BufferedImage image, File file, int dpi) throws IOException {
        int dpiPixelsPerMeter = (int) (dpi * 39.3701); // Convert DPI to pixels per meter

        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        try (ImageOutputStream output = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(output);

            IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(newImage), param);
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree("javax_imageio_1.0");

            // Use the utility method from ImageUtils
            IIOMetadataNode dimensionNode = getOrCreateChildNode(root, "Dimension");
            IIOMetadataNode horizontalNode = getOrCreateChildNode(dimensionNode, "HorizontalPixelSize");
            IIOMetadataNode verticalNode = getOrCreateChildNode(dimensionNode, "VerticalPixelSize");

            // Set DPI metadata values
            horizontalNode.setAttribute("value", String.valueOf(dpiPixelsPerMeter));
            verticalNode.setAttribute("value", String.valueOf(dpiPixelsPerMeter));

            // Update metadata tree and save image
            metadata.setFromTree("javax_imageio_1.0", root);
            writer.write(metadata, new IIOImage(newImage, null, metadata), param);
            writer.dispose();
        }
    }

    private void saveImage(BufferedImage image, String path, String format, int dpi, int width, int height) throws IOException {
        File outputFile = new File(path);

        // Use Thumbnailator for resizing
        BufferedImage resizedImage = Thumbnails.of(image)
                .size(width, height)
                .asBufferedImage();  // Generate BufferedImage instead of directly saving

        // Set DPI metadata for the output image
        if (format.equalsIgnoreCase("jpeg") || format.equalsIgnoreCase("jpg")) {
            saveJPEGWithDPI(resizedImage, outputFile, dpi);
        } else if (format.equalsIgnoreCase("png")) {
            savePNGWithDPI(resizedImage, outputFile, dpi);
        } else {
            logger.warn("⚠ Unsupported format: {} - Saving without DPI metadata", format);
            ImageIO.write(resizedImage, format, outputFile);
        }

        logger.info("✅ Image saved with {} DPI at: {}", dpi, path);
    }

    private IIOMetadataNode getOrCreateChildNode(IIOMetadataNode parent, String nodeName) {
        IIOMetadataNode child = null;
        for (int i = 0; i < parent.getLength(); i++) {
            if (parent.item(i).getNodeName().equals(nodeName)) {
                child = (IIOMetadataNode) parent.item(i);
                break;
            }
        }
        if (child == null) {
            child = new IIOMetadataNode(nodeName);
            parent.appendChild(child);
        }
        return child;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        try {
            File tempFile = File.createTempFile("opencv_temp", ".png");
            opencv_imgcodecs.imwrite(tempFile.getAbsolutePath(), mat);
            return ImageIO.read(tempFile);
        } catch (IOException e) {
            logger.error("❌ Failed to convert Mat to BufferedImage", e);
            return null;
        }
    }

    private static class AspectRatio {
        int widthRatio, heightRatio, targetWidth, targetHeight, dpi;
        String nameSuffix, outputFormat;

        public AspectRatio(int widthRatio, int heightRatio, int targetWidth, int targetHeight, String nameSuffix, String outputFormat, int dpi) {
            this.widthRatio = widthRatio;
            this.heightRatio = heightRatio;
            this.targetWidth = targetWidth;
            this.targetHeight = targetHeight;
            this.nameSuffix = nameSuffix;
            this.outputFormat = outputFormat;
            this.dpi = dpi;
        }
    }
}