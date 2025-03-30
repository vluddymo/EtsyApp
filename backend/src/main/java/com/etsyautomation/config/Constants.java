package com.etsyautomation.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {

    public static final String TEMP_UPLOAD_PATH = System.getProperty("java.io.tmpdir") + "/etsy-upload/";
}
