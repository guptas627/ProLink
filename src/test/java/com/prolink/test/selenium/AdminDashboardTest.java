package com.prolink.test.selenium;

import org.openqa.selenium.By;
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

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AdminDashboardTest {

    private WebDriver driver;
    private WebDriverWait wait;

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
    }

    @Test
    public void testAdminDashboard() {
        // Step 1: Log in as admin
        driver.get("http://localhost:8080/login");

        WebElement loginUsernameField = driver.findElement(By.cssSelector("#username"));
        loginUsernameField.sendKeys("Abhainn");

        WebElement loginPasswordField = driver.findElement(By.cssSelector("#password"));
        loginPasswordField.sendKeys("admin");

        WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();

        // Wait for the admin dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".admin-dashboard")));

        // Step 2: Open the Skills Chart modal
        try {
            // Wait for the "Skill's Chart" button to be clickable
            WebElement chartsButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-charts")
            ));
            chartsButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Skill's Chart' button.", e);
        }

        // Wait for the Skills Chart modal to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".modal-overlay-chart")));

        // Step 3: Close the Skills Chart modal
        WebElement closeChartsModalButton = driver.findElement(By.cssSelector(".modal-overlay-chart .close-btn"));
        closeChartsModalButton.click();

        // Wait for the Skills Chart modal to close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal-overlay-chart")));

        // Step 4: Select a user and view their profile
        try {
            // Wait for the first user card to be clickable
            WebElement firstUserCard = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".user-card")
            ));
            WebElement viewProfileButton = firstUserCard.findElement(By.cssSelector(".btn-profile"));
            viewProfileButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Show Profile' button.", e);
        }

        // Wait for the profile modal to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".modal-overlay-profile")));

        // Step 5: Close the profile modal
        WebElement closeProfileModalButton = driver.findElement(By.cssSelector(".modal-overlay-profile .close-btn"));
        closeProfileModalButton.click();

        // Wait for the profile modal to close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal-overlay-profile")));

        // Step 6: Select a user for download
        try {
            // Wait for the first user's checkbox to be clickable
            WebElement firstUserCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".user-card input[type='checkbox']")
            ));
            firstUserCheckbox.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the user's checkbox.", e);
        }

        // Step 7: Download selected users in JSON format
        try {
            // Wait for the "Download JSON" button to be clickable
            WebElement downloadJsonButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-json")
            ));
            downloadJsonButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Download JSON' button.", e);
        }

        // Step 8: Download selected users in XML format
        try {
            // Wait for the "Download XML" button to be clickable
            WebElement downloadXmlButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-xml")
            ));
            downloadXmlButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Download XML' button.", e);
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