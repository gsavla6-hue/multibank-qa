package com.multibank.tests;

import com.aventstack.extentreports.Status;
import com.multibank.config.DriverFactory;
import com.multibank.config.FrameworkConfig;
import com.multibank.utils.ExtentReportManager;
import com.multibank.utils.ScreenshotUtil;
import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * Abstract base class for all test classes.
 * Manages driver lifecycle, reporting hooks, and screenshot-on-failure.
 *
 * Lifecycle order guaranteed by TestNG:
 *   @BeforeSuite  → globalSetup()
 *   @BeforeMethod → initDriver()   [parent, alwaysRun]
 *   @BeforeMethod → initPage()     [child,  alwaysRun] - driver is ready here
 *   @Test         → test method
 *   @AfterMethod  → tearDown()     [parent, alwaysRun]
 *   @AfterSuite   → globalTearDown()
 */
@Slf4j
public abstract class BaseTest {

    protected WebDriver driver;

    // ── Suite ────────────────────────────────────────────────────────────────

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        log.info("=== Test Suite Starting ===");
        ExtentReportManager.getInstance();
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        ExtentReportManager.flush();
        log.info("=== Test Suite Complete ===");
    }

    // ── Per-test driver init ─────────────────────────────────────────────────
    // Priority = 0 so this runs BEFORE child @BeforeMethod (priority = 1)

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void initDriver(@Optional("chrome") String browser, Method method) {
        String browserToUse = System.getProperty("browser", browser);
        log.info("--- Test: {} | Browser: {} ---", method.getName(), browserToUse);

        DriverFactory.initDriver(browserToUse);
        driver = DriverFactory.getDriver();

        var test = ExtentReportManager.getInstance()
                .createTest(method.getName())
                .assignCategory(this.getClass().getSimpleName());
        ExtentReportManager.setTest(test);
        test.log(Status.INFO, "Browser: " + browserToUse);
    }

    // ── Per-test teardown ────────────────────────────────────────────────────

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        var extentTest = ExtentReportManager.getTest();

        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("TEST FAILED: {}", result.getName());
            String path = ScreenshotUtil.capture(driver, result.getName());
            if (extentTest != null) {
                extentTest.fail(result.getThrowable());
                if (!path.isEmpty()) extentTest.addScreenCaptureFromPath(path, "Failure Screenshot");
            }
            saveScreenshot();
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("TEST SKIPPED: {}", result.getName());
            if (extentTest != null) extentTest.skip("Test skipped");
        } else {
            log.info("TEST PASSED: {}", result.getName());
            if (extentTest != null) extentTest.pass("Test passed");
        }

        DriverFactory.quitDriver();
        ExtentReportManager.removeTest();
    }

    // ── Allure attachment ────────────────────────────────────────────────────

    @Attachment(value = "Screenshot on failure", type = "image/png")
    private byte[] saveScreenshot() {
        try {
            return ((org.openqa.selenium.TakesScreenshot) driver)
                    .getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    // ── Helper for subclasses ────────────────────────────────────────────────

    protected void logStep(String message) {
        log.info("  > {}", message);
        var test = ExtentReportManager.getTest();
        if (test != null) test.log(Status.INFO, message);
    }
}
