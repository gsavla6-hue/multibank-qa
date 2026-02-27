package com.multibank.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralised configuration loader.
 * Reads from config.properties; any value can be overridden at runtime via
 * system properties (e.g. -Dbrowser=firefox).
 */
@Slf4j
public class FrameworkConfig {

    private static final Properties PROPERTIES = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream in = FrameworkConfig.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException("Cannot find " + CONFIG_FILE + " on the classpath");
            }
            PROPERTIES.load(in);
            log.info("Configuration loaded from {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load framework configuration", e);
        }
    }

    private FrameworkConfig() {}

    // ── Getters ─────────────────────────────────────────────────────────────

    public static String getBaseUrl() {
        return get("base.url");
    }

    public static String getBrowser() {
        return get("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static int getImplicitWait() {
        return Integer.parseInt(get("implicit.wait.seconds"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(get("explicit.wait.seconds"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(get("page.load.timeout.seconds"));
    }

    public static String getScreenshotDir() {
        return get("screenshot.dir");
    }

    public static String getReportDir() {
        return get("report.dir");
    }

    public static int getRetryCount() {
        return Integer.parseInt(get("retry.count"));
    }

    // ── Internal ─────────────────────────────────────────────────────────────

    /**
     * System properties override file properties – useful for CI pipelines.
     */
    private static String get(String key) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp.trim();
        }
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing required config key: " + key);
        }
        return value.trim();
    }
}
