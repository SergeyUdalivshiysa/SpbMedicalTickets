package Main.controllers;

import Main.Main;
import Main.services.LookUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class LookUpController {

    @Autowired
    private LookUpService lookUpService;

    @PostMapping("/ticketcheck")
    public void addTask(String url, String name)  {
        lookUpService.handleTaskAddition(url, name);
    }

    @DeleteMapping("/ticketcheck/{id}")
    public void deleteTask(@PathVariable int id) {
        lookUpService.eliminateTask(id);
    }


}