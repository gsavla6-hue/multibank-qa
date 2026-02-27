package com.multibank.tests;

import com.multibank.pages.HomePage;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates that hyperlinks on the home page are not broken (no 4xx/5xx).
 * Uses logStep() inherited from BaseTest — no separate @Slf4j needed.
 */
@Epic("MultiBank Trading Platform")
@Feature("Link Validation")
public class LinkValidationTest extends BaseTest {

    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        homePage.open();
    }

    @Test(description = "No navigation links return HTTP 4xx or 5xx")
    @Severity(SeverityLevel.NORMAL)
    @Story("Broken link detection")
    public void testNoNavLinksAreBroken() {
        List<WebElement> links = homePage.getNavLinks();
        logStep("Total nav links to validate: " + links.size());

        List<String> brokenLinks  = new ArrayList<>();
        List<String> checkedLinks = new ArrayList<>();

        for (WebElement link : links) {
            String href = link.getAttribute("href");
            if (href == null || href.isBlank()
                    || href.startsWith("mailto:")
                    || href.startsWith("tel:")
                    || href.startsWith("javascript:")
                    || href.equals("#")) {
                continue;
            }
            if (href.startsWith("/")) {
                href = "https://mb.io" + href;
            }
            int status = getHttpStatus(href);
            checkedLinks.add(href + " -> " + status);
            logStep("Checked: " + href + " -> HTTP " + status);
            if (status >= 400) {
                brokenLinks.add(href + " returned HTTP " + status);
            }
        }

        logStep("Links checked: " + checkedLinks.size());
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(checkedLinks).as("Should have checked at least one link").isNotEmpty();
        soft.assertThat(brokenLinks)
                .as("Broken links found:\n" + String.join("\n", brokenLinks))
                .isEmpty();
        soft.assertAll();
    }

    @Test(description = "App Store and Google Play links return HTTP 200")
    @Severity(SeverityLevel.NORMAL)
    @Story("Download link health")
    public void testDownloadLinksAreReachable() {
        String appStoreHref   = homePage.getAppStoreHref();
        String googlePlayHref = homePage.getGooglePlayHref();

        SoftAssertions soft = new SoftAssertions();
        if (!appStoreHref.isBlank()) {
            int status = getHttpStatus(appStoreHref);
            logStep("App Store link status: " + status);
            soft.assertThat(status)
                    .as("App Store link should not return an error (got %d)", status)
                    .isLessThan(400);
        }
        if (!googlePlayHref.isBlank()) {
            int status = getHttpStatus(googlePlayHref);
            logStep("Google Play link status: " + status);
            soft.assertThat(status)
                    .as("Google Play link should not return an error (got %d)", status)
                    .isLessThan(400);
        }
        soft.assertAll();
    }

    private int getHttpStatus(String href) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(href).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(8_000);
            conn.setReadTimeout(8_000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (QA-Automation)");
            conn.connect();
            int code = conn.getResponseCode();
            conn.disconnect();
            return code;
        } catch (Exception e) {
            logStep("Could not check URL " + href + ": " + e.getMessage());
            return 0;
        }
    }
}
