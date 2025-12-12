package be.abis.exercise.controller;

import be.abis.exercise.dto.EnrolmentDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class TrainingApiController {

    @Autowired
    TrainingService trainingService;

    @GetMapping("sessions/query")
    public Flux<SessionDTO> findSessionsForCourse(@RequestParam("title") String courseTitle){
        return trainingService.findSessionsForCourse(courseTitle);
    }

    @GetMapping("persons/{id}/enrolments")
    public Flux<EnrolmentDTO> findEnrolments(@PathVariable("id") int personId)
    {
        return trainingService.findEnrolments(personId);
    }

    @PostMapping("sessions/{id}/enrolments")
    public Mono<Void> enrollForSession(@RequestBody PersonForm person, @PathVariable("id") int sessionId) {
        return Mono.fromCallable(() -> {
                    trainingService.enrolForSession(person, sessionId); // can throw checked
                    return Void.TYPE; // dummy return
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then() // convert Mono<Object> to Mono<Void>
                .onErrorMap(ex -> {
                    if (ex instanceof EnrolException) return ex; // pass through
                    return new RuntimeException(ex); // wrap others
                });
    }

    @PatchMapping("sessions/{id}/cancel")
    public Mono<Void> cancelSession(@PathVariable("id") int id){
        return trainingService.cancelSession(id);
    }

}
