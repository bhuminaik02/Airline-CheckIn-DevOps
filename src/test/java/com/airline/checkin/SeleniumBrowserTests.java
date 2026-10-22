package com.airline.checkin;

import com.airline.checkin.booking.Booking;
import com.airline.checkin.booking.BookingRepository;
import com.airline.checkin.booking.BookingStatus;
import com.airline.checkin.booking.FlightSeatReservation;
import com.airline.checkin.booking.FlightSeatReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.http.ClientConfig;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@Tag("selenium")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:selenium;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
class SeleniumBrowserTests {

    private static final String BOOKING_REFERENCE = "DEMOSEL1001";
    private static final String OCCUPIED_BOOKING_REFERENCE = "DEMOSEL1002";
    private static final String SURNAME = "Traveler";
    private static final String SCREENSHOT_DIRECTORY = "target/selenium-output/screenshots";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DRIVER_START_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DRIVER_COMMAND_TIMEOUT = Duration.ofSeconds(60);

    @RegisterExtension
    static final FailureScreenshotExtension FAILURE_SCREENSHOT = new FailureScreenshotExtension();

    @LocalServerPort
    private int port;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightSeatReservationRepository reservationRepository;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void startBrowserWithIsolatedFictionalBookings() {
        resetFictionalBookings();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu", "--window-size=1440,1000");
        String chromeBinary = System.getenv("CHROME_BINARY");
        if (chromeBinary != null && !chromeBinary.isBlank()) {
            options.setBinary(chromeBinary);
        }
        ChromeDriverService service = new ChromeDriverService.Builder()
                .withTimeout(DRIVER_START_TIMEOUT)
                .withLogOutput(System.out)
                .build();
        ClientConfig clientConfig = ClientConfig.defaultConfig()
                .connectionTimeout(Duration.ofSeconds(15))
                .readTimeout(DRIVER_COMMAND_TIMEOUT);
        try {
            driver = new ChromeDriver(service, options, clientConfig);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("ChromeDriver or the Chrome session failed to start within the "
                    + "configured timeouts (30 seconds for driver service startup, 60 seconds for a WebDriver "
                    + "command). Selenium Manager network requests are limited to 45 seconds (SE_TIMEOUT); "
                    + "check Chrome access and the Jenkins account's driver cache or network access.", exception);
        }
        wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void loadsTheMainPortalForm() {
        openPortal();

        assertEquals("Airline Check-In Demonstration Portal", driver.getTitle());
        assertTrue(driver.findElement(By.id("lookup-form")).isDisplayed());
        assertTrue(driver.findElement(By.id("reference")).isDisplayed());
        assertTrue(driver.findElement(By.id("surname")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("#lookup-form button[type='submit']")).isDisplayed());
    }

    @Test
    void looksUpTheFictionalDemoBooking() {
        lookupBooking(BOOKING_REFERENCE, SURNAME);

        assertTrue(wait.until(visibilityOfElementLocated(By.id("booking-panel"))).isDisplayed());
        assertEquals(BOOKING_REFERENCE, driver.findElement(By.id("booking-reference")).getText());
        assertEquals("Fictional Traveler", driver.findElement(By.id("passenger")).getText());
        assertEquals("WEB101", driver.findElement(By.id("flight-number")).getText());
        assertTrue(driver.findElement(By.id("message")).getText().contains("Booking found."));
    }

    @Test
    void showsAnErrorForAMissingBooking() {
        lookupBooking("DEMOSEL9999", "Nobody");

        assertTrue(wait.until(textToBePresentInElementLocated(By.id("message"),
                "No booking matches that reference and surname.")));
        assertFalse(driver.findElement(By.id("booking-panel")).isDisplayed());
    }

    @Test
    void assignsAnAvailableSeatAndChecksIn() {
        lookupBooking(BOOKING_REFERENCE, SURNAME);
        wait.until(visibilityOfElementLocated(By.id("seat-form")));

        new Select(driver.findElement(By.id("seat-choice"))).selectByValue("13C");
        driver.findElement(By.cssSelector("#seat-form button[type='submit']")).click();
        assertTrue(wait.until(textToBePresentInElementLocated(By.id("message"), "Seat 13C assigned.")));
        assertEquals("13C", driver.findElement(By.id("seat-number")).getText());

        driver.findElement(By.cssSelector("#check-in-form button[type='submit']")).click();
        assertTrue(wait.until(textToBePresentInElementLocated(By.id("booking-status"), "CHECKED IN")));
        assertTrue(driver.findElement(By.id("boarding-pass")).isDisplayed());
        assertEquals("13C", driver.findElement(By.id("pass-seat")).getText());
        assertTrue(driver.findElement(By.cssSelector(".pass-warning")).getText().contains("NOT VALID FOR TRAVEL"));
        assertEquals(BookingStatus.CHECKED_IN, bookingRepository.findByBookingReferenceIgnoreCase(BOOKING_REFERENCE)
                .orElseThrow().getStatus());
    }

    @Test
    void rejectsAnOccupiedSeatAndLeavesBookingUnchanged() {
        lookupBooking(BOOKING_REFERENCE, SURNAME);
        wait.until(visibilityOfElementLocated(By.id("seat-form")));

        new Select(driver.findElement(By.id("seat-choice"))).selectByValue("12B");
        driver.findElement(By.cssSelector("#seat-form button[type='submit']")).click();

        assertTrue(wait.until(textToBePresentInElementLocated(By.id("message"),
                "Seat is already assigned on this flight.")));
        Booking unchanged = bookingRepository.findByBookingReferenceIgnoreCase(BOOKING_REFERENCE).orElseThrow();
        assertEquals("12A", unchanged.getSeatNumber());
        assertEquals(BookingStatus.BOOKED, unchanged.getStatus());
    }

    private void openPortal() {
        driver.get("http://localhost:" + port + "/");
        wait.until(visibilityOfElementLocated(By.id("lookup-form")));
    }

    private void lookupBooking(String reference, String surname) {
        openPortal();
        driver.findElement(By.id("reference")).sendKeys(reference);
        driver.findElement(By.id("surname")).sendKeys(surname);
        driver.findElement(By.cssSelector("#lookup-form button[type='submit']")).click();
    }

    private void resetFictionalBookings() {
        reservationRepository.deleteAllInBatch();
        bookingRepository.deleteAllInBatch();

        LocalDateTime departure = LocalDateTime.of(2035, 6, 15, 9, 30);
        List<Booking> fictionalBookings = List.of(
                new Booking(BOOKING_REFERENCE, "Fictional", SURNAME, "WEB101", "SFO", "SEA",
                        departure, "12A", BookingStatus.BOOKED),
                new Booking(OCCUPIED_BOOKING_REFERENCE, "Sample", "Passenger", "WEB101", "SFO", "SEA",
                        departure, "12B", BookingStatus.BOOKED));
        bookingRepository.saveAllAndFlush(fictionalBookings);
        reservationRepository.saveAllAndFlush(List.of(
                new FlightSeatReservation(BOOKING_REFERENCE, "WEB101", "12A"),
                new FlightSeatReservation(OCCUPIED_BOOKING_REFERENCE, "WEB101", "12B")));
    }

    private void captureFailureScreenshot(String testName) {
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            return;
        }
        try {
            Path directory = Path.of(SCREENSHOT_DIRECTORY);
            Files.createDirectories(directory);
            Path screenshotPath = directory.resolve(testName + "-" + System.currentTimeMillis() + ".png");
            Files.write(screenshotPath, screenshotDriver.getScreenshotAs(OutputType.BYTES));
            System.err.println("Selenium failure screenshot saved to " + screenshotPath.toAbsolutePath());
        } catch (IOException | RuntimeException exception) {
            System.err.println("Could not save Selenium failure screenshot: " + exception.getMessage());
        }
    }

    private static class FailureScreenshotExtension implements AfterTestExecutionCallback {
        @Override
        public void afterTestExecution(ExtensionContext context) {
            if (context.getExecutionException().isEmpty()) {
                return;
            }
            Object testInstance = context.getRequiredTestInstance();
            if (testInstance instanceof SeleniumBrowserTests browserTests) {
                browserTests.captureFailureScreenshot(context.getRequiredTestMethod().getName());
            }
        }
    }
}
