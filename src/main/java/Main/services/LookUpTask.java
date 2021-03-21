package Main.services;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.sql.Date;
import java.util.List;



@Component
@Scope("prototype")
public class LookUpTask implements Runnable {

    @Autowired
    private LookUpService lookUpService;


    private String url;
    private int id;
    private long addTime;
    private String doctorOrCabinetName;
    private Date lastAttempt;
    private boolean doctorChecked;
    private int attemptsNumber;
    public boolean isFinished = false;



    public LookUpTask() {
    }

    public LookUpTask(String url, String doctorOrCabinetName, int id) {
        this.url = url;
        this.id = id;
        this.addTime = System.currentTimeMillis();
        this.doctorOrCabinetName = doctorOrCabinetName.trim();
        this.attemptsNumber = 0;
        this.doctorChecked = false;
        this.lastAttempt = null;
    }

    public LookUpTask(String url, String doctorOrCabinetName, int id, long addTime, int failedAttempts) {
        this.url = url;
        this.id = id;
        this.addTime = addTime;
        this.doctorOrCabinetName = doctorOrCabinetName.trim();
        this.attemptsNumber = failedAttempts;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDoctorOrCabinetName(String doctorOrCabinetName) {
        this.doctorOrCabinetName = doctorOrCabinetName;
    }


    public void run() {
        System.out.println("Начало работы id - " + id);
        int ticketNumber;
        sleep();
        WebDriver driver = getDriver();
        WebElement webElement = getWebElement(driver, url, doctorOrCabinetName);
        if (webElement == null) {
            //TODO реализовать отмену задачи
            System.out.println("доктора нема");
        }
        ticketNumber = getNumberOfTickets(webElement, driver);
        getFinishedTaskInformation(ticketNumber);
    }



    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (FutureStorage.get(id).isCancelled()) {
                Thread.currentThread().interrupt();
            }

        }

    }

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

    private WebElement getWebElement(WebDriver driver, String url, String doctorOrCabinetName) {
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, 180);
        try {
            WebElement visibleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul[class='service-doctor-top__col service-doctor-top__list']")));
        } catch (TimeoutException exception) {
            exception.printStackTrace();
            driver.quit();

            if (!doctorChecked && attemptsNumber > 3) {
                //TODO реализовать данный сценарий
            }
        }
        //Вариант 2
        List<WebElement> elements = driver.findElements(By.cssSelector("div[data-doctor-name='" + doctorOrCabinetName + "']"));
        return elements.isEmpty() ? null : elements.get(0);
    }


    private int getNumberOfTickets(WebElement webElement, WebDriver driver) {
        String ticketsInformation = webElement.findElement(
                By.cssSelector("ul[class='service-doctor-top__col service-doctor-top__list']")).getText();
        driver.close();
        driver.quit();
        if (ticketsInformation.trim().equals("")) {
            return  0;
        } else {
            ticketsInformation = ticketsInformation.replaceAll("[\\D]", "");
           return  (Integer.parseInt(ticketsInformation));
        }
        //Вариант 1
   /*     List<WebElement> elements = driver.findElements(By.cssSelector("div[class='service-block-1 service-doctor js-doctor']"));
        for (WebElement webElement : elements) {
            String ticketsInformation = webElement.findElement(
                    By.cssSelector("ul[class='service-doctor-top__col service-doctor-top__list']")).getText();
            if (ticketsInformation.trim().equals("")) {
                ticketNumber = 0;
            } else {
                ticketsInformation = ticketsInformation.replaceAll("[\\D]", "");
                ticketNumber = (Integer.parseInt(ticketsInformation));
            }
        }*/
    }


    private void getFinishedTaskInformation(int ticketNumber) {
        if (ticketNumber > 0) {
            System.out.printf("Количество номерков : %s\n", ticketNumber);
            System.out.println("Завершение задачи - id - " + id + " результат - номерки найдены");
            lookUpService.eliminateTask(id);
        } else {
            System.out.println("Номерков нет");
            System.out.println("Завершение задачи - id - " + id + " результат - номерков нет");
            lastAttempt.setTime(System.currentTimeMillis());
            System.out.println(lastAttempt);

        }

        //TODO реализовать остановку поиска по истечению некоторого времени
     /*   if (System.currentTimeMillis() > addTime + 5000) {
            System.out.println("Поптыки кончились. id - " + id);
            System.out.println("id" + id + "попытки кончились");
        }*/
    }




}

