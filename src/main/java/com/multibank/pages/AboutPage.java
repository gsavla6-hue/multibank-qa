package com.multibank.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the About Us / Why MultiBank page.
 */
@Slf4j
public class AboutPage extends BasePage {

    private static final String WHY_MULTIBANK_URL_FRAGMENT = "why";

    // ── Locators ─────────────────────────────────────────────────────────────

    @FindBy(css = "h1, [class*='page-title'], [class*='hero-title']")
    private WebElement pageHeading;

    private static final By PAGE_HEADING       = By.cssSelector("h1, [class*='page-title'], [class*='hero-title']");
    private static final By SECTION_HEADINGS   = By.cssSelector("h2, h3, [class*='section-title']");
    private static final By FEATURE_ITEMS      = By.cssSelector("[class*='feature'], [class*='benefit'], [class*='card'], [class*='item']");
    private static final By STAT_NUMBERS       = By.cssSelector("[class*='stat'], [class*='counter'], [class*='number']");
    private static final By ABOUT_NAV_LINK     = By.cssSelector("a[href*='about'], nav a:contains('About'), [class*='nav'] a");

    // ── Actions ──────────────────────────────────────────────────────────────

    public AboutPage navigateToWhyMultiBank() {
        // Try direct URL first
        String baseUrl = driver.getCurrentUrl().replaceAll("(?<=//[^/]+).*", "");
        String[] candidates = {
                "https://mb.io/en/company",
                "https://mb.io/en/about",
                baseUrl + "/company",
                baseUrl + "/about"
        };

        for (String url : candidates) {
            try {
                navigateTo(url);
                if (driver.getCurrentUrl().contains("about") || driver.getCurrentUrl().contains("why")) {
                    log.info("Navigated to About page via: {}", url);
                    return this;
                }
            } catch (Exception e) {
                log.debug("URL attempt failed: {}", url);
            }
        }

        // Fallback: click nav link
        clickAboutNavLink();
        return this;
    }

    private void clickAboutNavLink() {
        List<WebElement> navLinks = findElements(ABOUT_NAV_LINK);
        for (WebElement link : navLinks) {
            String text = link.getText().toLowerCase();
            String href = link.getAttribute("href") != null ? link.getAttribute("href").toLowerCase() : "";
            if (text.contains("about") || text.contains("why") || href.contains("about") || href.contains("why")) {
                log.info("Clicking About nav link: {}", text);
                jsClick(link);
                return;
            }
        }
        log.warn("Could not find About navigation link");
    }

    public boolean isPageHeadingPresent() {
        return isElementPresent(PAGE_HEADING) && isElementVisible(PAGE_HEADING);
    }

    public String getPageHeadingText() {
        if (!isElementPresent(PAGE_HEADING)) return "";
        return getText(PAGE_HEADING);
    }

    public List<String> getSectionHeadings() {
        return findElements(SECTION_HEADINGS)
                .stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public int getFeatureItemCount() {
        return findElements(FEATURE_ITEMS).size();
    }

    public List<String> getStatNumbers() {
        return findElements(STAT_NUMBERS)
                .stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public boolean isOnAboutPage() {
        String url = getCurrentUrl().toLowerCase();
        return url.contains("about") || url.contains("why");
    }
}
