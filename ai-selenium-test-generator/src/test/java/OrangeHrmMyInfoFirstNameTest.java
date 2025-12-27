
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

public class OrangeHrmMyInfoFirstNameTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUpChromeDriver() {
       
        ChromeOptions options = new ChromeOptions();
        // Add any options needed for CI stability
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    @AfterClass(alwaysRun = true)
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(description = "Login to OrangeHRM, navigate to My Info, read First Name, print it, and assert it is not empty")
    public void loginAndVerifyFirstNameIsNotEmpty() {
        // Step 1: Navigate
        driver.get("https://opensource-demo.orangehrmlive.com/");
        // Wait for login form as a signal that the page has loaded
        WebElement usernameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("username"))
        );
        Assert.assertTrue(usernameInput.isDisplayed(), "Login page did not load successfully.");

        // Step 2: Verify Page Title
        Assert.assertEquals(driver.getTitle(), "OrangeHRM", "Unexpected page title.");

        // Step 3: Enter Username and verify populated
        usernameInput.clear();
        usernameInput.sendKeys("Admin");
        Assert.assertEquals(usernameInput.getAttribute("value"), "Admin", "Username was not populated correctly.");

        // Step 4: Enter Password and verify populated (non-empty)
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.clear();
        passwordInput.sendKeys("admin123");
        String passwordValue = passwordInput.getAttribute("value");
        Assert.assertTrue(passwordValue != null && !passwordValue.isEmpty(), "Password was not populated.");

        // Step 5: Click Login
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // Step 6: Verify Dashboard page loaded
        WebElement dashboardHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Dashboard']"))
        );
        Assert.assertTrue(dashboardHeader.isDisplayed(), "Dashboard header was not visible after login.");
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard") || driver.getTitle().contains("OrangeHRM"),
                "URL or title did not indicate Dashboard after login.");

        // Step 7: Click My Info in left sidebar
        WebElement myInfoMenu = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='My Info']"))
        );
        myInfoMenu.click();

        // Step 8: Verify Personal Details section is visible
        WebElement personalDetailsHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Personal Details']"))
        );
        Assert.assertTrue(personalDetailsHeader.isDisplayed(), "Personal Details section header is not displayed.");

        // Step 9: Read First Name value
        WebElement firstNameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("firstName"))
        );
        String firstNameValue = firstNameInput.getAttribute("value");

        // Step 10: Print First Name to console
        System.out.println("First Name value: " + firstNameValue);

        // Step 11: Assert Not Empty (not null, not empty, not whitespace-only)
        Assert.assertNotNull(firstNameValue, "First Name value should not be null.");
        Assert.assertFalse(firstNameValue.trim().isEmpty(), "First Name value should not be empty or whitespace-only.");
    }
}