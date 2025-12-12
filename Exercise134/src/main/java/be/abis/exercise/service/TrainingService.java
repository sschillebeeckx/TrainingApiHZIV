package be.abis.exercise.service;

import be.abis.exercise.dto.EnrolmentDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.model.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface TrainingService {

     String getWelcomeMessage();
     PersonService getPersonService();
     CourseService getCourseService();

    // Mono<Void> enrolForSession(PersonForm person, int sessionId) throws EnrolException;
    void enrolForSession(PersonForm personIn, int sessionId) throws EnrolException;
    Flux<SessionDTO> findSessionsForCourse(String courseTitle);

     Flux<EnrolmentDTO> findEnrolments(int personId);

    Mono<Void> deleteSession(int id);

    Mono<Void> cancelSession(int id);

    Session addNewCompanySession(LocalDate startDate, int personId, int courseId);
}
