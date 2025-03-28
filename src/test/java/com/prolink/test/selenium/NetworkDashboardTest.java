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

public class NetworkDashboardTest {

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
    public void testNetworkDashboard() {
        // Step 1: Register and log in a new user (Main User)
        String fullName = faker.name().fullName();
        String username = faker.name().username().replace(".", ""); // Remove dots from username
        String email = faker.internet().emailAddress();
        String password = "Password123!"; // Use a strong password

        // Register the main user
        driver.get("http://localhost:8080/register");

        driver.findElement(By.cssSelector("#fullname")).sendKeys(fullName);
        driver.findElement(By.cssSelector("#username")).sendKeys(username);
        driver.findElement(By.cssSelector("#email")).sendKeys(email);
        driver.findElement(By.cssSelector("#password")).sendKeys(password);
        driver.findElement(By.cssSelector("#confirmPassword")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for the registration to complete
        wait.until(d -> driver.getCurrentUrl().contains("login")); // Adjust the condition as needed

        // Step 2: Log in as the main user
        WebElement loginUsernameField = driver.findElement(By.cssSelector("#username"));
        loginUsernameField.sendKeys(username);

        WebElement loginPasswordField = driver.findElement(By.cssSelector("#password"));
        loginPasswordField.sendKeys(password);

        WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();

        // Wait for the dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-dashboard")));

        // Step 3: Register and log in as a new random user (Second User)
        String secondUserFullName = faker.name().fullName(); // Random full name
        String secondUserUsername = faker.name().username().replace(".", ""); // Random username
        String secondUserEmail = faker.internet().emailAddress(); // Random email
        String secondUserPassword = "Password123!"; // Use a strong password

        // Log out of the main user
        driver.findElement(By.cssSelector(".profile-dropdown")).click();
        driver.findElement(By.xpath("//button[contains(text(), 'Logout')]")).click();

        // Wait for the login page to load
        wait.until(ExpectedConditions.urlContains("login"));

        // Register the second user
        driver.get("http://localhost:8080/register");

        driver.findElement(By.cssSelector("#fullname")).sendKeys(secondUserFullName);
        driver.findElement(By.cssSelector("#username")).sendKeys(secondUserUsername);
        driver.findElement(By.cssSelector("#email")).sendKeys(secondUserEmail);
        driver.findElement(By.cssSelector("#password")).sendKeys(secondUserPassword);
        driver.findElement(By.cssSelector("#confirmPassword")).sendKeys(secondUserPassword);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for the registration to complete
        wait.until(d -> driver.getCurrentUrl().contains("login")); // Adjust the condition as needed

        // Log in as the second user
        loginUsernameField = driver.findElement(By.cssSelector("#username"));
        loginUsernameField.sendKeys(secondUserUsername);

        loginPasswordField = driver.findElement(By.cssSelector("#password"));
        loginPasswordField.sendKeys(secondUserPassword);

        signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();

        // Wait for the dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-dashboard")));

        // Step 4: Log out of the second user's account
        driver.findElement(By.cssSelector(".profile-dropdown")).click();
        driver.findElement(By.xpath("//button[contains(text(), 'Logout')]")).click();

        // Wait for the login page to load
        wait.until(ExpectedConditions.urlContains("login"));

        // Step 5: Log in as the main user again
        loginUsernameField = driver.findElement(By.cssSelector("#username"));
        loginUsernameField.sendKeys(username);

        loginPasswordField = driver.findElement(By.cssSelector("#password"));
        loginPasswordField.sendKeys(password);

        signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();

        // Wait for the dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-dashboard")));

        // Step 6: Navigate to the Network Dashboard
        try {
            // Wait for the "My Network" button to be clickable
            WebElement myNetworkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.//span/text(), 'My Network')]")
            ));
            myNetworkButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'My Network' button.", e);
        }

        // Wait for the Network Dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".network-dashboard")));

        // Step 7: Search for the second user
        WebElement searchBar = driver.findElement(By.cssSelector(".search-bar input"));
        searchBar.sendKeys(secondUserFullName); // Search by the second user's full name
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Step 8: Send a connection request to the second user
        WebElement connectButton = driver.findElement(By.xpath("//button[contains(text(), 'Connect')]"));
        
        // Scroll the "Connect" button into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", connectButton);
        
        // Wait for the button to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(connectButton));
        
        // Use JavaScript to click the button
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", connectButton);

        // Wait for the toast notification to appear
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".toast.success")
            ));
            assertTrue(toast.isDisplayed(), "Toast notification for connection request not displayed.");
            assertEquals("✅ Connection request sent!", toast.getText(), "Toast message is incorrect.");
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate the toast notification.", e);
        }

        // Step 9: Log out of the main user
        driver.findElement(By.cssSelector(".profile-dropdown")).click();
        driver.findElement(By.xpath("//button[contains(text(), 'Logout')]")).click();

        // Wait for the login page to load
        wait.until(ExpectedConditions.urlContains("login"));

        // Step 10: Log in as the second user
        loginUsernameField = driver.findElement(By.cssSelector("#username"));
        loginUsernameField.sendKeys(secondUserUsername);

        loginPasswordField = driver.findElement(By.cssSelector("#password"));
        loginPasswordField.sendKeys(secondUserPassword);

        signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();

        // Wait for the dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-dashboard")));

        // Step 11: Navigate to the Network Dashboard
        try {
            // Wait for the "My Network" button to be clickable
            WebElement myNetworkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.//span/text(), 'My Network')]")
            ));
            myNetworkButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'My Network' button.", e);
        }

        // Wait for the Network Dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".network-dashboard")));

        // Step 12: Accept the pending connection request
        try {
            // Wait for the "Accept" button to be clickable
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Accept')]")
            ));
            
            // Scroll the "Accept" button into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", acceptButton);
            
            // Use JavaScript to click the button
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptButton);

            // Wait for the toast notification to appear
            WebElement acceptToast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".toast.success")
            ));
            assertTrue(acceptToast.isDisplayed(), "Toast notification for accepted request not displayed.");
            assertEquals("✅ Connection accepted!", acceptToast.getText(), "Toast message is incorrect.");
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Accept' button.", e);
        }

        // Step 13: Verify connections
        try {
            // Wait for the connections section to be visible
            WebElement connectionsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".connections")
            ));
            assertTrue(connectionsSection.isDisplayed(), "Connections section not found.");
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate the connections section.", e);
        }
        
     // Step 14: Navigate to the Messaging Page
        try {
            // Wait for the "Messaging" button to be clickable
            WebElement messagingButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.//span/text(), 'Messaging')]")
            ));
            messagingButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Messaging' button.", e);
        }

        // Wait for the Messaging Dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".messaging-dashboard")));

        // Step 15: Send a message to the second user
        String messageText = "Hello, this is a test message!";
        WebElement messageInput = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector(".chat-input input")
        ));
        messageInput.sendKeys(messageText);

        WebElement sendButton = driver.findElement(By.cssSelector(".chat-input button"));
        sendButton.click();

        // Wait for the message to appear in the chat window
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.cssSelector(".chat-messages .sent p"), messageText
        ));

        // Step 16: Verify the message is sent
        WebElement sentMessage = driver.findElement(By.cssSelector(".chat-messages .sent p"));
        assertEquals(messageText, sentMessage.getText(), "Sent message text does not match.");

        // Step 17: Log out of the second user's account
        driver.findElement(By.cssSelector(".profile-dropdown")).click();
        driver.findElement(By.xpath("//button[contains(text(), 'Logout')]")).click();

        // Wait for the login page to load
        wait.until(ExpectedConditions.urlContains("login"));

        // Step 18: Log in as the main user again
        WebElement loginUsernameField1 = driver.findElement(By.cssSelector("#username"));
        loginUsernameField1.sendKeys(username);

        WebElement loginPasswordField1 = driver.findElement(By.cssSelector("#password"));
        loginPasswordField1.sendKeys(password);

        WebElement signInButton1 = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton1.click();

        // Wait for the dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-dashboard")));

        // Step 19: Navigate to the Messaging Page
        try {
            // Wait for the "Messaging" button to be clickable
            WebElement messagingButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.//span/text(), 'Messaging')]")
            ));
            messagingButton.click();
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate or click the 'Messaging' button.", e);
        }

        // Wait for the Messaging Dashboard to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".messaging-dashboard")));

        // Step 20: Verify the received message
        try {
            // Wait for the received message to appear in the chat window
            WebElement receivedMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".chat-messages .received p")
            ));
            assertEquals(messageText, receivedMessage.getText(), "Received message text does not match.");
        } catch (Exception e) {
            // Print the page source for debugging
            System.out.println("Page Source: " + driver.getPageSource());
            throw new RuntimeException("Failed to locate the received message.", e);
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