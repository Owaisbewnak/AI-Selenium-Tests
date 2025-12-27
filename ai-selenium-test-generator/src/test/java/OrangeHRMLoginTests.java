
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class OrangeHRMLoginTests extends BaseTest {

    @Test(description = "TC001_Login_Success_Dashboard_Visible: Verify that a registered user can log in with valid credentials and see the Dashboard.")
    public void TC001_Login_Success_Dashboard_Visible() {
        LoginPage loginPage = new LoginPage(driver, wait);

        // Step 1: Navigate + validation
        Assert.assertTrue(loginPage.isLoaded(), "Login page loads successfully");

        // Step 2: Verify page title
        Assert.assertEquals(driver.getTitle(), "OrangeHRM", "Page title equals 'OrangeHRM'");

        // Step 3: Enter username
        loginPage.typeUsername("Admin");
        Assert.assertEquals(loginPage.getUsernameValue(), "Admin", "Username value is entered");

        // Step 4: Enter password
        loginPage.typePassword("admin123");
        Assert.assertEquals(loginPage.getPasswordValue(), "admin123", "Password value is entered");

        // Step 5: Click login and validate dashboard
        loginPage.clickLogin();
        DashboardPage dashboardPage = new DashboardPage(driver, wait);
        Assert.assertTrue(dashboardPage.waitUntilLoaded(), "User is redirected to the dashboard");
        Assert.assertTrue(dashboardPage.isHeaderVisible(), "Dashboard header with text 'Dashboard' is visible");
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"), "URL contains '/dashboard'");
    }

    @Test(description = "TC002_Login_Success_Validate_Title_and_Dashboard_Elements: Verify successful login and validate title and key dashboard elements.")
    public void TC002_Login_Success_Validate_Title_and_Dashboard_Elements() {
        LoginPage loginPage = new LoginPage(driver, wait);

        // Step 1: Navigate + validation
        Assert.assertTrue(loginPage.isLoaded(), "Login page loads successfully");

        // Step 2: Verify page title
        Assert.assertEquals(driver.getTitle(), "OrangeHRM", "Page title equals 'OrangeHRM'");

        // Step 3 and 4: Enter valid credentials
        loginPage.typeUsername("Admin");
        Assert.assertEquals(loginPage.getUsernameValue(), "Admin", "Username value is entered");
        loginPage.typePassword("admin123");
        Assert.assertEquals(loginPage.getPasswordValue(), "admin123", "Password value is entered");

        // Step 5: Login and validate elements
        loginPage.clickLogin();
        DashboardPage dashboardPage = new DashboardPage(driver, wait);
        Assert.assertTrue(dashboardPage.waitUntilLoaded(), "User is redirected to the dashboard");
        Assert.assertEquals(driver.getTitle(), "OrangeHRM", "Page title remains 'OrangeHRM'");
        Assert.assertTrue(dashboardPage.isTopNavVisible(), "Top navigation bar is visible");
        Assert.assertTrue(dashboardPage.isUserMenuVisible(), "User profile/menu icon is visible");
    }

    @Test(description = "TC003_Login_Failure_Invalid_Credentials: Verify that login fails with invalid credentials and an appropriate error message is displayed.")
    public void TC003_Login_Failure_Invalid_Credentials() {
        LoginPage loginPage = new LoginPage(driver, wait);

        // Step 1: Navigate + validation
        Assert.assertTrue(loginPage.isLoaded(), "Login page loads successfully");

        // Step 2: Verify page title
        Assert.assertEquals(driver.getTitle(), "OrangeHRM", "Page title equals 'OrangeHRM'");

        // Step 3 and 4: Enter invalid password
        loginPage.typeUsername("Admin");
        Assert.assertEquals(loginPage.getUsernameValue(), "Admin", "Username value is entered");
        loginPage.typePassword("wrongPass123");
        Assert.assertEquals(loginPage.getPasswordValue(), "wrongPass123", "Password value is entered");

        // Step 5: Click login and validate error state
        loginPage.clickLogin();
        Assert.assertTrue(loginPage.isErrorVisible(), "Error message is displayed");
        Assert.assertEquals(loginPage.getErrorText(), "Invalid credentials", "Error message 'Invalid credentials' is displayed");

        // User remains on login page (login button still visible and URL should not be dashboard)
        Assert.assertTrue(loginPage.isLoginButtonVisible(), "User remains on login page (login button visible)");
        Assert.assertFalse(driver.getCurrentUrl().contains("/dashboard"), "URL does not contain '/dashboard'");

        // Dashboard not visible
        boolean dashboardHeaderPresent = driver.findElements(DashboardPage.HEADER_DASHBOARD).size() > 0;
        Assert.assertFalse(dashboardHeaderPresent, "Dashboard is not visible");

        // Page title remains 'OrangeHRM'
        Assert.assertEquals(driver.getTitle(), "OrangeHRM", "Page title remains 'OrangeHRM'");
    }
}

/* ========================== Base and Page Objects ========================== */

class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final String baseUrl = "https://opensource-demo.orangehrmlive.com/";

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @BeforeMethod(alwaysRun = true)
    public void navigateToLogin() {
        driver.manage().deleteAllCookies();
        driver.get(baseUrl);
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}

class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameInput = By.cssSelector("input[name='username']");
    private final By passwordInput = By.cssSelector("input[name='password']");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By errorText = By.xpath("//*[contains(@class,'oxd-alert-content-text') and normalize-space()='Invalid credentials']");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public LoginPage typeUsername(String username) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(usernameInput));
        el.clear();
        el.sendKeys(username);
        return this;
    }

    public LoginPage typePassword(String password) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(passwordInput));
        el.clear();
        el.sendKeys(password);
        return this;
    }

    public String getUsernameValue() {
        return driver.findElement(usernameInput).getAttribute("value");
    }

    public String getPasswordValue() {
        return driver.findElement(passwordInput).getAttribute("value");
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    public boolean isLoginButtonVisible() {
        try {
            return driver.findElement(loginButton).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isErrorVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorText)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getErrorText() {
        try {
            return driver.findElement(errorText).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}

class DashboardPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Exposed for a negative assertion in test 3 without waiting
    static final By HEADER_DASHBOARD = By.xpath("//h6[normalize-space()='Dashboard']");
    private final By topNavBar = By.cssSelector("div.oxd-topbar-body");
    private final By userMenuIcon = By.cssSelector("span.oxd-userdropdown-tab");

    public DashboardPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean waitUntilLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(HEADER_DASHBOARD)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isHeaderVisible() {
        return isVisible(HEADER_DASHBOARD);
    }

    public boolean isTopNavVisible() {
        return isVisible(topNavBar);
    }

    public boolean isUserMenuVisible() {
        return isVisible(userMenuIcon);
    }

    private boolean isVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }
}