package com.prolink.test.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.github.javafaker.Faker;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class JobsDashboardTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private Faker faker;

    @BeforeEach
    public void setUp() {
        // Set up ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Configure ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--remote-allow-origins=*");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // Initialize ChromeDriver with options
        driver = new ChromeDriver(options);

        // Maximize the browser window
        driver.manage().window().maximize();

        // Initialize WebDriverWait with a timeout of 20 seconds
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Initialize Faker for generating random data
        faker = new Faker();
    }

    @Test
    public void testJobsDashboard() {
        // Step 1: Register a new user
        String fullName = faker.name().fullName();
        String username = faker.name().username().replace(".", ""); // Remove dots from username
        String email = faker.internet().emailAddress();
        String password = "Password123!"; // Use a strong password

        // Register the user
        driver.get("http://localhost:8080/register");

        driver.findElement(By.cssSelector("#fullname")).sendKeys(fullName);
        driver.findElement(By.cssSelector("#username")).sendKeys(username);
        driver.findElement(By.cssSelector("#email")).sendKeys(email);
        driver.findElement(By.cssSelector("#password")).sendKeys(password);
        driver.findElement(By.cssSelector("#confirmPassword")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for the registration to complete
        wait.until(d -> driver.getCurrentUrl().contains("login")); // Adjust the condition as needed

        // Step 2: Log in as the registered user
        WebElement loginUsernameField = driver.findElement(By.cssSelector("#username"));
        loginUsernameField.sendKeys(username);

        WebElement loginPasswordField = driver.findElement(By.cssSelector("#password"));
        loginPasswordField.sendKeys(password);

        WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();

        // Wait for the dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-dashboard")));

        // Step 3: Navigate to the Jobs page
        try {
            // Wait for the "Jobs" button to be clickable
            WebElement jobsButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.//span/text(), 'Jobs')]")
            ));
            jobsButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Jobs' button.", e);
        }

        // Wait for the Jobs Dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".job-dashboard")));

        // Step 4: Create a job
        String jobTitle = "Software Engineer";
        String jobDescription = "Looking for a skilled software engineer with experience in Java and Spring Boot.";
        String jobLocation = "New York, NY";

        WebElement jobTitleInput = driver.findElement(By.cssSelector(".create-job input[placeholder='Job title *']"));
        jobTitleInput.sendKeys(jobTitle);

        WebElement jobDescriptionInput = driver.findElement(By.cssSelector(".create-job textarea[placeholder='Job description *']"));
        jobDescriptionInput.sendKeys(jobDescription);

        WebElement jobLocationInput = driver.findElement(By.cssSelector(".create-job input[placeholder='Location *']"));
        jobLocationInput.sendKeys(jobLocation);

        WebElement saveButton = driver.findElement(By.cssSelector(".create-job button[type='submit']"));
        saveButton.click();

        // Wait for the job to be created
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.cssSelector(".jobs-list .job-card h3"), jobTitle
        ));

        // Step 5: Apply for the created job
        try {
            // Wait for the "Apply" button to be clickable
            WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'job-card')]//button[contains(text(), 'Apply')]")
            ));
            applyButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Apply' button.", e);
        }

        
    }

    @AfterEach
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }
}