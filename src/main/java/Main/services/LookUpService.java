package Main.services;
import Main.entities.TaskEntity;
import Main.repository.TicketEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Service
public class LookUpService {


    public LookUpService() {
    }
    @Autowired
    private TicketEntityRepository ticketEntityRepository;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private ApplicationContext applicationContext;


    //TODO имплементировать влидацию
    public boolean isUrlCorrect(String url) {
        return url.matches("^(https://gorzdrav.spb.ru/service-free-schedule#).+");
    }

    public void handleTaskAddition (String url, String name) {
        if (isUrlCorrect(url)) {
            TaskEntity taskEntity = new TaskEntity(url, name);
            TaskEntity newTaskEntity = ticketEntityRepository.save(taskEntity);
            int id = newTaskEntity.getId();
            ScheduledFuture scheduledFuture = addTaskToExecutor(url, name, id);
            FutureStorage.putToStorage(id, scheduledFuture);
        }

        //TODO добавить логирование
        else {
            System.out.println("Неверный адресс");

        }
    }

    public synchronized void eliminateTask(int id) {
        ticketEntityRepository.deleteById(id);
        ScheduledFuture scheduledFuture = FutureStorage.deleteFromStorage(id);
        scheduledFuture.cancel(false);
    }

    public ScheduledFuture addTaskToExecutor(String url, String name, int id)  {
        LookUpTask ticketCheck = applicationContext.getBean(LookUpTask.class);
        ticketCheck.setDoctorOrCabinetName(name);
        ticketCheck.setId(id);
        ticketCheck.setUrl(url);
        ScheduledFuture scheduledFuture = taskScheduler.scheduleWithFixedDelay(ticketCheck, Duration.ofMinutes(1));
        return scheduledFuture;
    }

}
