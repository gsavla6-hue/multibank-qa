package com.multibank.listeners;

import lombok.extern.slf4j.Slf4j;
import org.testng.IAnnotationTransformer;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Combined TestNG listener:
 *  - Applies RetryAnalyzer to every test automatically
 *  - Logs test lifecycle events
 */
@Slf4j
public class TestListener implements ITestListener, IAnnotationTransformer {

    // ── IAnnotationTransformer – auto-apply retry ─────────────────────────────

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }

    // ── ITestListener ─────────────────────────────────────────────────────────

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶ STARTING: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✔ PASSED:  {} ({}ms)", result.getName(), getDuration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✘ FAILED:  {} ({}ms) — {}", result.getName(),
                getDuration(result),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⊘ SKIPPED: {}", result.getName());
    }

    private long getDuration(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
