import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;


public class TicketCheck extends Thread {
    private String url;
    private int id;
    private int attempts = 0;

    public TicketCheck(String url, int id) {
        this.url = url;
        this.id = id;
    }


    public void run() {
        if (++attempts > 1) {
            Main.hashMap.get(id).cancel(true);
            Main.hashMap.remove(id);
            System.out.println("Поптыки кончились. id - " + id);
        }
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (Main.hashMap.get(id).isCancelled()) {
                this.interrupt();
            }
        }

        int ticketNumber = 0;
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, 30);
        try {
            WebElement visibleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul[class='service-doctor-top__col service-doctor-top__list']")));
        } catch (TimeoutException exception) {
            driver.quit();
        }
        List<WebElement> elements = driver.findElements(By.cssSelector("div[class='service-block-1 service-doctor js-doctor']"));
        for (WebElement webElement : elements) {
            String ticketsInformation = webElement.findElement(
                    By.cssSelector("ul[class='service-doctor-top__col service-doctor-top__list']")).getText();
            if (ticketsInformation.trim().equals("")) {
                ticketNumber = 0;
            } else {
                ticketsInformation = ticketsInformation.replaceAll("[\\D]", "");
                ticketNumber = (Integer.parseInt(ticketsInformation));
            }
        }
        driver.quit();

        if (ticketNumber > 0) {
            System.out.printf("Количество номерков : %s", ticketNumber);
            Main.hashMap.get(id).cancel(false);
            Main.hashMap.remove(id);
        } else System.out.println("Номерков нет");
    }


}
