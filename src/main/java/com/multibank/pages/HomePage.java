package com.multibank.pages;

import com.multibank.config.FrameworkConfig;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for mb.io/en home page.
 *
 * Actual page structure (verified 2024-03-15):
 *   Nav  : Explore | Features | Company | $MBG | Sign in | Sign up
 *   Hero : "Crypto for everyone" + "Download the app" CTA
 *   Trading sections: Top Gainers | Trending Now | Top Losers
 *   App download: single deep link (mbio.go.link) — not separate store links
 *   Footer: Legal links, Support, payment method logos
 */
@Slf4j
public class HomePage extends BasePage {

    // ── Locators ─────────────────────────────────────────────────────────────

    // Header / Nav
    @FindBy(css = "header, nav")
    private WebElement topNavBar;

    private static final By NAV_LINKS     = By.cssSelector("header a, nav a");
    private static final By LOGO          = By.cssSelector("header img, nav img, a[href*='/en'] img");

    // Hero section
    private static final By HERO_HEADING  = By.xpath("//*[contains(text(),'Crypto for everyone')]");
    private static final By DOWNLOAD_LINK = By.cssSelector("a[href*='mbio.go.link'], a[href*='go.link']");
    private static final By OPEN_ACCOUNT  = By.cssSelector("a[href*='register']");

    // Trading sections (Top Gainers / Trending Now / Top Losers)
    private static final By TRADING_SECTION_HEADINGS = By.xpath(
        "//*[contains(text(),'Top Gainers') or contains(text(),'Trending Now') or contains(text(),'Top Losers')]"
    );
    private static final By TRADING_PAIRS = By.cssSelector(
        "table tbody tr, [class*='asset'], [class*='coin'], [class*='pair'], [class*='market']"
    );

    // Sign in / Sign up
    private static final By SIGN_IN_LINK  = By.cssSelector("a[href*='login'], a[href*='sign-in']");
    private static final By SIGN_UP_LINK  = By.cssSelector("a[href*='register'], a[href*='sign-up']");

    // Footer
    private static final By FOOTER        = By.cssSelector("footer, [class*='footer']");
    private static final By FOOTER_LINKS  = By.cssSelector("footer a, [class*='footer'] a");
    private static final By LEGAL_LINKS   = By.cssSelector("a[href*='terms'], a[href*='privacy'], a[href*='cookie']");

    // App download — mb.io uses a single unified deep link
    private static final By APP_DOWNLOAD  = By.cssSelector("a[href*='mbio.go.link'], a[href*='go.link']");

    // ── Actions ───────────────────────────────────────────────────────────────

    public HomePage open() {
        navigateTo(FrameworkConfig.getBaseUrl());
        log.info("Opened home page: {}", FrameworkConfig.getBaseUrl());
        return this;
    }

    public boolean isLogoVisible() {
        return isElementVisible(LOGO);
    }

    public boolean isNavBarVisible() {
        try {
            return topNavBar.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getNavLinkTexts() {
        return findElements(NAV_LINKS).stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<WebElement> getNavLinks() {
        return findElements(NAV_LINKS);
    }

    public boolean isHeroHeadingVisible() {
        return isElementVisible(HERO_HEADING);
    }

    public boolean isDownloadLinkPresent() {
        return isElementPresent(DOWNLOAD_LINK);
    }

    public String getDownloadLinkHref() {
        if (!isElementPresent(DOWNLOAD_LINK)) return "";
        return driver.findElement(DOWNLOAD_LINK).getAttribute("href");
    }

    public boolean isSignInLinkPresent() {
        return isElementPresent(SIGN_IN_LINK);
    }

    public boolean isSignUpLinkPresent() {
        return isElementPresent(SIGN_UP_LINK);
    }

    public boolean isTradingSectionPresent() {
        return isElementPresent(TRADING_SECTION_HEADINGS);
    }

    public List<String> getTradingSectionHeadings() {
        return findElements(TRADING_SECTION_HEADINGS).stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public List<WebElement> getTradingPairRows() {
        return findElements(TRADING_PAIRS);
    }

    public boolean isFooterPresent() {
        scrollToBottom();
        return isElementPresent(FOOTER);
    }

    public List<String> getFooterLinkTexts() {
        scrollToBottom();
        return findElements(FOOTER_LINKS).stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public boolean isLegalLinkPresent() {
        scrollToBottom();
        return isElementPresent(LEGAL_LINKS);
    }

    // Keep these for ContentValidationTest compatibility
    // mb.io uses a single deep link for both stores
    public boolean isAppStoreLinkPresent() {
        scrollToBottom();
        return isElementPresent(APP_DOWNLOAD);
    }

    public boolean isGooglePlayLinkPresent() {
        scrollToBottom();
        return isElementPresent(APP_DOWNLOAD);
    }

    public String getAppStoreHref() {
        if (!isElementPresent(APP_DOWNLOAD)) return "";
        return driver.findElement(APP_DOWNLOAD).getAttribute("href");
    }

    public String getGooglePlayHref() {
        if (!isElementPresent(APP_DOWNLOAD)) return "";
        return driver.findElement(APP_DOWNLOAD).getAttribute("href");
    }

    public boolean isDownloadSectionVisible() {
        return isElementPresent(DOWNLOAD_LINK) || isElementPresent(APP_DOWNLOAD);
    }

    // For banner check — mb.io has Khabib / MBG promo sections
    public boolean isBannerSectionPresent() {
        return isElementPresent(By.cssSelector(
            "[class*='banner'], [class*='promo'], [class*='hero'], img[src*='khabib'], img[src*='warm-bg']"
        ));
    }

    public boolean isSpotSectionPresent() {
        return isTradingSectionPresent();
    }
}
