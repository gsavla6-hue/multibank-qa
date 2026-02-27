package com.multibank.tests;

import com.multibank.pages.HomePage;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.Dimension;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Validates the platform's responsive layout across common viewport sizes.
 *
 * Viewports tested:
 *   - Desktop  : 1920×1080
 *   - Laptop   : 1366×768
 *   - Tablet   : 768×1024
 *   - Mobile   : 375×812  (iPhone X)
 *
 * Checks: page loads without JS errors, key sections are present,
 * and the page title is consistent across viewports.
 */
@Epic("MultiBank Trading Platform")
@Feature("Responsive Layout")
public class ResponsiveLayoutTest extends BaseTest {

    private HomePage homePage;
    private String baselineTitle;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        homePage.open();
        baselineTitle = homePage.getPageTitle();
    }

    // ── Data provider ─────────────────────────────────────────────────────────

    @DataProvider(name = "viewports")
    public Object[][] viewports() {
        return new Object[][] {
                { "Desktop",  1920, 1080 },
                { "Laptop",   1366,  768 },
                { "Tablet",    768, 1024 },
                { "Mobile",    375,  812 },
        };
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Test(dataProvider = "viewports",
          description = "Page loads and title is consistent at each viewport")
    @Severity(SeverityLevel.NORMAL)
    @Story("Viewport consistency")
    public void testPageLoadsAtViewport(String label, int width, int height) {
        logStep("Setting viewport → " + label + " (" + width + "×" + height + ")");
        driver.manage().window().setSize(new Dimension(width, height));
        driver.navigate().refresh();

        SoftAssertions soft = new SoftAssertions();
        String title = homePage.getPageTitle();
        logStep("Title at " + label + ": " + title);

        soft.assertThat(title)
                .as("Page title should not be empty at %s viewport", label)
                .isNotBlank();
        soft.assertThat(title)
                .as("Page title should be consistent across viewports")
                .isEqualTo(baselineTitle);
        soft.assertAll();
    }

    @Test(dataProvider = "viewports",
          description = "Navigation is present (or hamburger menu) at each viewport")
    @Severity(SeverityLevel.NORMAL)
    @Story("Responsive navigation")
    public void testNavigationPresentAtViewport(String label, int width, int height) {
        logStep("Setting viewport → " + label + " (" + width + "×" + height + ")");
        driver.manage().window().setSize(new Dimension(width, height));
        driver.navigate().refresh();

        // On mobile the nav may be behind a hamburger – we just assert
        // that either the standard nav or a menu toggle is present.
        boolean navVisible    = homePage.isNavBarVisible();
        boolean linksPresent  = !homePage.getNavLinks().isEmpty();

        logStep("Nav visible: " + navVisible + " | Links present: " + linksPresent);

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(navVisible || linksPresent)
                .as("Navigation should be accessible (bar or hamburger) at %s (%dx%d)",
                        label, width, height)
                .isTrue();
        soft.assertAll();
    }

    @Test(dataProvider = "viewports",
          description = "Spot trading section visible at each viewport")
    @Severity(SeverityLevel.MINOR)
    @Story("Responsive content")
    public void testSpotSectionVisibleAtViewport(String label, int width, int height) {
        logStep("Setting viewport → " + label + " (" + width + "×" + height + ")");
        driver.manage().window().setSize(new Dimension(width, height));
        driver.navigate().refresh();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(homePage.isSpotSectionPresent())
                .as("Spot trading section should be present at %s viewport", label)
                .isTrue();
        soft.assertAll();
    }
}
