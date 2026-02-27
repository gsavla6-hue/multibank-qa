package com.multibank.pages;

import com.multibank.config.DriverFactory;
import com.multibank.config.FrameworkConfig;
import com.multibank.utils.ScreenshotUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Abstract base class for all Page Objects.
 * Encapsulates common WebDriver interactions with smart wait strategies,
 * removing the need for Thread.sleep() anywhere in the framework.
 */
@Slf4j
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(FrameworkConfig.getExplicitWait()));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    public void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    // ── Wait strategies ──────────────────────────────────────────────────────

    protected WebElement waitForVisible(By locator) {
        log.debug("Waiting for element to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForClickable(By locator) {
        log.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected boolean waitForUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected boolean waitForTextPresent(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    // ── Interactions ─────────────────────────────────────────────────────────

    protected void click(By locator) {
        log.debug("Clicking element: {}", locator);
        waitForClickable(locator).click();
    }

    protected void click(WebElement element) {
        waitForClickable(element).click();
    }

    protected void jsClick(WebElement element) {
        log.debug("JS clicking element");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", element);
    }

    protected void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);");
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getAttribute(attribute);
    }

    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    // ── Screenshot ───────────────────────────────────────────────────────────

    public String takeScreenshot(String name) {
        return ScreenshotUtil.capture(driver, name);
    }
}
