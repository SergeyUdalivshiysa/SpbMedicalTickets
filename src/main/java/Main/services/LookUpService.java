package Main.services;

import Main.dto.LookUpTaskDTO;
import Main.entities.TaskEntity;
import Main.repository.TicketEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    @Value("${lookUpRateMinutes}")
    private int lookUpRateMinutes;

    public void crateScheduledLookUpTask(LookUpTaskDTO lookUpTaskDTO) {
        String url = lookUpTaskDTO.getUrl();
        String name = lookUpTaskDTO.getDoctorOrCabinetName();
        long userId = lookUpTaskDTO.getUserId();
        long chatId = lookUpTaskDTO.getChatId();
        int minimumTicketNumberNeeded = lookUpTaskDTO.getTicketMinimumValueNeeded();
        TaskEntity taskEntity = new TaskEntity(url, name, userId, chatId, minimumTicketNumberNeeded);
        taskEntity.setAddTime(Instant.now());
        TaskEntity newTaskEntity = ticketEntityRepository.save(taskEntity);
        int entityId = newTaskEntity.getId();
        ScheduledFuture scheduledFuture = addTaskToExecutor(taskEntity);
        FutureStorage.putToStorage(entityId, scheduledFuture);
    }

    public synchronized void eliminateTask(int id) {
        ticketEntityRepository.deleteById(id);
        ScheduledFuture scheduledFuture = FutureStorage.deleteFromStorage(id);
        scheduledFuture.cancel(false);
        logger.info("Id " + id + " is eliminated.");
    }


    private ScheduledFuture addTaskToExecutor(TaskEntity taskEntity) {
        LookUpTask lookUpTask = applicationContext.getBean(LookUpTask.class);
        setLookUpTaskPropertiesFromTaskEntity(taskEntity, lookUpTask);
        ScheduledFuture scheduledFuture = taskScheduler.scheduleWithFixedDelay(lookUpTask, Duration.ofMinutes(lookUpRateMinutes));
        logger.info("Id " + taskEntity.getId() + "is added to executor");
        return scheduledFuture;
    }

    public void addTaskToExecutorFromDataBase(TaskEntity taskEntity) {
        LookUpTask lookUpTask = applicationContext.getBean(LookUpTask.class);
        setLookUpTaskPropertiesFromTaskEntity(taskEntity, lookUpTask);
        Instant lastAttempt = taskEntity.getLastAttempt();
        Instant startTime = lastAttempt.plus(lookUpRateMinutes, ChronoUnit.MINUTES);
        ScheduledFuture scheduledFuture = taskScheduler.scheduleWithFixedDelay(lookUpTask, startTime, Duration.ofMinutes(lookUpRateMinutes));
        logger.info("Id " + taskEntity.getId() + "is added to executor");
        FutureStorage.putToStorage(taskEntity.getId(), scheduledFuture);
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
        lookUpTask.setChatId(taskEntity.getChatId());
        lookUpTask.setTicketMinimumValueNeeded(taskEntity.getTicketMinimumValueNeeded());
    }

    private void setTaskEntityPropertiesFromLookUpTask(LookUpTask lookUpTask, TaskEntity taskEntity) {
        taskEntity.setAttemptsNumber(lookUpTask.getAttemptsNumber());
        taskEntity.setDoctorChecked(lookUpTask.isDoctorChecked());
        taskEntity.setLastAttempt(lookUpTask.getLastAttempt());
        taskEntity.setChatId(lookUpTask.getChatId());
        taskEntity.setTicketMinimumValueNeeded(lookUpTask.getTicketMinimumValueNeeded());
    }

}
