package be.abis.exercise.model;

import be.abis.exercise.util.CancelConverter;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "sessions")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="skind", discriminatorType=DiscriminatorType.STRING)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sno")
    private int sessionId;
    @Column(name="sdate")
    private LocalDate startDate;
    @Column(name="sins_pno")
    private int instructorId;

    //@Column(name="skind")
    //private String kind;

    @Column(name="sincomes")
    private double income;
    @Column(name="scancel")
    @Convert(converter= CancelConverter.class)
    private boolean cancelled;
    @Column(name="s_cid")
    private int courseId;

    public Session(){}

    public Session(LocalDate startDate, int instructorId, int courseId) {
        this.startDate = startDate;
        this.instructorId = instructorId;
        this.courseId = courseId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    /*public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }*/
    public String getKind(){
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
