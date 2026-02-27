package com.multibank.utils;

import com.multibank.config.FrameworkConfig;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for capturing and storing screenshots.
 */
@Slf4j
public class ScreenshotUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {}

    /**
     * Captures a screenshot and saves it to the configured screenshot directory.
     *
     * @param driver WebDriver instance
     * @param name   Descriptive name (test name, step name, etc.)
     * @return Absolute path to the saved screenshot, or empty string on failure
     */
    public static String capture(WebDriver driver, String name) {
        try {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String safeName = name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            String fileName = safeName + "_" + timestamp + ".png";

            Path dir = Paths.get(FrameworkConfig.getScreenshotDir());
            Files.createDirectories(dir);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = dir.resolve(fileName);
            Files.copy(src.toPath(), dest);

            log.info("Screenshot saved: {}", dest.toAbsolutePath());
            return dest.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to save screenshot: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Returns screenshot as base64 (useful for embedding in reports).
     */
    public static String captureAsBase64(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture base64 screenshot: {}", e.getMessage());
            return "";
        }
    }
}
