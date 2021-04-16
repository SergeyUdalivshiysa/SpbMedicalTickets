package main.entities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@FieldDefaults(level= AccessLevel.PRIVATE)
public class TaskEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    String url;
    String doctorOrCabinetName;
    Instant addTime;
    Instant lastAttempt;
    boolean doctorChecked;
    int attemptsNumber;
    long userId;
    long chatId;
    int ticketMinimumValueNeeded;

    public TaskEntity(String url, String doctorOrCabinetName, long userId, long chatId, int ticketMinimumValueNeeded) {
        this.url = url;
        this.doctorOrCabinetName = doctorOrCabinetName;
        this.attemptsNumber = 0;
        this.chatId = chatId;
        this.userId=userId;
        this.ticketMinimumValueNeeded = ticketMinimumValueNeeded;
    }
}
