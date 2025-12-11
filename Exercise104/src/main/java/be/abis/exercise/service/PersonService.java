package be.abis.exercise.service;

import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.dto.Persons;
import be.abis.exercise.form.PersonForm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonService {
	Flux<PersonDTO> getAllPersons();
    Mono<PersonDTO> findPerson(int id);
    Mono<PersonDTO> findPerson(String emailAddress, String passWord);
    Mono<PersonDTO> addPerson(PersonForm p);
    Mono<Void> deletePerson(int id);
    Mono<PersonDTO> changePasswordPut(int id, PersonForm p, String newPswd);
    Mono<PersonDTO> changePasswordPatch(int id, PersonForm p, String newPswd);
    Mono<Persons> findPersonsByCompanyName(String compName);
    Long countAllPersons();
}
