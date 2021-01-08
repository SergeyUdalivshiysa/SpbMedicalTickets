
import java.io.IOException;

import java.util.HashMap;
import java.util.concurrent.*;


public class Main {

    public static HashMap<Integer, ScheduledFuture> hashMap= new HashMap<>();



    public static void main(String[] args) throws IOException {

        String url = "https://gorzdrav.spb.ru/service-free-schedule#%5B%7B%22district%22:%2212%22%7D,%7B%22lpu%22:%22244%22%7D,%7B%22speciality%22:%2279%22%7D%5D";
        String url1 = "https://gorzdrav.spb.ru/service-free-schedule#%5B%7B%22district%22:%2212%22%7D,%7B%22lpu%22:%22244%22%7D,%7B%22speciality%22:%2228%22%7D%5D";
        String url2 = "https://gorzdrav.spb.ru/service-free-schedule#%5B%7B%22district%22:%2212%22%7D,%7B%22lpu%22:%22244%22%7D,%7B%22speciality%22:%2218%22%7D%5D";

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Asus G750JH\\IdeaProjects\\SpbMedicalTickets\\SpbMedicalTicketsProject\\lib\\chromedriver.exe");

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        hashMap.put(1, executorService.scheduleWithFixedDelay(new TicketCheck(url, 1), 0, 1, TimeUnit.MINUTES));
        hashMap.put(2, executorService.scheduleWithFixedDelay(new TicketCheck(url1, 2), 0, 1, TimeUnit.MINUTES));
        hashMap.put(2, executorService.scheduleWithFixedDelay(new TicketCheck(url2, 3), 0, 1, TimeUnit.MINUTES));


    }
}
