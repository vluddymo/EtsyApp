package com.etsyautomation.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.etsyautomation.config.Constants.TEMP_UPLOAD_PATH;

@Component
public class PdfGenerator {

    public static File generateDownloadLinkPdf(File previewImage, String driveLink) throws IOException {
        // Ordner sicherstellen
        File uploadDir = new File(TEMP_UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();

        // === Preview Image ===
        PDImageXObject pdImage = PDImageXObject.createFromFile(previewImage.getAbsolutePath(), document);

        float imageWidth = pageWidth;
        float imageHeight = pageWidth * 3f / 2f;

        if (imageHeight > pageHeight) {
            imageHeight = pageHeight;
            imageWidth = pageHeight * 2f / 3f;
        }

        float x = (pageWidth - imageWidth) / 2f;
        float y = (pageHeight - imageHeight) / 2f;

        content.drawImage(pdImage, x, y, imageWidth, imageHeight);

        // === Overlay ===
        float overlayHeight = pageWidth /4f;
        float overlayY = 30f;
        content.setNonStrokingColor(new Color(140, 82, 255)); // #8c52ff
        content.addRect(0, overlayY, pageWidth / 2f, overlayHeight);
        content.fill();

        // === Logo ===

        float logoX = pageWidth / 4f - 40f;
        float logoY = 30f + overlayHeight -40f;
        float logoWidth = 80f;
        float logoHeight = 80f;
        PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/static/assets/250307_Bildmarke.png", document);
        content.drawImage(logo, logoX, logoY, logoWidth, logoHeight);

        // === Text ===
        String linkText = "Download here";
        content.beginText();
        content.setNonStrokingColor(Color.WHITE);
        content.setFont(PDType1Font.HELVETICA_BOLD, 32);
        float textX = 20f;
        float textY = overlayY + 60f;
        content.newLineAtOffset(textX, textY);
        content.showText(linkText);
        content.endText();
        content.close();

        // === Klickbaren Link einfügen ===
        PDAnnotationLink link = new PDAnnotationLink();
        PDRectangle linkPosition = new PDRectangle(textX, overlayY, pageWidth / 2f, overlayHeight); // Bereich des Links
        link.setRectangle(linkPosition);

        PDActionURI action = new PDActionURI();
        action.setURI(driveLink);
        link.setAction(action);

        page.getAnnotations().add(link);

        // === Speichern ===
        String driveFolderId = driveLink.substring(driveLink.lastIndexOf("/") + 1);
        File output = new File(TEMP_UPLOAD_PATH + "download-link.pdf");
        document.save(output);
        document.close();

        return output;
    }
}
