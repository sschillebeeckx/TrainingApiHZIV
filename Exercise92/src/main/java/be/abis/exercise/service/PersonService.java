package be.abis.exercise.service;

import be.abis.exercise.dto.Persons;
import be.abis.exercise.model.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonService {
	Flux<Person> getAllPersons();
    Mono<Person> findPerson(int id);
    Mono<Person> findPerson(String emailAddress, String passWord);
    Mono<Person> addPerson(Person p);
    Mono<Void> deletePerson(int id);
    Mono<Person> changePasswordPut(Person p, String newPswd);
    Mono<Person> changePasswordPatch(Person p, String newPswd);
    Mono<Persons> findPersonsByCompanyName(String compName);
    Long countAllPersons();
}
