package be.abis.exercise.service;

import be.abis.exercise.model.Person;
import be.abis.exercise.model.Persons;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface PersonService {
	Flux<Person> getAllPersons();
    Mono<Person> findPerson(int id);
    Mono<Person> findPerson(String emailAddress, String passWord);
    Mono<Void> addPerson(Person p);
    Mono<Void> deletePerson(int id);
    Mono<Void> changePasswordPut(Person p, String newPswd);
    Mono<Void> changePasswordPatch(Person p, String newPswd);
    Mono<Persons> findPersonsByCompanyName(String compName);
}
