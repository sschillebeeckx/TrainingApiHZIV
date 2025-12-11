package be.abis.exercise.service;

import be.abis.exercise.dto.Course;
import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.mapper.SessionMapper;
import be.abis.exercise.model.Enrolment;
import be.abis.exercise.repository.SessionJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AbisTrainingService implements TrainingService {
    private SessionJpaRepository sessionRepo;

    public AbisTrainingService(SessionJpaRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    @Value("Welcome to the Abis Training Service")
    private String welcomeMessage;

    @Autowired
    private PersonService personService;

    @Autowired
    private CourseService courseService;

    @Override
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public CourseService getCourseService() {
        return courseService;
    }

    @Override
    public Flux<SessionDTO> findSessionsForCourse(String courseTitle) {
        return this.getCourseService().findCourseByShortTitle(courseTitle)
                .map(course -> new Course(course.getCourseId(), course.getLongTitle()))
                .flatMapMany(course ->
                        Mono.fromCallable(() -> sessionRepo.findByCourseId(course.getCourseId()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMapMany(sessionList -> Flux.fromIterable(sessionList))
                                .flatMap(session ->
                                        this.getPersonService().findPerson(session.getInstructorId())
                                                .map(person -> new PersonDTO(person.getFirstName(), person.getLastName()))
                                                .map(person -> SessionMapper.toDTO(course, session, person))
                                )
                );
    }



    @Override
    public Mono<Void> enrolForSession(PersonForm person, int sessionId) throws EnrolException {
       return null;
    }


    @Override
    public Flux<Enrolment> findEnrolments(int personId) {
        return null;
    }


}
