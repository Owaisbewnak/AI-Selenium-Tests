import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrangeHRMLoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/";
    private static final String EXPECTED_LOGIN_PAGE_TITLE = "OrangeHRM";
    private static final String VALID_USERNAME = "Admin";
    private static final String VALID_PASSWORD = "admin123";

    // Locators
    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By dashboardHeader = By.xpath("//h6[normalize-space()='Dashboard']");

    @BeforeClass
    public void setUpChromeDriver() {
        // Selenium Manager (Selenium 4.6+) will resolve the chromedriver binary automatically.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // prefer explicit waits
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterClass(alwaysRun = true)
    public void tearDownAndQuitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(description = "Verify user can log in with valid credentials and reach the Dashboard")
    public void shouldLoginWithValidCredentialsAndSeeDashboard() {
        // Navigate to the login page
        driver.get(BASE_URL);

        // Verify the login page title
        wait.until(ExpectedConditions.titleIs(EXPECTED_LOGIN_PAGE_TITLE));
        Assert.assertEquals(driver.getTitle(), EXPECTED_LOGIN_PAGE_TITLE, "Login page title should match.");

        // Enter valid credentials
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        username.clear();
        username.sendKeys(VALID_USERNAME);

        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        password.clear();
        password.sendKeys(VALID_PASSWORD);

        driver.findElement(loginButton).click();

        // Verify successful login by checking Dashboard visibility
        WebElement dashboard = wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader));
        Assert.assertEquals(dashboard.getText().trim(), "Dashboard", "User should land on the Dashboard after login.");
    }
}