package com.prolink.test.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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

public class UserRegistrationAndLoginTest {

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
    public void testUserRegistrationAndLogin() {
        // Generate random user data
        String fullName = faker.name().fullName();
        String username = faker.name().username().replace(".", ""); // Remove dots from username
        String email = faker.internet().emailAddress();
        String password = "Password123!"; // Use a strong password

        // Step 1: Open the website
        driver.get("http://localhost:8080/register");

        // Step 2: Enter random "Full Name"
        WebElement fullNameField = driver.findElement(By.cssSelector("#fullname"));
        fullNameField.sendKeys(fullName);

        // Step 3: Enter random "Username"
        WebElement usernameField = driver.findElement(By.cssSelector("#username"));
        usernameField.sendKeys(username);

        // Step 4: Enter random "Email"
        WebElement emailField = driver.findElement(By.cssSelector("#email"));
        emailField.sendKeys(email);

        // Step 5: Enter "Password"
        WebElement passwordField = driver.findElement(By.cssSelector("#password"));
        passwordField.sendKeys(password);

        // Step 6: Enter "Confirm Password"
        WebElement confirmPasswordField = driver.findElement(By.cssSelector("#confirmPassword"));
        confirmPasswordField.sendKeys(password);

        // Step 7: Click on "Sign Up"
        WebElement signUpButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signUpButton.click();

     // Wait for the registration to complete
        wait.until(d -> driver.getCurrentUrl().contains("login")); // Adjust the condition as needed

        // Step 8: Enter "Username" (Login Page)
        WebElement loginUsernameField = driver.findElement(By.cssSelector("#username"));
        loginUsernameField.sendKeys(username);

        // Step 9: Enter "Password" (Login Page)
        WebElement loginPasswordField = driver.findElement(By.cssSelector("#password"));
        loginPasswordField.sendKeys(password);

        // Step 10: Click on "Sign in"
        WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();

     // Wait for the dashboard to load and check for the text "Work Experience"
        try {
            System.out.println("Waiting for dashboard...");
            
            // Wait for the element containing "Work Experience" to be visible
            WebElement workExperienceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Work Experience')]")));
            
            System.out.println("Dashboard loaded successfully.");
            System.out.println("Text found: " + workExperienceElement.getText());

            // Verify successful login by checking the text "Work Experience"
            assertTrue(workExperienceElement.isDisplayed(), "Login failed: 'Work Experience' section not found on the dashboard.");
        } catch (TimeoutException e) {
            System.out.println("Timeout: Dashboard did not load. Current URL: " + driver.getCurrentUrl());
            throw e;
        }
    }
}