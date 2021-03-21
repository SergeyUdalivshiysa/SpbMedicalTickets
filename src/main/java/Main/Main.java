package Main;

import Main.services.LookUpService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    static Logger loggerSearch;
    static Logger loggerExceptions;

    @Autowired
    static private LookUpService taskAdditionHandler;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.setProperty("webdriver.chrome.driver", "lib\\operadriver.exe");

        //TODO Добавить логгирование, добавить тесты




    }
}
