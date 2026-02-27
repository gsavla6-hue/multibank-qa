package com.multibank.tests;

import com.multibank.pages.HomePage;
import com.multibank.utils.TestDataLoader;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Tests for mb.io/en navigation and layout.
 * Nav items verified from live page: Explore, Features, Company, $MBG, Sign in, Sign up
 */
@Epic("mb.io Trading Platform")
@Feature("Navigation & Layout")
public class NavigationTest extends BaseTest {

    private HomePage homePage;
    private List<String> expectedNavItems;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        homePage.open();
        expectedNavItems = TestDataLoader.getStringList("navigation.json", "expectedNavItems");
    }

    @Test(description = "Home page loads with correct title and URL")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Page loads")
    public void testPageLoads() {
        logStep("Verify page title and URL");
        String title = homePage.getPageTitle();
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(title).as("Page title should not be empty").isNotBlank();
        soft.assertThat(homePage.getCurrentUrl())
                .as("URL should contain mb.io")
                .containsIgnoringCase("mb.io");
        soft.assertAll();
    }

    @Test(description = "Navigation bar is rendered")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation bar")
    public void testNavBarPresent() {
        logStep("Assert navigation bar is displayed");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isNavBarVisible())
                .as("Navigation bar should be visible").isTrue();
        soft.assertAll();
    }

    @Test(description = "Navigation contains expected items: Explore, Features, Company")
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation items")
    public void testNavItemsPresent() {
        List<String> actual = homePage.getNavLinkTexts();
        logStep("Expected: " + expectedNavItems);
        logStep("Actual  : " + actual);
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(actual).as("Should have at least one nav link").isNotEmpty();
        for (String expected : expectedNavItems) {
            boolean found = actual.stream()
                    .anyMatch(a -> a.toLowerCase().contains(expected.toLowerCase()));
            soft.assertThat(found)
                    .as("Nav item '%s' should be present. Actual: %s", expected, actual)
                    .isTrue();
        }
        soft.assertAll();
    }

    @Test(description = "Sign In and Sign Up links are present")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Authentication links")
    public void testSignInSignUpPresent() {
        logStep("Checking Sign In and Sign Up links");
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isSignInLinkPresent())
                .as("Sign In link should be present").isTrue();
        soft.assertThat(homePage.isSignUpLinkPresent())
                .as("Sign Up link should be present").isTrue();
        soft.assertAll();
    }

    @Test(description = "All navigation links have a non-empty href")
    @Severity(SeverityLevel.NORMAL)
    @Story("Link validation")
    public void testNavLinksHaveValidHref() {
        var links = homePage.getNavLinks();
        logStep("Total nav links found: " + links.size());
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(links).as("Should have at least one nav link").isNotEmpty();
        for (var link : links) {
            String href = link.getAttribute("href");
            String text = link.getText().trim();
            if (text.isEmpty()) continue;
            soft.assertThat(href)
                    .as("Link '%s' should have a non-empty href", text)
                    .isNotNull().isNotBlank();
        }
        soft.assertAll();
    }
}
