package be.abis.exercise.service;

import be.abis.exercise.dto.Course;
import be.abis.exercise.dto.EnrolmentDTO;
import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.SessionCanNotBeDeletedException;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.mapper.EnrolmentMapper;
import be.abis.exercise.mapper.SessionMapper;
import be.abis.exercise.model.CompanySession;
import be.abis.exercise.model.Enrolment;
import be.abis.exercise.model.Session;
import be.abis.exercise.repository.EnrolmentJpaRepository;
import be.abis.exercise.repository.SessionJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;

@Service
public class AbisTrainingService implements TrainingService {
    private SessionJpaRepository sessionRepo;
    private EnrolmentJpaRepository enrolRepo;

    @Autowired
    private  PlatformTransactionManager txManager;

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

    /*@Transactional(rollbackFor = EnrolException.class)
    public Mono<Void> enrolForSession(PersonForm personIn, int sessionId) {

        return personService.addPerson(personIn)
                .onErrorResume(PersonAlreadyExistsException.class,
                        e -> personService.findPersonByEmail(personIn.getEmailAddress()))

                // Load session and combine with person
                .zipWith(findSession(sessionId)
                        .switchIfEmpty(Mono.error(new EnrolException("Can not enrol, session does not exist"))))

                .flatMap(tuple -> {
                    PersonDTO person = tuple.getT1();
                    Session session = tuple.getT2();

                    // Step 1: check session capacity
                    return countEnrolmentsForSession(session.getSessionId())
                            .flatMap(count -> {
                                if (count >= 12) {
                                    return Mono.error(new EnrolException("Session is full"));
                                }
                                return Mono.just(person);
                            })

                            // Step 2: check if already enrolled
                            .flatMap(p ->
                                    findEnrolment(p.getPersonId(), session.getSessionId())
                                            .flatMap(existing -> {
                                                if (existing != null) {
                                                    return Mono.error(new EnrolException("You are already enrolled for this session"));
                                                }
                                                return Mono.just(p);
                                            })
                            )

                            // Step 3: load last enrolment number and course concurrently
                            .zipWith(getLastEnoForSession(session.getSessionId()))
                            .zipWith(courseService.findCourseById(session.getCourseId()))

                            // Step 4: save enrolment (blocking, wrapped safely)
                            .flatMap(data -> {
                                PersonDTO p = data.getT1().getT1();
                                Integer lastEno = data.getT1().getT2();
                                Course course = data.getT2();

                                int newEno = (lastEno == null) ? 1 : lastEno + 1;
                                Integer enrolmentCompanyNumber =
                                        (p.getCompanyNumber() == 0 ? null : p.getCompanyNumber());

                                // Wrap blocking void method in Mono.fromRunnable and run on boundedElastic
                                return Mono.fromRunnable(() ->
                                                enrolRepo.saveEnrolment(
                                                        session.getSessionId(),
                                                        newEno,
                                                        p.getPersonId(),
                                                        course.getPricePerDay(),
                                                        enrolmentCompanyNumber
                                                ))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .onErrorMap(dae -> {
                                            if (dae instanceof DataAccessException) {
                                                return new EnrolException("oops, something went wrong");
                                            }
                                            return dae;
                                        });
                            });
                })
                .then(); // Return Mono<Void>
    }*/

    @Transactional(rollbackFor = EnrolException.class)
    public void enrolForSession(PersonForm personIn, int sessionId) throws EnrolException {
        // your existing logic but fully blocking
        PersonDTO person = personService.addPerson(personIn)
                .onErrorResume(PersonAlreadyExistsException.class,
                        e -> personService.findPersonByEmail(personIn.getEmailAddress()))
                .block(); // blocking call to get PersonDTO
        System.out.println(person.getPersonId());
        try {
            Session session = sessionRepo.findById(sessionId);

            if (session == null) throw new EnrolException("Session not found");

            int count = enrolRepo.countEnrolmentsForSession(session.getSessionId());
            if (count >= 12) throw new EnrolException("Session is full");

            Enrolment existing = enrolRepo.findEnrolment(person.getPersonId(), session.getSessionId());
            if (existing != null) throw new EnrolException("Already enrolled");

            Integer lastEno = enrolRepo.getLastEnoForSession(session.getSessionId());
            int newEno = (lastEno == null) ? 1 : lastEno + 1;

            Course course = courseService.findCourseById(session.getCourseId()).block();

            // **blocking call inside the same transaction**
            enrolRepo.saveEnrolment(session.getSessionId(), newEno, person.getPersonId(),
                    course.getPricePerDay());
        } catch (EnrolException e){
            personService.deletePerson(person.getPersonId()).block();
            throw e;
        }
    }

    private Mono<Integer> getLastEnoForSession(int sessionId) {
        return Mono.just(enrolRepo.getLastEnoForSession(sessionId));
    }

    private Mono<Enrolment> findEnrolment(int personId, int sessionId) {
        return Mono.justOrEmpty(enrolRepo.findEnrolment(personId, sessionId));
    }

    private Mono<Integer> countEnrolmentsForSession(int sessionId) {
          return Mono.just(enrolRepo.countEnrolmentsForSession(sessionId));
    }

    public Mono<Session> findSession(int id){
        return Mono.just(sessionRepo.findById(id));
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

    @Override
    public Mono<Void> cancelSession(int id) {
        return Mono.fromRunnable(() -> {
            TransactionStatus tx = txManager.getTransaction(new DefaultTransactionDefinition());
            try {
                sessionRepo.cancelSession(id);  // JPA update
                txManager.commit(tx);
            } catch (Exception e) {
                txManager.rollback(tx);
                throw e;
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Transactional
    public Session addNewCompanySession(LocalDate startDate,int personId,int courseId) {
        CompanySession cs = new CompanySession(startDate,personId,courseId);
        Session newSession = sessionRepo.save(cs);
        return newSession;
    }

}
