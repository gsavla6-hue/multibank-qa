package com.multibank.tests;

import com.multibank.config.FrameworkConfig;
import com.multibank.pages.HomePage;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Basic performance checks using the browser's Navigation Timing API.
 *
 * These are smoke-level performance gates — not a replacement for
 * dedicated load testing. Thresholds are configurable via test data.
 *
 * Metrics captured:
 *   - DOM Content Loaded (ms)
 *   - Full page load time (ms)
 *   - First contentful paint (ms) via PerformancePaintTiming API
 */
@Epic("MultiBank Trading Platform")
@Feature("Performance")
public class PerformanceTest extends BaseTest {

    private static final int DOM_CONTENT_LOADED_THRESHOLD_MS = 5_000;
    private static final int FULL_LOAD_THRESHOLD_MS          = 10_000;
    private static final int FCP_THRESHOLD_MS                = 3_000;

    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        homePage.open();
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "DOM Content Loaded time is within acceptable threshold")
    @Severity(SeverityLevel.MINOR)
    @Story("Page load performance")
    public void testDomContentLoadedTime() {
        Long dcl = getNavigationTimingMetric("domContentLoadedEventEnd")
                 - getNavigationTimingMetric("navigationStart");

        logStep("DOM Content Loaded: " + dcl + "ms (threshold: " + DOM_CONTENT_LOADED_THRESHOLD_MS + "ms)");

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(dcl)
                .as("DOM Content Loaded should be under %dms but was %dms",
                        DOM_CONTENT_LOADED_THRESHOLD_MS, dcl)
                .isLessThan(DOM_CONTENT_LOADED_THRESHOLD_MS);
        soft.assertAll();
    }

    @Test(description = "Full page load time is within acceptable threshold")
    @Severity(SeverityLevel.MINOR)
    @Story("Page load performance")
    public void testFullPageLoadTime() {
        Long loadTime = getNavigationTimingMetric("loadEventEnd")
                      - getNavigationTimingMetric("navigationStart");

        logStep("Full page load: " + loadTime + "ms (threshold: " + FULL_LOAD_THRESHOLD_MS + "ms)");

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(loadTime)
                .as("Full page load should be under %dms but was %dms",
                        FULL_LOAD_THRESHOLD_MS, loadTime)
                .isLessThan(FULL_LOAD_THRESHOLD_MS);
        soft.assertAll();
    }

    @Test(description = "First Contentful Paint is within acceptable threshold")
    @Severity(SeverityLevel.MINOR)
    @Story("Page load performance")
    public void testFirstContentfulPaint() {
        Long fcp = getFirstContentfulPaint();
        if (fcp == null) {
            logStep("FCP not available in this browser — skipping assertion");
            return;
        }

        logStep("First Contentful Paint: " + fcp + "ms (threshold: " + FCP_THRESHOLD_MS + "ms)");

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(fcp)
                .as("FCP should be under %dms but was %dms", FCP_THRESHOLD_MS, fcp)
                .isLessThan(FCP_THRESHOLD_MS);
        soft.assertAll();
    }

    @Test(description = "Captures and logs all Navigation Timing metrics")
    @Severity(SeverityLevel.TRIVIAL)
    @Story("Timing diagnostics")
    public void testLogAllTimingMetrics() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        @SuppressWarnings("unchecked")
        Map<String, Object> timing = (Map<String, Object>) js.executeScript(
                "var t = window.performance.timing; return {" +
                "  navigationStart:         t.navigationStart," +
                "  domainLookupStart:       t.domainLookupStart," +
                "  domainLookupEnd:         t.domainLookupEnd," +
                "  connectStart:            t.connectStart," +
                "  connectEnd:              t.connectEnd," +
                "  requestStart:            t.requestStart," +
                "  responseStart:           t.responseStart," +
                "  responseEnd:             t.responseEnd," +
                "  domInteractive:          t.domInteractive," +
                "  domContentLoadedEventEnd:t.domContentLoadedEventEnd," +
                "  loadEventEnd:            t.loadEventEnd" +
                "};"
        );

        long navStart = ((Number) timing.get("navigationStart")).longValue();
        logStep("── Navigation Timing Breakdown ──────────────────");
        timing.forEach((key, val) -> {
            if (!key.equals("navigationStart") && val instanceof Number) {
                long diff = ((Number) val).longValue() - navStart;
                if (diff > 0) logStep("  " + key + ": +" + diff + "ms");
            }
        });

        // Assert timing map was populated
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(timing).as("Timing map should not be empty").isNotEmpty();
        soft.assertAll();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private long getNavigationTimingMetric(String metric) {
        Object val = ((JavascriptExecutor) driver)
                .executeScript("return window.performance.timing." + metric + ";");
        return val == null ? 0L : ((Number) val).longValue();
    }

    private Long getFirstContentfulPaint() {
        try {
            Object result = ((JavascriptExecutor) driver).executeScript(
                    "var entries = performance.getEntriesByName('first-contentful-paint');" +
                    "return entries.length > 0 ? entries[0].startTime : null;"
            );
            return result == null ? null : ((Number) result).longValue();
        } catch (Exception e) {
            return null;
        }
    }
}
