package Main.services;

import Main.entities.TaskEntity;
import Main.repository.TicketEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(LookUpService.class);


    //TODO имплементировать влидацию
    public boolean isUrlCorrect(String url) {
        return url.matches("^(https://gorzdrav.spb.ru/service-free-schedule#).+");
    }

    public void handleTaskAddition(String url, String name) {
        if (isUrlCorrect(url)) {
            TaskEntity taskEntity = new TaskEntity(url, name);
            taskEntity.setAddTime(System.currentTimeMillis());
            TaskEntity newTaskEntity = ticketEntityRepository.save(taskEntity);
            int id = newTaskEntity.getId();
            ScheduledFuture scheduledFuture = addTaskToExecutor(taskEntity);
            FutureStorage.putToStorage(id, scheduledFuture);
        } else {
            logger.info("Неверный адрес");
            //Оповестить пользователя
        }
    }

    public synchronized void eliminateTask(int id) {
        ticketEntityRepository.deleteById(id);
        ScheduledFuture scheduledFuture = FutureStorage.deleteFromStorage(id);
        scheduledFuture.cancel(false);
        logger.info("Id " + id + " is eliminated.");
    }


    public ScheduledFuture addTaskToExecutor(TaskEntity taskEntity) {
        LookUpTask lookUpTask = applicationContext.getBean(LookUpTask.class);
        setLookUpTaskPropertiesFromTaskEntity(taskEntity, lookUpTask);
        ScheduledFuture scheduledFuture = taskScheduler.scheduleWithFixedDelay(lookUpTask, Duration.ofMinutes(1));
        logger.info("Id " + taskEntity.getId() + "is added to executor");
        return scheduledFuture;
    }


    public void updateTaskEntity(LookUpTask lookUpTask) {
        TaskEntity taskEntity = ticketEntityRepository.findById(lookUpTask.getId()).get();
        setTaskEntityPropertiesFromLookUpTask(lookUpTask, taskEntity);
        ticketEntityRepository.save(taskEntity);
    }

    private void setLookUpTaskPropertiesFromTaskEntity(TaskEntity taskEntity, LookUpTask lookUpTask) {
        lookUpTask.setDoctorOrCabinetName(taskEntity.getDoctorOrCabinetName());
        lookUpTask.setId(taskEntity.getId());
        lookUpTask.setUrl(taskEntity.getUrl());
        lookUpTask.setAddTime(taskEntity.getAddTime());
    }

    private void setTaskEntityPropertiesFromLookUpTask(LookUpTask lookUpTask, TaskEntity taskEntity) {
        taskEntity.setAttemptsNumber(lookUpTask.getAttemptsNumber());
        taskEntity.setDoctorChecked(lookUpTask.isDoctorChecked());
        taskEntity.setLastAttempt(lookUpTask.getLastAttempt());
    }

}
