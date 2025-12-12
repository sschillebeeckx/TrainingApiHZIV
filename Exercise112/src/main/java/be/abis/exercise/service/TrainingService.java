package be.abis.exercise.service;

import be.abis.exercise.dto.EnrolmentDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.form.PersonForm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrainingService {

    public String getWelcomeMessage();
    public PersonService getPersonService();
    public CourseService getCourseService();

    public Mono<Void> enrolForSession(PersonForm person, int sessionId) throws EnrolException;

    Flux<SessionDTO> findSessionsForCourse(String courseTitle);

    public Flux<EnrolmentDTO> findEnrolments(int personId);

    Mono<Void> deleteSession(int id);

}
