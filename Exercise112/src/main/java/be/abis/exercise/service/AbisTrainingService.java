package be.abis.exercise.service;

import be.abis.exercise.dto.Course;
import be.abis.exercise.dto.EnrolmentDTO;
import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.exception.SessionCanNotBeDeletedException;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.mapper.EnrolmentMapper;
import be.abis.exercise.mapper.SessionMapper;
import be.abis.exercise.repository.EnrolmentJpaRepository;
import be.abis.exercise.repository.SessionJpaRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AbisTrainingService implements TrainingService {
    private SessionJpaRepository sessionRepo;
    private EnrolmentJpaRepository enrolRepo;

    public AbisTrainingService(SessionJpaRepository sessionRepo,EnrolmentJpaRepository enrolRepo) {
        this.sessionRepo = sessionRepo;
        this.enrolRepo = enrolRepo;
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
    public Flux<EnrolmentDTO> findEnrolments(int personId) {
        return   Mono.fromCallable(() -> enrolRepo.findByEnrolleeNQ(personId))
                .subscribeOn(Schedulers.boundedElastic())   // Run blocking DB call on elastic thread pool
                .flatMapMany(Flux::fromIterable)
                .map(EnrolmentMapper::toNamedQueryResultDTO)
                .flatMap(nqDTO  -> {
                    Mono<PersonDTO> personMono = this.getPersonService().findPerson(nqDTO.personId());
                    Mono<Course> courseMono = this.getCourseService().findCourseById(nqDTO.courseId());

                    // Step 3: Combine both API results + local row fields
                    return Mono.zip(personMono, courseMono)
                            .map(tuple -> EnrolmentMapper.toDTO(
                                    nqDTO, // from DB row
                                    tuple.getT1(),           // Person
                                    tuple.getT2()          // Course

                            ));
                });
    }

    @Override
    @Transactional
    public Mono<Void> deleteSession(int id) {
        return Mono.fromRunnable(() -> {
                    try {
                        sessionRepo.deleteById(id);
                    } catch (DataIntegrityViolationException e) {
                        // handle the exception as you like
                        throw new SessionCanNotBeDeletedException(e.getMessage());
                    }
                })
                .then();
    }


}
