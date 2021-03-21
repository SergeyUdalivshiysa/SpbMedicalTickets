package Main.entities;

import Main.Main;
import org.springframework.boot.autoconfigure.web.WebProperties;

import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class TaskEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String url;
    private long timeOfAddition;
    private String doctorOrCabinetName;
    private Date lastAttempt;
    private boolean onceSuccess;
    private int attemptsNumber;

    public TaskEntity(String url, String doctorOrCabinetName) {
        this.url = url;
        this.doctorOrCabinetName = doctorOrCabinetName;
        this.attemptsNumber = 0;
        this.timeOfAddition = System.currentTimeMillis();
    }

    public TaskEntity() {
    }

    public int getId() {
        return id;
    }
}
