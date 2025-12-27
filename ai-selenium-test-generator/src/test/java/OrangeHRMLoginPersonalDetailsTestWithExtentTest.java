

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class OrangeHRMLoginPersonalDetailsTestWithExtentTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private ExtentReports extent;

    @BeforeClass
    public void setUp() {
        ensureDirectories();

        ExtentSparkReporter spark = new ExtentSparkReporter("reports/OrangeHRM_Login_PersonalDetails_Report.html");
        spark.config().setDocumentTitle("OrangeHRM - Personal Details Verification");
        spark.config().setReportName("Login and Verify Personal Details - First Name Not Empty");

        extent = new ExtentReports();
        extent.attachReporter(spark);

        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (extent != null) {
            extent.flush();
        }
    }

    @Test
    public void loginAndVerifyFirstNameIsNotEmpty() throws Exception{
        ExtentTest test = extent.createTest(
                "Login to OrangeHRM and Verify Personal Details - First Name Not Empty",
                "Registered user logs in to the OrangeHRM demo site, navigates to My Info, reads the First Name, prints it, and asserts it is not empty.");

        try {
            // Step 1: Navigate
            String baseUrl = "https://opensource-demo.orangehrmlive.com/";
            test.info("Step 1: Navigate to " + baseUrl);
            driver.get(baseUrl);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            test.pass("Application loads successfully. Login form is visible.");

            // Step 2: Verify Page Title
            test.info("Step 2: Verify page title equals 'OrangeHRM'.");
            String actualTitle = driver.getTitle();
            Assert.assertEquals(actualTitle, "OrangeHRM", "Page title should be 'OrangeHRM'.");
            test.pass("Page title validated: " + actualTitle);

            // Step 3: Enter Username
            test.info("Step 3: Enter username 'Admin'.");
            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            usernameInput.clear();
            usernameInput.sendKeys("Admin");
            Assert.assertEquals(usernameInput.getAttribute("value"), "Admin", "Username value should be 'Admin'.");
            test.pass("Username value is populated with 'Admin'.");

            // Step 4: Enter Password
            test.info("Step 4: Enter password.");
            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordInput.clear();
            passwordInput.sendKeys("admin123");
            Assert.assertTrue(passwordInput.getAttribute("value").length() > 0, "Password should be populated.");
            test.pass("Password value is populated.");

            // Step 5: Click Login
            test.info("Step 5: Click the Login button.");
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            loginButton.click();
            test.pass("Login request is submitted.");

            // Step 6: Verify Dashboard Loaded
            test.info("Step 6: Verify Dashboard is displayed.");
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Dashboard']")),
                    ExpectedConditions.urlContains("/dashboard")));
            boolean dashboardHeaderVisible = !driver.findElements(By.xpath("//h6[normalize-space()='Dashboard']")).isEmpty()
                    && driver.findElement(By.xpath("//h6[normalize-space()='Dashboard']")).isDisplayed();
            Assert.assertTrue(dashboardHeaderVisible || driver.getCurrentUrl().contains("dashboard"),
                    "Dashboard should be displayed.");
            test.pass("Dashboard is displayed. Current URL: " + driver.getCurrentUrl());

            // Step 7: Click My Info
            test.info("Step 7: Click 'My Info' menu item in the left sidebar.");
            WebElement myInfoMenu = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='My Info']")));
            myInfoMenu.click();
            test.pass("Navigation to My Info page is initiated.");

            // Step 8: Verify Personal Details section visible
            test.info("Step 8: Verify 'Personal Details' section header is displayed.");
            WebElement personalDetailsHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h6[normalize-space()='Personal Details']")));
            Assert.assertTrue(personalDetailsHeader.isDisplayed(), "'Personal Details' section header should be displayed.");
            test.pass("'Personal Details' section header is displayed.");

            // Step 9: Read First Name
            test.info("Step 9: Read the value of the 'First Name' input field.");
            WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));
            String firstNameValue = firstNameInput.getAttribute("value");
            test.pass("First Name value captured: " + firstNameValue);

            // Step 10: Print to Console
            test.info("Step 10: Print the 'First Name' value to the console.");
            System.out.println("First Name value: " + firstNameValue);
            test.pass("First Name value printed to console.");

            // Step 11: Assert Not Empty
            test.info("Step 11: Assert that 'First Name' is not null, not empty, and not whitespace-only.");
            Assert.assertTrue(firstNameValue != null && !firstNameValue.trim().isEmpty(),
                    "First Name value should not be null/empty/whitespace-only.");
            test.pass("Assertion passed: First Name value is present and non-empty.");

        } catch (Throwable t) {
            String screenshotPath = captureScreenshot("Login_PersonalDetails_Failure");
            try {
                ExtentTest testNode = test.fail("Test failed with error: " + t.getMessage(),
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                testNode.info("Screenshot saved at: " + screenshotPath);
            } catch (Exception e) {
                test.fail("Failed to attach screenshot due to IO error: " + e.getMessage());
            }
            throw new AssertionError(t);
        }
    }

    private String captureScreenshot(String name) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String filePath = "reports/screenshots/" + name + "_" + timestamp + ".png";
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), Paths.get(filePath));
            return filePath;
        } catch (IOException e) {
            return "Failed to capture screenshot: " + e.getMessage();
        }
    }

    private void ensureDirectories() {
        try {
            Files.createDirectories(Paths.get("reports/screenshots"));
        } catch (IOException ignored) {
        }
    }
}