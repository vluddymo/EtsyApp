package com.etsyautomation.utils;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import java.io.File;

public class OpenCVTest {
    public static void main(String[] args) {
        String imagePath = "/Users/vluddy/Desktop/Transform_Booth/test.jpg";

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.out.println("❌ Image file does not exist!");
            return;
        }

        System.out.println("📡 Attempting to load image with OpenCV...");

        Mat matImage = opencv_imgcodecs.imread(imagePath);

        if (matImage.empty()) {
            System.out.println("❌ OpenCV failed to load the image!");
        } else {
            System.out.println("✅ OpenCV successfully loaded the image! Dimensions: " + matImage.cols() + "x" + matImage.rows());
        }
    }
}
