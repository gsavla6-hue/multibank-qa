package com.multibank.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.multibank.config.FrameworkConfig;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton manager for ExtentReports.
 * Thread-safe ExtentTest storage via ThreadLocal.
 */
@Slf4j
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST_THREAD_LOCAL = new ThreadLocal<>();

    private static final String REPORT_NAME = "MultiBank_Test_Report";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ExtentReportManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            Path reportPath = Paths.get(FrameworkConfig.getReportDir())
                    .resolve(REPORT_NAME + "_" + timestamp + ".html");

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath.toString());
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("MultiBank QA Test Report");
            spark.config().setReportName("MultiBank Platform – Automation Results");
            spark.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", "mb.io/en");
            extent.setSystemInfo("Environment", "Production");
            extent.setSystemInfo("Browser", FrameworkConfig.getBrowser());
            extent.setSystemInfo("Tester", System.getProperty("user.name", "QA"));

            log.info("ExtentReports initialised → {}", reportPath.toAbsolutePath());
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return TEST_THREAD_LOCAL.get();
    }

    public static void setTest(ExtentTest test) {
        TEST_THREAD_LOCAL.set(test);
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed");
        }
    }

    public static void removeTest() {
        TEST_THREAD_LOCAL.remove();
    }
}
