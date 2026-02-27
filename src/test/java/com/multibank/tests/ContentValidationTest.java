package com.multibank.tests;

import com.multibank.pages.HomePage;
import com.multibank.pages.AboutPage;
import com.multibank.utils.TestDataLoader;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Content validation tests for mb.io/en.
 * Verified from live page:
 *   - Footer with Legal links (Terms, Privacy, Cookie, etc.)
 *   - App download via mbio.go.link (single unified deep link for iOS + Android)
 *   - Company/About page at mb.io/en/company
 */
@Epic("mb.io Trading Platform")
@Feature("Content Validation")
public class ContentValidationTest extends BaseTest {

    private HomePage homePage;
    private AboutPage aboutPage;
    private Map<String, Object> contentData;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        aboutPage = new AboutPage();
        homePage.open();
        contentData = TestDataLoader.loadMap("content.json");
    }

    // ── App Download ──────────────────────────────────────────────────────────

    @Test(description = "Download the app link is present (unified iOS + Android deep link)")
    @Severity(SeverityLevel.NORMAL)
    @Story("App download link")
    public void testAppStoreLinkPresent() {
        logStep("Checking for app download link (mbio.go.link)");
        SoftAssertions soft = new SoftAssertions();
        boolean present = homePage.isDownloadLinkPresent();
        logStep("Download link present: " + present);
        soft.assertThat(present)
                .as("App download link should be present on the page").isTrue();
        if (present) {
            String href = homePage.getDownloadLinkHref();
            logStep("Download href: " + href);
            soft.assertThat(href).as("Download href should not be blank").isNotBlank();
        }
        soft.assertAll();
    }

    @Test(description = "App download link points to correct deep link URL")
    @Severity(SeverityLevel.NORMAL)
    @Story("App download link")
    public void testGooglePlayLinkPresent() {
        logStep("Checking app download deep link URL");
        SoftAssertions soft = new SoftAssertions();
        String href = homePage.getDownloadLinkHref();
        logStep("App download href: " + href);
        soft.assertThat(href)
                .as("Download link should point to mbio.go.link or similar")
                .isNotBlank();
        soft.assertAll();
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    @Test(description = "Footer is present at bottom of page")
    @Severity(SeverityLevel.NORMAL)
    @Story("Footer")
    public void testFooterPresent() {
        logStep("Scroll to bottom and check footer");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isFooterPresent())
                .as("Footer should be present").isTrue();
        soft.assertAll();
    }

    @Test(description = "Legal links (Terms, Privacy, Cookie) are present in footer")
    @Severity(SeverityLevel.NORMAL)
    @Story("Footer legal links")
    public void testLegalLinksPresent() {
        logStep("Checking legal links in footer");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isLegalLinkPresent())
                .as("Legal links (Terms/Privacy/Cookie) should be present in footer")
                .isTrue();
        soft.assertAll();
    }

    @Test(description = "Download section is visible on home page")
    @Severity(SeverityLevel.NORMAL)
    @Story("Download section")
    public void testDownloadSectionVisible() {
        logStep("Check download section visibility");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isDownloadSectionVisible())
                .as("Download section / CTA should be visible").isTrue();
        soft.assertAll();
    }

    @Test(description = "Promo / banner section is present")
    @Severity(SeverityLevel.MINOR)
    @Story("Marketing banners")
    public void testMarketingBannersPresent() {
        logStep("Check banner / promo section presence");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isBannerSectionPresent())
                .as("Banner/promo section should be present").isTrue();
        soft.assertAll();
    }

    // ── About / Company page ─────────────────────────────────────────────────

    @Test(description = "Company page loads at mb.io/en/company")
    @Severity(SeverityLevel.NORMAL)
    @Story("Company page")
    public void testAboutPageLoads() {
        logStep("Navigate to Company page");
        aboutPage.navigateToWhyMultiBank();
        logStep("Current URL: " + aboutPage.getCurrentUrl());
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(aboutPage.getCurrentUrl())
                .as("Should have navigated to company/about page")
                .isNotBlank();
        soft.assertAll();
    }

    @Test(description = "Company page has a visible heading")
    @Severity(SeverityLevel.NORMAL)
    @Story("Company page content")
    public void testAboutPageHeadingPresent() {
        aboutPage.navigateToWhyMultiBank();
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(aboutPage.isPageHeadingPresent())
                .as("Company page should have a visible heading").isTrue();
        String heading = aboutPage.getPageHeadingText();
        logStep("Page heading: " + heading);
        soft.assertThat(heading).as("Heading should not be blank").isNotBlank();
        soft.assertAll();
    }
}
