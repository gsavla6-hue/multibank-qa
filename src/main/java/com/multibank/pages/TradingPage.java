package com.multibank.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Spot Trading section on the trading platform.
 */
@Slf4j
public class TradingPage extends BasePage {

    // ── Locators ─────────────────────────────────────────────────────────────

    // Category tabs (All, Favorites, BTC, ETH, etc.)
    private static final By CATEGORY_TABS      = By.cssSelector("[class*='tab'], [role='tab'], [class*='category'] button, [class*='filter'] button");
    private static final By ACTIVE_TAB         = By.cssSelector("[class*='tab'][class*='active'], [class*='tab'][class*='selected'], [role='tab'][aria-selected='true']");

    // Trading pairs table / list
    private static final By PAIR_NAMES         = By.cssSelector("[class*='symbol'], [class*='pair-name'], [class*='base-currency'], td:first-child");
    private static final By PAIR_PRICES        = By.cssSelector("[class*='price'], [class*='last-price'], td:nth-child(2)");
    private static final By PAIR_CHANGES       = By.cssSelector("[class*='change'], [class*='percent'], td:nth-child(3)");
    private static final By TRADING_TABLE      = By.cssSelector("table, [class*='market-list'], [class*='pair-list'], [class*='trading-list']");
    private static final By TABLE_ROWS         = By.cssSelector("table tbody tr, [class*='market-list'] [class*='row'], [class*='pair-list'] [class*='item']");

    // Search / filter
    private static final By SEARCH_INPUT       = By.cssSelector("input[type='search'], input[placeholder*='Search'], input[placeholder*='search'], [class*='search'] input");
    private static final By SPOT_TAB           = By.cssSelector("button:contains('Spot'), [class*='tab']:contains('Spot'), a:contains('Spot')");

    // ── Actions ──────────────────────────────────────────────────────────────

    public boolean isTradingTablePresent() {
        return isElementPresent(TRADING_TABLE) || isElementPresent(TABLE_ROWS);
    }

    public List<String> getCategoryTabNames() {
        return findElements(CATEGORY_TABS)
                .stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public String getActiveTabName() {
        if (isElementPresent(ACTIVE_TAB)) {
            return getText(ACTIVE_TAB);
        }
        return "";
    }

    public int getTradingPairCount() {
        return findElements(TABLE_ROWS).size();
    }

    public List<String> getTradingPairNames() {
        return findElements(PAIR_NAMES)
                .stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public List<String> getTradingPairPrices() {
        return findElements(PAIR_PRICES)
                .stream()
                .map(el -> el.getText().trim())
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toList());
    }

    public void clickCategoryTab(String tabName) {
        List<WebElement> tabs = findElements(CATEGORY_TABS);
        for (WebElement tab : tabs) {
            if (tab.getText().trim().equalsIgnoreCase(tabName)) {
                log.info("Clicking category tab: {}", tabName);
                jsClick(tab);
                return;
            }
        }
        log.warn("Tab not found: {}", tabName);
    }

    public boolean isSearchInputPresent() {
        return isElementPresent(SEARCH_INPUT);
    }

    public void searchTradingPair(String pairName) {
        if (isElementPresent(SEARCH_INPUT)) {
            WebElement input = waitForClickable(SEARCH_INPUT);
            input.clear();
            input.sendKeys(pairName);
            log.info("Searched for trading pair: {}", pairName);
        } else {
            log.warn("Search input not found");
        }
    }

    public boolean arePairPricesNonEmpty() {
        List<String> prices = getTradingPairPrices();
        return !prices.isEmpty() && prices.stream().anyMatch(p -> !p.isEmpty());
    }
}
