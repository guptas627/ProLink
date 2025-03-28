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

public class UserDashboardTest {

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
    public void testUserDashboard() {
        // Generate random user data
        String fullName = faker.name().fullName();
        String username = faker.name().username().replace(".", ""); // Remove dots from username
        String email = faker.internet().emailAddress();
        String password = "Password123!"; // Use a strong password

        // Step 1: Open the registration page
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

        // Wait for the dashboard to load
        try {
            System.out.println("Waiting for dashboard...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-dashboard")));
            System.out.println("Dashboard loaded successfully.");
        } catch (TimeoutException e) {
            System.out.println("Timeout: Dashboard did not load. Current URL: " + driver.getCurrentUrl());
            throw e;
        }

        // Step 11: Verify the dashboard elements
        WebElement dashboardElement = driver.findElement(By.cssSelector(".user-dashboard"));
        assertTrue(dashboardElement.isDisplayed(), "Dashboard not displayed.");

        // Verify the "Work Experience" section
        WebElement workExperienceSection = driver.findElement(By.xpath("//h3[contains(text(), 'Work Experience')]"));
        assertTrue(workExperienceSection.isDisplayed(), "Work Experience section not found.");

        // Verify the "Education" section
        WebElement educationSection = driver.findElement(By.xpath("//h3[contains(text(), 'Education')]"));
        assertTrue(educationSection.isDisplayed(), "Education section not found.");

        // Verify the "Skills" section
        WebElement skillsSection = driver.findElement(By.xpath("//h3[contains(text(), 'Skills')]"));
        assertTrue(skillsSection.isDisplayed(), "Skills section not found.");

        // Step 12: Add a new work experience
        WebElement addWorkExperienceButton = driver.findElement(By.xpath("//h3[contains(text(), 'Work Experience')]/following-sibling::button[contains(text(), '+ Add')]"));
        addWorkExperienceButton.click();

        WebElement workExperienceInput = driver.findElement(By.cssSelector(".input-box"));
        workExperienceInput.sendKeys("Software Engineer at ProLink");

        WebElement saveWorkExperienceButton = driver.findElement(By.xpath("//input[@class='input-box']/following-sibling::button[contains(text(), 'Save')]"));
        saveWorkExperienceButton.click();

        // Verify the new work experience is added
        WebElement newWorkExperience = driver.findElement(By.xpath("//li[contains(text(), 'Software Engineer at ProLink')]"));
        assertTrue(newWorkExperience.isDisplayed(), "New work experience not added.");

        // Step 13: Add a new education
        WebElement addEducationButton = driver.findElement(By.xpath("//h3[contains(text(), 'Education')]/following-sibling::button[contains(text(), '+ Add')]"));
        addEducationButton.click();

        WebElement educationInput = driver.findElement(By.cssSelector(".input-box"));
        educationInput.sendKeys("Bachelor of Science in Computer Science");

        WebElement saveEducationButton = driver.findElement(By.xpath("//input[@class='input-box']/following-sibling::button[contains(text(), 'Save')]"));
        saveEducationButton.click();

        // Verify the new education is added
        WebElement newEducation = driver.findElement(By.xpath("//li[contains(text(), 'Bachelor of Science in Computer Science')]"));
        assertTrue(newEducation.isDisplayed(), "New education not added.");

        // Step 14: Add a new skill
        WebElement skillsDropdown = driver.findElement(By.cssSelector(".dropdown"));
        skillsDropdown.click();

        WebElement skillOption = driver.findElement(By.xpath("//option[contains(text(), 'JavaScript')]"));
        skillOption.click();

        // Verify the new skill is added
        WebElement newSkill = driver.findElement(By.xpath("//li[contains(text(), 'JavaScript')]"));
        assertTrue(newSkill.isDisplayed(), "New skill not added.");
    }

    @AfterEach
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }
}