package com.multibank.listeners;

import com.multibank.config.FrameworkConfig;
import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyser.
 * Automatically retries flaky tests up to the configured retry count.
 * Applied globally via the TestNG listener or per-test via @Test(retryAnalyzer=...).
 */
@Slf4j
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetries = FrameworkConfig.getRetryCount();
        if (retryCount < maxRetries) {
            retryCount++;
            log.warn("Retrying test '{}' — attempt {}/{}", result.getName(), retryCount, maxRetries);
            return true;
        }
        return false;
    }
}
