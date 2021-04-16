package main;

import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Main {
    static Logger loggerSearch;
    static Logger loggerExceptions;

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "lib\\operadriver.exe");
        SpringApplication.run(Main.class, args);


        //TODO Добавить логгирование, тесты




    }
}
