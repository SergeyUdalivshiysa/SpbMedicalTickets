package Main.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
@Scope("prototype")
public class LookUpTask implements Runnable {

    @Autowired
    private LookUpService lookUpService;

    private String url;
    private int id;
    private String doctorOrCabinetName;
    private long addTime;
    private Instant lastAttempt;
    private boolean doctorChecked;
    private int attemptsNumber;
    int ticketNumber;
    Logger logger = LoggerFactory.getLogger(LookUpTask.class);

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
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }

    private void getAndHandleWebElement(WebDriver driver, String url, String doctorOrCabinetName) {
        try {
            WebElement webElement = getWebElement(driver, url, doctorOrCabinetName);
            if (webElement != null) {
                doctorChecked = true;
                ticketNumber = getNumberOfTickets(webElement, driver);
                getFinishedTaskInformation(ticketNumber);
            } else handleDoctorAbsence();
        }
        catch (TimeoutException exception) {
            logger.info("Id + " + id + ". Timeout Exception.");
            handleTimeoutException(driver);
        }
    }

    private WebElement getWebElement(WebDriver driver, String url, String doctorOrCabinetName) throws TimeoutException {
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
            //Уведомить пользователя
            lookUpService.eliminateTask(id);
        }
        else {
            attemptsNumber++;
            lastAttempt = Instant.ofEpochMilli(System.currentTimeMillis());
            lookUpService.updateTaskEntity(this);
        }
    }

    private void handleDoctorAbsence() {
        logger.info("Id " + id + ". Доктор не найден.");
        //Уведомить пользователя
        lookUpService.eliminateTask(id);
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
        if (ticketNumber > 0) {
            logger.info("Id " + id + ". Найдено " + ticketNumber + " номерков.");
            lookUpService.eliminateTask(id);
            //Уведомить пользователя
        } else {
            logger.info("Id " + id + ". Номерки не найдены");
            lastAttempt = Instant.ofEpochMilli(System.currentTimeMillis());
            attemptsNumber++;
            lookUpService.updateTaskEntity(this);
            checkAndHandleTimeout();
        }
    }

    private void checkAndHandleTimeout() {
        if (System.currentTimeMillis() > addTime + 500000) {
            logger.info("Id " + id + ". Попытки кончились");
            //Уведомить пользователя
            lookUpService.eliminateTask(id);
        }
    }
}

