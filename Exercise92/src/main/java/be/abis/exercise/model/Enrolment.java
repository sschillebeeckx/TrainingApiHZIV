package be.abis.exercise.model;

public class Enrolment {

    private Session session;
    private int enrolmentInSession;
    private int enrolleeId;
    private double pricePerDayPayed;
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
