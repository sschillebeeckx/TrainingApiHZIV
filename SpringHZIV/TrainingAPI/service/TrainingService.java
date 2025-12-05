package be.abis.exercise.service;

import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.model.Enrolment;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrainingService {

    public String getWelcomeMessage();
    public PersonService getPersonService();
    public CourseService getCourseService();

    public Mono<Void> enrolForSession(Person person, int sessionId) throws EnrolException;

    Flux<Session> findSessionsForCourse(String courseTitle);

    public Flux<Enrolment> findEnrolments(int personId);


}
