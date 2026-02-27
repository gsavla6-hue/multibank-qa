# MultiBank QA Automation Framework

> Production-grade Selenium + TestNG automation suite for [mb.io](https://mb.io/en)

[![CI Pipeline](https://github.com/YOUR_USERNAME/multibank-qa/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/multibank-qa/actions/workflows/ci.yml)

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Running Tests](#running-tests)
- [Configuration](#configuration)
- [Test Coverage](#test-coverage)
- [Reporting](#reporting)
- [CI/CD](#cicd)
- [Task 2 – Character Frequency](#task-2--character-frequency)
- [Design Decisions & Trade-offs](#design-decisions--trade-offs)
- [Extending the Framework](#extending-the-framework)

---

## Overview

This repository contains a complete QA automation framework covering:

| Task | Description |
|------|-------------|
| **Task 1** | Web UI automation for trade.multibank.io using Selenium 4 + TestNG |
| **Task 2** | String character frequency counter with edge-case handling |

---

## Architecture

```
┌────────────────────────────────────────────────────────────┐
│                        Test Layer                          │
│  NavigationTest │ TradingTest │ ContentValidationTest      │
└────────────────────────┬───────────────────────────────────┘
                         │ extends
┌────────────────────────▼───────────────────────────────────┐
│                       BaseTest                             │
│  Driver lifecycle │ Extent/Allure hooks │ Retry │ Screenshots│
└────────────────────────┬───────────────────────────────────┘
                         │ uses
┌────────────────────────▼───────────────────────────────────┐
│                    Page Object Layer                       │
│  BasePage │ HomePage │ TradingPage │ AboutPage             │
└──────┬──────────────────────────┬──────────────────────────┘
       │                          │
┌──────▼──────┐          ┌────────▼────────┐
│DriverFactory│          │  Test Data      │
│(ThreadLocal)│          │  (JSON files)   │
└──────┬──────┘          └─────────────────┘
       │
┌──────▼──────┐
│FrameworkConfig│
│(config.props) │
└─────────────┘
```

### Key Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| **POM pattern** | Page Object Model | Separates UI structure from test logic; one place to update selectors |
| **Thread safety** | `ThreadLocal<WebDriver>` | Enables parallel test execution without race conditions |
| **Configuration** | `config.properties` + system property overrides | Environment-agnostic; CI can override via `-D` flags without editing files |
| **Test data** | External JSON files | No hard-coded assertions; data changes don't require recompilation |
| **Wait strategy** | `WebDriverWait` + `ExpectedConditions` | Zero `Thread.sleep()` calls; tests wait only as long as needed |
| **Assertions** | AssertJ `SoftAssertions` | All assertion failures captured per test; easier to diagnose multiple issues in one run |
| **Retry** | `IRetryAnalyzer` (global via `IAnnotationTransformer`) | Handles transient network/timing flakiness without manual annotation on every test |
| **Reporting** | ExtentReports (HTML) + Allure | ExtentReports for quick human review; Allure for CI dashboards and trend history |
| **Driver management** | WebDriverManager | Automatic driver binary resolution; no manual chromedriver downloads |

---

## Project Structure

```
multibank-qa/
├── src/
│   ├── main/
│   │   ├── java/com/multibank/
│   │   │   ├── config/
│   │   │   │   ├── DriverFactory.java       # Thread-safe WebDriver lifecycle
│   │   │   │   └── FrameworkConfig.java     # Centralised config with sys-prop overrides
│   │   │   ├── pages/
│   │   │   │   ├── BasePage.java            # Smart waits, scroll, JS utilities
│   │   │   │   ├── HomePage.java            # Home page POM
│   │   │   │   ├── TradingPage.java         # Trading section POM
│   │   │   │   └── AboutPage.java           # About/Why MultiBank POM
│   │   │   ├── utils/
│   │   │   │   ├── ExtentReportManager.java # Singleton ExtentReports instance
│   │   │   │   ├── ScreenshotUtil.java      # PNG capture + base64 for Allure
│   │   │   │   └── TestDataLoader.java      # JSON → Map/List loader
│   │   │   └── task2/
│   │   │       └── CharacterFrequency.java  # Task 2 solution
│   │   └── resources/
│   │       ├── config.properties            # Framework configuration
│   │       └── logback.xml                  # Structured logging
│   └── test/
│       ├── java/com/multibank/
│       │   ├── tests/
│       │   │   ├── BaseTest.java            # TestNG base: setup/teardown/reporting
│       │   │   ├── NavigationTest.java      # Navigation & layout tests
│       │   │   ├── TradingTest.java         # Spot trading section tests
│       │   │   └── ContentValidationTest.java # Banners, downloads, About page
│       │   ├── listeners/
│       │   │   ├── RetryAnalyzer.java       # Configurable flaky-test retry
│       │   │   └── TestListener.java        # Auto-applies retry; logs lifecycle
│       │   └── task2/
│       │       └── CharacterFrequencyTest.java # Unit tests for Task 2
│       └── resources/
│           ├── testdata/
│           │   ├── navigation.json          # Expected nav items
│           │   ├── trading.json             # Trading pair expectations
│           │   └── content.json             # Content/About page expectations
│           ├── testng-regression.xml        # Full regression suite
│           ├── testng-smoke.xml             # Fast smoke suite
│           ├── testng-cross-browser.xml     # Parallel cross-browser suite
│           └── allure.properties
├── .github/
│   └── workflows/
│       └── ci.yml                           # GitHub Actions pipeline
└── pom.xml
```

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Java (JDK) | 11+ | `java -version` to verify |
| Maven | 3.8+ | `mvn -version` to verify |
| Chrome | Latest | Auto-managed by WebDriverManager |
| Firefox | Latest | Optional – for cross-browser runs |
| Edge | Latest | Optional – for cross-browser runs |

> **No manual chromedriver download required.** WebDriverManager resolves the correct binary automatically.

---

## Quick Start

```bash
# 1. Clone
git clone https://github.com/YOUR_USERNAME/multibank-qa.git
cd multibank-qa

# 2. Run the full regression suite (Chrome, headed)
mvn test

# 3. Run smoke suite only
mvn test -Psmoke

# 4. Run headless (e.g. on a server)
mvn test -Dheadless=true

# 5. Run on Firefox
mvn test -Dbrowser=firefox

# 6. Run cross-browser suite in parallel
mvn test -Pcross-browser
```

---

## Running Tests

### Maven Profiles

| Profile | Command | Description |
|---------|---------|-------------|
| `regression` | `mvn test` | Full suite, parallel by class (default) |
| `smoke` | `mvn test -Psmoke` | Critical path only (~5 tests) |
| `cross-browser` | `mvn test -Pcross-browser` | Chrome + Firefox + Edge in parallel |

### Runtime overrides (system properties)

| Property | Default | Example |
|----------|---------|---------|
| `browser` | `chrome` | `-Dbrowser=firefox` |
| `headless` | `false` | `-Dheadless=true` |
| `base.url` | `https://trade.multibank.io` | `-Dbase.url=https://staging.multibank.io` |
| `retry.count` | `1` | `-Dretry.count=2` |
| `explicit.wait.seconds` | `15` | `-Dexplicit.wait.seconds=20` |

### Run a single test class

```bash
mvn test -Dtest=NavigationTest
```

### Run a specific test method

```bash
mvn test -Dtest=NavigationTest#testNavItemsPresent
```

---

## Configuration

All defaults live in `src/main/resources/config.properties`. Any value can be overridden without editing the file by passing it as a `-D` system property.

```properties
base.url=https://trade.multibank.io
browser=chrome
headless=false
implicit.wait.seconds=5
explicit.wait.seconds=15
page.load.timeout.seconds=30
retry.count=1
screenshot.dir=target/screenshots
report.dir=target/extent-reports
```

---

## Test Coverage

### Navigation & Layout

| Test | Validates |
|------|-----------|
| `testPageLoads` | Page title non-empty, URL contains "multibank" |
| `testLogoVisible` | Logo element is displayed |
| `testNavBarPresent` | Navigation bar is rendered |
| `testNavItemsPresent` | Expected items from `navigation.json` are in the nav |
| `testNavLinksHaveValidHref` | All visible nav links have a non-empty `href` |

### Spot Trading

| Test | Validates |
|------|-----------|
| `testSpotSectionPresent` | Spot trading section exists on home page |
| `testCategoryTabsDisplayed` | Category tabs from `trading.json` are rendered |
| `testTradingPairsListed` | At least `minPairCount` rows displayed |
| `testTradingPairNamesNonEmpty` | All visible pair names are non-blank strings |
| `testTradingPairPricesPopulated` | Price data is not empty |
| `testAllCategoryTabShowsPairs` | 'All' tab shows ≥ 1 pair |

### Content Validation

| Test | Validates |
|------|-----------|
| `testMarketingBannersPresent` | Banner section visible at page bottom |
| `testDownloadSectionVisible` | Download section is displayed |
| `testAppStoreLinkPresent` | App Store link exists and points to apple.com |
| `testGooglePlayLinkPresent` | Google Play link exists and points to google.com |
| `testAboutPageLoads` | About/Why page navigates successfully |
| `testAboutPageHeadingPresent` | H1 / page heading is rendered |
| `testAboutPageSectionHeadings` | Expected sections from `content.json` are present |
| `testAboutPageFeatureItems` | Feature/benefit items are displayed |

---

## Reporting

### ExtentReports (HTML)

Generated at `target/extent-reports/MultiBank_Test_Report_<timestamp>.html`.

Open in any browser — includes pass/fail status, browser info, step logs, and failure screenshots.

### Allure Report

```bash
# Generate and open locally (requires Allure CLI)
mvn allure:serve

# Or generate static HTML
mvn allure:report
# → target/site/allure-maven-plugin/index.html
```

### Screenshots

Automatically captured on test failure to `target/screenshots/`. Also embedded in both report types.

### Logs

Framework and test logs written to `target/logs/framework.log`.

---

## CI/CD

The GitHub Actions pipeline (`.github/workflows/ci.yml`) runs on:

- Every push to `main` / `develop`
- Every pull request to `main`
- Nightly at 02:00 UTC
- Manual trigger (choose browser + suite)

**Artifacts published per run:**
- Allure results (30-day retention)
- ExtentReports HTML (30-day retention)
- Failure screenshots (14-day retention)
- Framework logs (7-day retention)

**Allure reports** are deployed to GitHub Pages at:
`https://YOUR_USERNAME.github.io/multibank-qa/allure-report/<run-number>/`

---

## Task 2 – Character Frequency

**Location:** `src/main/java/com/multibank/task2/CharacterFrequency.java`

### Run

```bash
mvn exec:java -Dexec.mainClass="com.multibank.task2.CharacterFrequency"
```

### Example output

```
Input : "hello world"
Output: h:1, e:1, l:3, o:2,  :1, w:1, r:1, d:1
```

### Algorithm

Uses a single O(n) pass over the string with a `LinkedHashMap` (preserves insertion order) to maintain first-appearance ordering. Space complexity is O(k) where k = number of unique characters.

### Configuration options

```java
// Default: case-sensitive, whitespace counted, all characters
new CharacterFrequency()

// Case-insensitive, whitespace ignored
new CharacterFrequency(false, false, false)

// Alphanumeric only (no spaces or special chars)
new CharacterFrequency(true, false, true)
```

### Assumptions

| Assumption | Default |
|------------|---------|
| Case sensitivity | **Case-sensitive** (`'A'` ≠ `'a'`) |
| Whitespace | **Included** (`' '` counts as a character) |
| Special characters | **Included** |
| Null / empty input | Returns empty result (no exception thrown) |

### Run unit tests

```bash
mvn test -Dtest=CharacterFrequencyTest
```

---

## Design Decisions & Trade-offs

### Why TestNG over JUnit 5?

TestNG's built-in parallel execution at the `<suite>` and `<test>` level, `@DataProvider`, grouped test suites via XML, and `IRetryAnalyzer` are first-class features. JUnit 5 can achieve the same with more configuration overhead.

### Why AssertJ SoftAssertions?

Hard assertions stop a test at the first failure, masking subsequent issues. Soft assertions collect all failures and report them together — giving more actionable information per test run.

### Why WebDriverManager?

Eliminates the "chromedriver version mismatch" class of CI failures. Automatically downloads and caches the correct binary for the installed browser version.

### Selector resilience

Locators use layered CSS strategies (multiple fallbacks separated by commas). This makes the framework more resilient to minor DOM changes — the page object finds the element via any of the candidate selectors.

### Trade-offs

| Trade-off | Decision |
|-----------|----------|
| Selector robustness vs. precision | Broad CSS selectors reduce maintenance but may match unintended elements if the DOM changes significantly |
| Parallel threads | Set to 3 by default — increase carefully; too high risks browser/memory exhaustion |
| Retry count | Set to 1 by default — catches transient failures without masking real bugs |

---

## Extending the Framework

### Add a new page

1. Create `src/main/java/com/multibank/pages/NewPage.java` extending `BasePage`
2. Define locators as `private static final By` constants
3. Expose actions as public methods

### Add a new test class

1. Create `src/test/java/com/multibank/tests/NewTest.java` extending `BaseTest`
2. Add test data to `src/test/resources/testdata/`
3. Add the class to the relevant TestNG XML suite

### Add a new browser

1. Add a case to `DriverFactory.initDriver()`
2. Add the browser to the cross-browser TestNG XML
3. Add it to the CI matrix in `ci.yml`

---

*Built with ❤️ for the MultiBank QA coding challenge.*
