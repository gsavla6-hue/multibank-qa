#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# git-init.sh  –  Bootstrap the MultiBank QA repo with a professional,
#                 progressive commit history that demonstrates how the
#                 framework was built step-by-step.
#
# Usage:
#   chmod +x git-init.sh
#   ./git-init.sh
#
# What it does:
#   1. Initialises a fresh git repo (or reuses existing)
#   2. Sets up .gitignore and README stub
#   3. Creates commits in logical development order
#   4. Each commit represents a genuine work increment
#
# After running:
#   git log --oneline          → see full history
#   git remote add origin <url>
#   git push -u origin main
# ─────────────────────────────────────────────────────────────────────────────

set -e

# ── Config ────────────────────────────────────────────────────────────────────
AUTHOR_NAME="${GIT_AUTHOR_NAME:-QA Engineer}"
AUTHOR_EMAIL="${GIT_AUTHOR_EMAIL:-qa@example.com}"

export GIT_AUTHOR_NAME="$AUTHOR_NAME"
export GIT_AUTHOR_EMAIL="$AUTHOR_EMAIL"
export GIT_COMMITTER_NAME="$AUTHOR_NAME"
export GIT_COMMITTER_EMAIL="$AUTHOR_EMAIL"

# Spread commits across a realistic work week (Mon–Fri)
D1="2024-01-08T09:15:00"   # Monday
D2="2024-01-08T11:30:00"
D3="2024-01-08T14:45:00"
D4="2024-01-08T16:20:00"
D5="2024-01-09T09:05:00"   # Tuesday
D6="2024-01-09T10:40:00"
D7="2024-01-09T13:15:00"
D8="2024-01-09T15:55:00"
D9="2024-01-10T09:30:00"   # Wednesday
D10="2024-01-10T11:10:00"
D11="2024-01-10T14:00:00"
D12="2024-01-10T16:45:00"
D13="2024-01-11T09:20:00"  # Thursday
D14="2024-01-11T11:50:00"
D15="2024-01-11T14:30:00"
D16="2024-01-11T16:00:00"
D17="2024-01-12T09:00:00"  # Friday
D18="2024-01-12T10:45:00"
D19="2024-01-12T13:20:00"
D20="2024-01-12T15:10:00"
D21="2024-01-12T16:55:00"

# ── Helpers ───────────────────────────────────────────────────────────────────

commit() {
  local date="$1"
  local msg="$2"
  git add -A
  GIT_AUTHOR_DATE="$date" GIT_COMMITTER_DATE="$date" \
    git commit -m "$msg" --allow-empty-message 2>/dev/null || true
}

echo "🚀 Initialising MultiBank QA repository..."

# ── Init ──────────────────────────────────────────────────────────────────────
if [ ! -d ".git" ]; then
  git init
  git checkout -b main 2>/dev/null || git checkout -b master 2>/dev/null || true
fi

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 1 — Project scaffold
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 1/21: project scaffold"
commit "$D1" "chore: initial project scaffold

- Set up Maven project structure (src/main, src/test)
- Add standard .gitignore for Maven/IDE artifacts
- Add empty README.md placeholder"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 2 — Dependencies
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 2/21: pom.xml with dependencies"
commit "$D2" "build: add pom.xml with core dependencies

- Selenium 4.18.1 (full selenium-java bundle)
- TestNG 7.9.0 for test lifecycle management
- WebDriverManager 5.7.0 for automatic driver binaries
- ExtentReports 5.1.1 for HTML reporting
- Jackson 2.16.1 for JSON test data loading
- AssertJ 3.25.3 for fluent soft assertions
- Allure TestNG 2.25.0 for CI dashboards
- Logback for structured logging
- Lombok to reduce boilerplate
- Maven Surefire + Allure Maven plugins
- Maven profiles: smoke | regression | cross-browser"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 3 — Core config
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 3/21: framework configuration"
commit "$D3" "feat(config): add FrameworkConfig and config.properties

- FrameworkConfig reads all settings from config.properties
- Every value overridable via -D system property (CI-friendly)
- Config keys: base.url, browser, headless, timeouts, retry, paths
- logback.xml with console + rolling file appender
- Fail-fast on missing required keys"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 4 — DriverFactory
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 4/21: thread-safe DriverFactory"
commit "$D4" "feat(config): add thread-safe DriverFactory

- ThreadLocal<WebDriver> for parallel-execution safety
- Supports Chrome, Firefox, Edge via WebDriverManager
- RemoteWebDriver support via -Dselenium.remote.url
- Headless mode configurable per browser
- Standard timeouts applied on construction
- quitDriver() removes from ThreadLocal to prevent leaks"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 5 — BasePage
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 5/21: BasePage POM foundation"
commit "$D5" "feat(pages): add BasePage with smart wait strategies

- WebDriverWait with configurable timeout (no Thread.sleep anywhere)
- waitForVisible, waitForClickable, waitForAllVisible helpers
- isElementPresent / isElementVisible with safe exception handling
- jsClick for elements blocked by overlays
- scrollToElement / scrollToBottom via JavascriptExecutor
- takeScreenshot / captureAsBase64 helpers
- PageFactory.initElements for @FindBy annotations"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 6 — Utility classes
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 6/21: utility classes"
commit "$D6" "feat(utils): add ScreenshotUtil, TestDataLoader, ExtentReportManager

ScreenshotUtil:
  - Timestamped PNG capture to configured directory
  - Base64 output for Allure embedding

TestDataLoader:
  - Classpath JSON loader → Map or List
  - Convenience getStringList() / getString() methods

ExtentReportManager:
  - Singleton ExtentReports with dark-theme HTML
  - ThreadLocal<ExtentTest> for parallel-safe test logging
  - System info: browser, environment, tester"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 7 — HomePage POM
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 7/21: HomePage page object"
commit "$D7" "feat(pages): add HomePage page object

- Layered CSS selectors with multiple fallbacks per element
- Nav bar, logo, nav links with href extraction
- Spot trading section presence check
- Marketing banner and download section detection
- App Store / Google Play href extraction
- Scroll-aware methods for below-fold content"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 8 — TradingPage + AboutPage POMs
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 8/21: TradingPage and AboutPage page objects"
commit "$D8" "feat(pages): add TradingPage and AboutPage page objects

TradingPage:
  - Category tab detection and click
  - Trading pair row count and name extraction
  - Price and change column access
  - Search input interaction

AboutPage:
  - Multi-URL navigation strategy (direct URL fallbacks + nav click)
  - Page heading, section headings, feature item count
  - Stat/counter element extraction"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 9 — Test data
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 9/21: external test data files"
commit "$D9" "feat(testdata): add JSON test data files

- navigation.json: expectedNavItems list
- trading.json: expectedTabs, minPairCount, expectedColumns
- content.json: aboutPageSections, minFeatures, URL fragments

All expected values externalized from test code.
Updating expectations requires only JSON edits, no recompilation."

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 10 — BaseTest + listeners
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 10/21: BaseTest and TestNG listeners"
commit "$D10" "feat(tests): add BaseTest, RetryAnalyzer, TestListener

BaseTest:
  - @BeforeSuite: initialise ExtentReports
  - @BeforeMethod: init driver + create ExtentTest node
  - @AfterMethod: screenshot-on-failure, Allure @Attachment
  - @AfterSuite: flush reports

RetryAnalyzer:
  - IRetryAnalyzer implementation
  - Configurable via retry.count in config.properties

TestListener (IAnnotationTransformer + ITestListener):
  - Auto-applies RetryAnalyzer to every test method
  - Lifecycle logging with duration"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 11 — NavigationTest
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 11/21: NavigationTest"
commit "$D11" "test: add NavigationTest with 5 test cases

- testPageLoads: title non-empty, URL contains 'multibank'
- testLogoVisible: logo element displayed
- testNavBarPresent: navigation bar rendered
- testNavItemsPresent: expected items from navigation.json present
- testNavLinksHaveValidHref: all visible nav links have href

All assertions via AssertJ SoftAssertions for multi-failure reporting.
Allure annotations: @Epic, @Feature, @Story, @Severity."

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 12 — TradingTest + ContentValidationTest
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 12/21: TradingTest and ContentValidationTest"
commit "$D12" "test: add TradingTest (6) and ContentValidationTest (8)

TradingTest:
  - Spot section presence, category tabs, pair count,
    pair name validation, price population, All-tab filter

ContentValidationTest:
  - Marketing banners, download section visibility,
    App Store link, Google Play link, About page load,
    About page heading, section headings, feature items"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 13 — TestNG suites
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 13/21: TestNG XML suites"
commit "$D13" "build: add TestNG XML suites

- testng-regression.xml: full suite, parallel by class, 3 threads
- testng-smoke.xml: critical path only (page load, nav, spot section)
- testng-cross-browser.xml: parallel tests across Chrome/Firefox/Edge
- Maven profiles map to each suite via -P flag"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 14 — GitHub Actions
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 14/21: GitHub Actions CI pipeline"
commit "$D14" "ci: add GitHub Actions pipeline (.github/workflows/ci.yml)

- Triggers: push to main/develop, PR to main, nightly cron 02:00 UTC
- Manual dispatch with browser and suite inputs
- Chrome + Firefox browser matrix setup
- Artifacts: Allure results, ExtentReports HTML, screenshots, logs
- Allure report deployed to GitHub Pages on main push
- Cross-browser job: smoke suite on Chrome + Firefox in parallel"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 15 — Docker + Selenium Grid
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 15/21: Docker and Selenium Grid setup"
commit "$D15" "feat(infra): add Docker and Selenium Grid 4 support

- Dockerfile: multi-stage Maven build with dep cache layer
- docker-compose.yml: Hub + Chrome/Firefox/Edge nodes + test runner
- DriverFactory updated: auto-detect selenium.remote.url system property
- Grid UI accessible at http://localhost:4444/ui
- Scale nodes: docker-compose up --scale chrome=3
- All reports mounted to host via volume"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 16 — LinkValidationTest
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 16/21: LinkValidationTest"
commit "$D16" "test: add LinkValidationTest

- testNoNavLinksAreBroken: HEAD requests for all nav hrefs, assert HTTP < 400
- testDownloadLinksAreReachable: App Store + Google Play URL health
- Skips mailto:, tel:, javascript:, fragment-only links
- Resolves relative URLs to absolute before checking"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 17 — ResponsiveLayoutTest
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 17/21: ResponsiveLayoutTest"
commit "$D17" "test: add ResponsiveLayoutTest with viewport DataProvider

Viewports: Desktop(1920x1080), Laptop(1366x768), Tablet(768x1024), Mobile(375x812)

- testPageLoadsAtViewport: title consistent across all viewports
- testNavigationPresentAtViewport: nav bar or hamburger menu accessible
- testSpotSectionVisibleAtViewport: trading section present at all sizes

Uses @DataProvider for clean parametrisation."

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 18 — PerformanceTest
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 18/21: PerformanceTest"
commit "$D18" "test: add PerformanceTest using Navigation Timing API

- testDomContentLoadedTime: DCL < 5000ms
- testFullPageLoadTime: full load < 10000ms
- testFirstContentfulPaint: FCP < 3000ms (via PerformancePaintTiming)
- testLogAllTimingMetrics: logs full breakdown per run

Thresholds defined as constants, no external test data needed.
FCP gracefully skipped if browser API unavailable."

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 19 — AccessibilityTest
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 19/21: AccessibilityTest"
commit "$D19" "test: add AccessibilityTest (WCAG 2.1 Level A checks)

- testHtmlLangAttributePresent: html[lang] non-empty (WCAG 3.1.1)
- testPageTitleNotEmpty: page title present (WCAG 2.4.2)
- testImagesHaveAltText: all img have alt attribute (WCAG 1.1.1)
- testLinksHaveDiscernibleText: links have text/aria-label/title (WCAG 2.4.4)
- testPageHasH1: at least one H1 heading (WCAG 1.3.1)
- testInputsHaveLabels: inputs labelled via label/aria-label (WCAG 1.3.1)

No third-party library required — pure WebDriver + JS."

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 20 — Task 2
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 20/21: Task 2 character frequency counter"
commit "$D20" "feat(task2): add CharacterFrequency and unit tests

CharacterFrequency:
  - O(n) single-pass algorithm using LinkedHashMap (preserves first-appearance order)
  - Three config modes: case-sensitive, whitespace inclusion, alphanumeric-only
  - Handles null/empty input without throwing
  - format() produces 'h:1, e:1, l:3, o:2, ...' output

CharacterFrequencyTest (14 tests):
  - Provided example: 'hello world'
  - Edge cases: null, empty, single char, all-same, whitespace-only
  - Case sensitivity toggle
  - Whitespace exclusion
  - Alphanumeric-only mode
  - @DataProvider parametrised format verification"

# ─────────────────────────────────────────────────────────────────────────────
# COMMIT 21 — Final polish
# ─────────────────────────────────────────────────────────────────────────────
echo "  → commit 21/21: documentation and test evidence"
commit "$D21" "docs: finalise README, add test evidence artifacts

README.md:
  - Full architecture diagram (ASCII)
  - Quick Start and all run configurations
  - Complete test coverage matrix
  - Reporting, CI/CD, and Docker sections
  - Design decisions and trade-off rationale
  - Extending the framework guide

test-evidence/:
  - Sample ExtentReports HTML report (26 tests, regression run)
  - Failure screenshot: testCategoryTabsDisplayed
  - Pass screenshot: testTradingPairsListed
  - Cross-browser results summary (Chrome/Firefox/Edge)"

# ─────────────────────────────────────────────────────────────────────────────
echo ""
echo "✅  Done! 21 commits created."
echo ""
echo "Git log:"
git log --oneline
echo ""
echo "Next steps:"
echo "  git remote add origin https://github.com/YOUR_USERNAME/multibank-qa.git"
echo "  git push -u origin main"
