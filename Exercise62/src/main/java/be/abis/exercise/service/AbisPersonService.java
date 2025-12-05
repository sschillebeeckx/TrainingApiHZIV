package be.abis.exercise.service;

import be.abis.exercise.model.Login;
import be.abis.exercise.model.Password;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Persons;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AbisPersonService implements PersonService {

	WebClient personClient = WebClient.create("http://localhost:8081/exercise/personapi");
	
	@Override
	public Flux<Person> getAllPersons() {
		return personClient.get()
				.uri("/persons")
				.retrieve()
				.bodyToFlux(Person.class);
	}

	@Override
	public Mono<Person> findPerson(int id) {
		return personClient.get()
				.uri("/persons/{id}",id)
				.retrieve()
				.bodyToMono(Person.class);
	}

	@Override
	public Mono<Person> findPerson(String emailAddress, String passWord) {
		Login login = new Login(emailAddress, passWord);
		return personClient.post()
				.uri("/persons/login")
				.bodyValue(login)
				.retrieve()
				.bodyToMono(Person.class);
	}

	@Override
	public Mono<Persons> findPersonsByCompanyName(String compName) {
		return personClient.get()
				.uri(uriBuilder -> uriBuilder.path("/persons/query").queryParam("compname", compName).build())
				//.accept(MediaType.APPLICATION_XML)
				.retrieve()
				.bodyToMono(Persons.class);
	}

	@Override
	public Mono<Void> addPerson(Person p) {
		System.out.println(p);
		return personClient.post()
				.uri("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(p)
				.retrieve()
				.bodyToMono(Void.class);
	}

	@Override
	public Mono<Void> changePasswordPut(Person p, String newPswd) {
		p.setPassword(newPswd);
		System.out.println(p.getPersonId());
		return personClient.put()
				.uri("/persons/"+p.getPersonId())
				.bodyValue(p)
				.retrieve()
				.bodyToMono(Void.class);
	}

	@Override
	public Mono<Void> changePasswordPatch(Person p, String newPswd) {
		Password pwd = new Password(newPswd);
		return personClient.patch()
				.uri("/persons/"+p.getPersonId()+"/password")
				.bodyValue(pwd)
				.retrieve()
				.bodyToMono(Void.class);
	}
	@Override
	public Mono<Void> deletePerson(int id) {
		return personClient.delete()
				.uri("/persons/{id}",id)
				.retrieve()
				.bodyToMono(Void.class);
	}

}
