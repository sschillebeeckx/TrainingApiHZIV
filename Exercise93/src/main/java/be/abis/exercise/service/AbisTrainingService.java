package be.abis.exercise.service;

import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.model.Course;
import be.abis.exercise.model.Enrolment;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Session;
import be.abis.exercise.repository.SessionJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
    public Flux<Session> findSessionsForCourse(String courseTitle) {
        return this.getCourseService().findCourseByShortTitle(courseTitle)
                .map(course->course.getCourseId())
                .flatMapMany(courseId ->
                        Mono.fromCallable(() -> sessionRepo.findByCourseId(courseId))
                                .flatMapMany(Flux::fromIterable));
    }


    @Override
    public Mono<Void> enrolForSession(Person person, int sessionId) throws EnrolException {
       return null;
    }


    @Override
    public Flux<Enrolment> findEnrolments(int personId) {
        return null;
    }


}
