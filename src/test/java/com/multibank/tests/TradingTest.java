package com.multibank.tests;

import com.multibank.pages.HomePage;
import com.multibank.utils.TestDataLoader;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Tests for mb.io trading sections.
 * Live page sections: Top Gainers | Trending Now | Top Losers
 */
@Epic("mb.io Trading Platform")
@Feature("Trading Sections")
public class TradingTest extends BaseTest {

    private HomePage homePage;
    private Map<String, Object> tradingData;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        homePage.open();
        tradingData = TestDataLoader.loadMap("trading.json");
    }

    @Test(description = "Trading sections (Top Gainers / Trending Now / Top Losers) are present")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Trading sections presence")
    public void testSpotSectionPresent() {
        logStep("Assert trading sections are present");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isTradingSectionPresent())
                .as("Trading sections should be visible on home page").isTrue();
        soft.assertAll();
    }

    @Test(description = "Expected trading section headings are displayed")
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading sections content")
    public void testTradingSectionHeadingsPresent() {
        @SuppressWarnings("unchecked")
        List<String> expectedSections = (List<String>) tradingData.get("expectedSections");
        List<String> actual = homePage.getTradingSectionHeadings();
        logStep("Expected sections: " + expectedSections);
        logStep("Actual sections  : " + actual);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(actual).as("At least one trading section should be visible").isNotEmpty();
        if (expectedSections != null) {
            for (String expected : expectedSections) {
                boolean found = actual.stream()
                        .anyMatch(a -> a.toLowerCase().contains(expected.toLowerCase()));
                soft.assertThat(found)
                        .as("Section '%s' should be present. Actual: %s", expected, actual)
                        .isTrue();
            }
        }
        soft.assertAll();
    }

    @Test(description = "Trading pair rows are listed")
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading pairs")
    public void testTradingPairsListed() {
        int count = homePage.getTradingPairRows().size();
        logStep("Trading pair rows found: " + count);
        int min = (int) tradingData.getOrDefault("minPairCount", 1);
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(count)
                .as("Should display at least %d trading pairs", min)
                .isGreaterThanOrEqualTo(min);
        soft.assertAll();
    }

    @Test(description = "Hero heading 'Crypto for everyone' is visible")
    @Severity(SeverityLevel.NORMAL)
    @Story("Hero content")
    public void testHeroHeadingVisible() {
        logStep("Check hero heading visibility");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isHeroHeadingVisible())
                .as("Hero heading 'Crypto for everyone' should be visible").isTrue();
        soft.assertAll();
    }

    @Test(description = "Download the app CTA link is present")
    @Severity(SeverityLevel.NORMAL)
    @Story("App download CTA")
    public void testDownloadAppLinkPresent() {
        logStep("Check Download the app link");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isDownloadLinkPresent())
                .as("'Download the app' link should be present").isTrue();
        String href = homePage.getDownloadLinkHref();
        logStep("Download href: " + href);
        soft.assertThat(href).as("Download link href should not be blank").isNotBlank();
        soft.assertAll();
    }
}
