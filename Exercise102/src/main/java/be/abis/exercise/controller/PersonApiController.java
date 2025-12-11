package be.abis.exercise.controller;

import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.dto.Persons;
import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.form.Login;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("persons")
public class PersonApiController {

    @Autowired
    PersonService personService;

    @Autowired
    PersonService ps;

    @GetMapping("")
    public Flux<PersonDTO> getAllPersons(){
        return ps.getAllPersons();
    }

    @GetMapping("{id}")
    public Mono<PersonDTO> findPerson(@PathVariable("id") int id) throws PersonNotFoundException {
       return ps.findPerson(id);
    }

    @PostMapping("/login")
    public Mono<PersonDTO> findPersonByMailAndPwd(@RequestBody Login login) throws PersonNotFoundException {
        return ps.findPerson(login.getEmail(),login.getPassword());
    }

    @GetMapping(path="/compquery")
    public Mono<Persons> findPersonsByCompanyName(@RequestParam("compname") String compName) {
        return ps.findPersonsByCompanyName(compName);
    }

    @PostMapping(path="")
    public Mono<PersonDTO> addPerson(@RequestBody PersonForm p) throws PersonAlreadyExistsException {
        return ps.addPerson(p);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deletePerson(@PathVariable("id") int id)  {
        return ps.deletePerson(id);
    }

    @PutMapping("{id}")
    public Mono<PersonDTO> changePassword(@PathVariable("id") int id, @RequestBody PersonForm person) throws PersonNotFoundException {
        System.out.println("changing password to newpswd= " + person.getPassword());
        return ps.changePasswordPut(id,person, person.getPassword());
    }





}
