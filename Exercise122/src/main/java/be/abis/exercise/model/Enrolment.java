package be.abis.exercise.model;

import be.abis.exercise.util.CancelConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "enrolments")
@IdClass(EnrolmentId.class)
@NamedNativeQuery(name="Enrolment.findByEnrolleeNQ",
        query = "select e_pno, sdate, s_cid " +
                "from enrolments inner join sessions on e_sno=sno " +
                "where e_pno = :pno and scancel is null and ecancel is null")
public class Enrolment {
    @Id
    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "e_sno")
    private Session session;
    @Id
    @Column(name="eno")
    private int enrolmentInSession;
    @Column(name="e_pno")
    private int enrolleeId;
    @Column(name="epay")
    private double pricePerDayPayed;
    @Column(name="ecancel")
    @Convert(converter= CancelConverter.class)
    private boolean cancelled;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public int getEnrolmentInSession() {
        return enrolmentInSession;
    }

    public void setEnrolmentInSession(int enrolmentInSession) {
        this.enrolmentInSession = enrolmentInSession;
    }

    public int getEnrolleeId() {
        return enrolleeId;
    }

    public void setEnrolleeId(int enrolleeId) {
        this.enrolleeId = enrolleeId;
    }

    public double getPricePerDayPayed() {
        return pricePerDayPayed;
    }

    public void setPricePerDayPayed(double pricePerDayPayed) {
        this.pricePerDayPayed = pricePerDayPayed;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
