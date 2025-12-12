package be.abis.exercise.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("c")
public class CompanySession extends Session {

    public CompanySession() {
    }

    public CompanySession(LocalDate startDate, int instructorId, int courseId) {
        super(startDate, instructorId, courseId);
    }

}
