package main.services;

import main.entities.TaskEntity;
import main.repository.TicketEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Service
public class InitializeService {

    @Autowired
    LookUpService lookUpService;

    @Autowired
    TicketEntityRepository ticketEntityRepository;

    //После остановки приложения, при повторном запуске все задачи по поиску будут добавлены в executor в порядке очереди
    @PostConstruct
    private void initialize() {
        ArrayList<TaskEntity> entities = (ArrayList) ticketEntityRepository.findAllAndSortByLastAttempt();
        entities.forEach(taskEntity ->
                lookUpService.addTaskToExecutorFromDataBase(taskEntity));
    }

}
