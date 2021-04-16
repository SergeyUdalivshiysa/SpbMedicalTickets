package main.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
@Scope("prototype")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LookUpTask implements Runnable {

    @Autowired
    private LookUpService lookUpService;

    @Autowired
    private UserNotificationService userNotificationService;

    String url;
    int id;
    String doctorOrCabinetName;
    Instant addTime;
    Instant lastAttempt;
    boolean doctorChecked;
    int attemptsNumber;
    int ticketNumber;
    long chatId;
    int ticketMinimumValueNeeded;
    Logger logger = LoggerFactory.getLogger(LookUpTask.class);

    @Value("${lookUpTaskSearchPeriod}")
    int lookUpTaskSearchPeriod;

    public void run() {
        logger.info("Начало работы id - " + id);
        sleep();
        WebDriver driver = getDriver();
        getAndHandleWebElement(driver, url, doctorOrCabinetName);
    }


    //Ожидание перед новым подключением к серверу горздрав, для предотвращения блокировки.
    private void sleep() {
        try {
            SleepService.sleep();
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (FutureStorage.get(id).isCancelled()) {
                Thread.currentThread().interrupt();
            }
        }
    }

    //Используется веб драйвер, тк сайт горздрава динамический и более простым парсером не удается собрать данные
    private WebDriver getDriver() {
        ChromeOptions options = new ChromeOptions();
    /*      options.addArguments(
                "--headless",
                "--disable-gpu",
                "--ignore-certificate-errors",
                "--disable-extensions",
                "--no-sandbox",
                "--disable-dev-shm-usage"
        );*/
        return new ChromeDriver(options);
    }

    private void getAndHandleWebElement(WebDriver driver, String url, String doctorOrCabinetName) {
        try {
            WebElement webElement = getWebElementAndFindDoctor(driver, url, doctorOrCabinetName);
            if (webElement != null) {
                handleDoctorFinding(driver, webElement);
            } else handleDoctorAbsence(driver);
        } catch (TimeoutException exception) {
            logger.info("Id + " + id + ". Timeout Exception.");
            handleTimeoutException(driver);
        }
    }

    private WebElement getWebElementAndFindDoctor(WebDriver driver, String url, String doctorOrCabinetName) throws TimeoutException {
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        WebElement visibleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul[class='service-doctor-top__col service-doctor-top__list']")));
        List<WebElement> elements = driver.findElements(By.cssSelector("div[data-doctor-name='" + doctorOrCabinetName + "']"));
        return elements.isEmpty() ? null : elements.get(0);


    }

    private void handleTimeoutException(WebDriver driver) {
        driver.quit();
        if (!doctorChecked && attemptsNumber >= 3) {
            logger.info("Id - " + id + ". Троекратная ошибка подключения к серверу.");
            sendMessage("Троекратная ошибка подключения к серверу, проверьте имя доктора/кабинета/процедуры и корректность ссылки");
            lookUpService.eliminateTask(id);
        } else {
            attemptsNumber++;
            lastAttempt = Instant.now();
            lookUpService.updateTaskEntity(this);
        }
    }

    private void handleDoctorFinding(WebDriver driver, WebElement webElement) {
        doctorChecked = true;
        ticketNumber = getNumberOfTickets(webElement, driver);
        getFinishedTaskInformation(ticketNumber);
    }

    private void handleDoctorAbsence(WebDriver driver) {
        List<WebElement> elements = driver.findElements(By.cssSelector("div[data-doctor-name]"));
        WebElement webElement = null;
        for (WebElement element : elements) {
            String doctorName = element.findElement(
                    By.className("service-block-1__title"))
                    .getText();
            if (doctorName.trim().equals(doctorOrCabinetName)) {
                webElement = element;
                break;
            }
        }
        if (webElement == null) {
            logger.info("Id " + id + ". Доктор не найден.");
            sendMessage("Доктор/кабинет/процедура не найден, проверьте корректность.");
            lookUpService.eliminateTask(id);
            driver.close();
        }
        else handleDoctorFinding(driver, webElement);
    }

    private int getNumberOfTickets(WebElement webElement, WebDriver driver) {
        String ticketsInformation = webElement.findElement(
                By.cssSelector("ul[class='service-doctor-top__col service-doctor-top__list']")).getText();
        driver.close();
        driver.quit();
        if (ticketsInformation.trim().equals("")) {
            return 0;
        } else {
            ticketsInformation = ticketsInformation.replaceAll("[\\D]", "");
            return (Integer.parseInt(ticketsInformation));
        }
    }


    private void getFinishedTaskInformation(int ticketNumber) {
        if (ticketNumber >= ticketMinimumValueNeeded) {
            logger.info("Id " + id + ". Найдено " + ticketNumber + " номерков.");
            lookUpService.eliminateTask(id);
            sendMessage(String.format("По ссылке %s найдено %s номерков!", url, ticketNumber));
        } else {
            logger.info("Id " + id + ". Номерки не найдены");
            lastAttempt = Instant.now();
            attemptsNumber++;
            lookUpService.updateTaskEntity(this);
            checkAndHandleTimeout();
        }
    }

    private void checkAndHandleTimeout() {
        if (Instant.now().compareTo(addTime.plus(5, ChronoUnit.MINUTES)) > 0) {
            logger.info("Id " + id + ". Попытки кончились");
            sendMessage("За прошедшую неделю номерки не были найдены. Создайте новый поиск для повтора");
            lookUpService.eliminateTask(id);
        }
    }

    private void sendMessage(String message) {
        userNotificationService.sendMessageToUser(message, chatId);
    }
}

