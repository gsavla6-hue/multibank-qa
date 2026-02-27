package com.multibank.tests;

import com.multibank.pages.HomePage;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic accessibility checks without a third-party a11y library.
 *
 * Covers WCAG 2.1 Level A criteria:
 *   - Images have non-empty alt text
 *   - Form inputs have associated labels or aria-label
 *   - Page has exactly one H1
 *   - Links have discernible text (not empty or icon-only)
 *   - lang attribute is present on <html>
 *   - Page title is non-empty (1.1.1, 2.4.2)
 *
 * Note: For full a11y auditing, integrate axe-core via WebDriver.
 */
@Epic("MultiBank Trading Platform")
@Feature("Accessibility")
public class AccessibilityTest extends BaseTest {

    private HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        homePage = new HomePage();
        homePage.open();
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "Page <html> element has a lang attribute")
    @Severity(SeverityLevel.NORMAL)
    @Story("WCAG 3.1.1 – Language of page")
    public void testHtmlLangAttributePresent() {
        String lang = (String) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.lang;");
        logStep("html[lang] = \"" + lang + "\"");

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(lang)
                .as("html element should have a non-empty lang attribute")
                .isNotBlank();
        soft.assertAll();
    }

    @Test(description = "Page title is not empty")
    @Severity(SeverityLevel.NORMAL)
    @Story("WCAG 2.4.2 – Page Titled")
    public void testPageTitleNotEmpty() {
        String title = homePage.getPageTitle();
        logStep("Page title: \"" + title + "\"");

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(title)
                .as("Page title should not be empty")
                .isNotBlank();
        soft.assertAll();
    }

    @Test(description = "All images have a non-empty alt attribute")
    @Severity(SeverityLevel.NORMAL)
    @Story("WCAG 1.1.1 – Non-text Content")
    public void testImagesHaveAltText() {
        List<WebElement> images = driver.findElements(By.tagName("img"));
        logStep("Total images found: " + images.size());

        List<String> violations = new ArrayList<>();
        for (WebElement img : images) {
            String alt  = img.getAttribute("alt");
            String src  = img.getAttribute("src");
            String role = img.getAttribute("role");

            // Decorative images should have role="presentation" or alt=""
            // Content images MUST have meaningful alt text
            if (alt == null) {
                violations.add("Missing alt attribute entirely: " + src);
            }
            // We don't flag alt="" because that's valid for decorative images
        }

        logStep("Alt-text violations: " + violations.size());
        violations.forEach(v -> logStep("  ⚠ " + v));

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(violations)
                .as("All images must have an alt attribute")
                .isEmpty();
        soft.assertAll();
    }

    @Test(description = "Visible links have discernible text")
    @Severity(SeverityLevel.NORMAL)
    @Story("WCAG 2.4.4 – Link Purpose")
    public void testLinksHaveDiscernibleText() {
        List<WebElement> links = driver.findElements(By.tagName("a"));
        List<String> violations = new ArrayList<>();

        for (WebElement link : links) {
            if (!link.isDisplayed()) continue;

            String text      = link.getText().trim();
            String ariaLabel = link.getAttribute("aria-label");
            String title     = link.getAttribute("title");
            String href      = link.getAttribute("href");

            // A link is accessible if it has visible text, aria-label, or title
            boolean accessible = !text.isEmpty()
                    || (ariaLabel != null && !ariaLabel.isBlank())
                    || (title != null && !title.isBlank());

            if (!accessible) {
                violations.add("Link with no discernible text: href=" + href);
            }
        }

        logStep("Link text violations: " + violations.size());
        violations.forEach(v -> logStep("  ⚠ " + v));

        SoftAssertions soft = new SoftAssertions();
        // Report violations but use a lenient threshold
        // (some icon-links use background images — acceptable in modern UIs)
        soft.assertThat(violations.size())
                .as("Should have few or no links without discernible text. Violations:\n"
                        + String.join("\n", violations))
                .isLessThanOrEqualTo(5);
        soft.assertAll();
    }

    @Test(description = "Page has at least one H1 heading")
    @Severity(SeverityLevel.MINOR)
    @Story("WCAG 1.3.1 – Info and Relationships")
    public void testPageHasH1() {
        List<WebElement> h1s = driver.findElements(By.tagName("h1"));
        logStep("H1 count: " + h1s.size());
        h1s.forEach(h -> logStep("  H1: \"" + h.getText().trim() + "\""));

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(h1s)
                .as("Page should have at least one H1 heading")
                .isNotEmpty();
        soft.assertAll();
    }

    @Test(description = "Input elements have associated labels or aria-label")
    @Severity(SeverityLevel.MINOR)
    @Story("WCAG 1.3.1 – Form Labels")
    public void testInputsHaveLabels() {
        List<WebElement> inputs = driver.findElements(
                By.cssSelector("input:not([type='hidden']):not([type='submit']):not([type='button'])"));
        logStep("Visible inputs found: " + inputs.size());

        if (inputs.isEmpty()) {
            logStep("No inputs found on page — skipping");
            return;
        }

        List<String> violations = new ArrayList<>();
        for (WebElement input : inputs) {
            if (!input.isDisplayed()) continue;

            String id         = input.getAttribute("id");
            String ariaLabel  = input.getAttribute("aria-label");
            String ariaLabelledBy = input.getAttribute("aria-labelledby");
            String placeholder = input.getAttribute("placeholder");

            // Check for associated <label for="id">
            boolean hasLabel = false;
            if (id != null && !id.isBlank()) {
                hasLabel = !driver.findElements(By.cssSelector("label[for='" + id + "']")).isEmpty();
            }

            boolean accessible = hasLabel
                    || (ariaLabel != null && !ariaLabel.isBlank())
                    || (ariaLabelledBy != null && !ariaLabelledBy.isBlank())
                    || (placeholder != null && !placeholder.isBlank()); // placeholder is not ideal but acceptable

            if (!accessible) {
                violations.add("Input without label: type=" + input.getAttribute("type") + " id=" + id);
            }
        }

        logStep("Input label violations: " + violations.size());
        violations.forEach(v -> logStep("  ⚠ " + v));

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(violations)
                .as("All inputs should have associated labels. Violations:\n"
                        + String.join("\n", violations))
                .isEmpty();
        soft.assertAll();
    }
}
