package Main.entities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TaskEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String url;
    private String doctorOrCabinetName;
    private long addTime;
    private Instant lastAttempt;
    private boolean doctorChecked;
    private int attemptsNumber;

    public TaskEntity(String url, String doctorOrCabinetName) {
        this.url = url;
        this.doctorOrCabinetName = doctorOrCabinetName;
        this.attemptsNumber = 0;
    }
}
