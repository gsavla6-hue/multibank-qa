package com.multibank.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Thread-safe WebDriver factory using ThreadLocal.
 * Supports Chrome, Firefox, and Edge; headless mode configurable.
 */
@Slf4j
public class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver getDriver() {
        if (DRIVER_THREAD_LOCAL.get() == null) {
            initDriver(FrameworkConfig.getBrowser());
        }
        return DRIVER_THREAD_LOCAL.get();
    }

    public static void initDriver(String browser) {
        WebDriver driver;
        boolean headless = FrameworkConfig.isHeadless();
        String remoteUrl = System.getProperty("selenium.remote.url", "");
        log.info("Initialising {} driver (headless={}, remote={})", browser, headless,
                remoteUrl.isEmpty() ? "false" : remoteUrl);

        switch (browser.toLowerCase().trim()) {
            case "firefox":
                FirefoxOptions ffOpts = new FirefoxOptions();
                if (headless) ffOpts.addArguments("--headless");
                if (!remoteUrl.isEmpty()) {
                    driver = buildRemote(remoteUrl, ffOpts);
                } else {
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver(ffOpts);
                }
                break;

            case "edge":
                EdgeOptions edgeOpts = new EdgeOptions();
                if (headless) edgeOpts.addArguments("--headless");
                if (!remoteUrl.isEmpty()) {
                    driver = buildRemote(remoteUrl, edgeOpts);
                } else {
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver(edgeOpts);
                }
                break;

            case "chrome":
            default:
                ChromeOptions chromeOpts = new ChromeOptions();
                if (headless) chromeOpts.addArguments("--headless=new");
                chromeOpts.addArguments(
                        "--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--disable-gpu",
                        "--window-size=1920,1080",
                        "--disable-extensions",
                        "--disable-popup-blocking"
                );
                if (!remoteUrl.isEmpty()) {
                    driver = buildRemote(remoteUrl, chromeOpts);
                } else {
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver(chromeOpts);
                }
                break;
        }

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(FrameworkConfig.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(FrameworkConfig.getPageLoadTimeout()));
        driver.manage().window().maximize();

        DRIVER_THREAD_LOCAL.set(driver);
        log.info("Driver created successfully for thread {}", Thread.currentThread().getId());
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            log.info("Quitting driver for thread {}", Thread.currentThread().getId());
            driver.quit();
            DRIVER_THREAD_LOCAL.remove();
        }
    }

    private static RemoteWebDriver buildRemote(String remoteUrl,
                                                org.openqa.selenium.MutableCapabilities caps) {
        try {
            log.info("Connecting to Selenium Grid at {}", remoteUrl);
            return new RemoteWebDriver(new URL(remoteUrl), caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL: " + remoteUrl, e);
        }
    }
}
