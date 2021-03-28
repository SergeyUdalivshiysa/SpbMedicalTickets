package Main.entities;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

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


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDoctorOrCabinetName() {
        return doctorOrCabinetName;
    }

    public void setDoctorOrCabinetName(String doctorOrCabinetName) {
        this.doctorOrCabinetName = doctorOrCabinetName;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public Instant getLastAttempt() {
        return lastAttempt;
    }

    public void setLastAttempt(Instant lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public boolean isDoctorChecked() {
        return doctorChecked;
    }

    public void setDoctorChecked(boolean doctorChecked) {
        this.doctorChecked = doctorChecked;
    }

    public int getAttemptsNumber() {
        return attemptsNumber;
    }

    public void setAttemptsNumber(int attemptsNumber) {
        this.attemptsNumber = attemptsNumber;
    }

    public TaskEntity(String url, String doctorOrCabinetName) {
        this.url = url;
        this.doctorOrCabinetName = doctorOrCabinetName;
        this.attemptsNumber = 0;
        this.addTime = System.currentTimeMillis();
    }

    public TaskEntity(int id, String url, String doctorOrCabinetName, long addTime, Instant lastAttempt, boolean doctorChecked, int attemptsNumber) {
        this.id = id;
        this.url = url;
        this.doctorOrCabinetName = doctorOrCabinetName;
        this.addTime = addTime;
        this.lastAttempt = lastAttempt;
        this.doctorChecked = doctorChecked;
        this.attemptsNumber = attemptsNumber;
    }

    public TaskEntity() {
    }

    public int getId() {
        return id;
    }
}
