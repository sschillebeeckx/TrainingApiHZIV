package be.abis.exercise.service;

import be.abis.exercise.dto.CountResult;
import be.abis.exercise.dto.LoginResult;
import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.dto.Persons;
import be.abis.exercise.exception.*;
import be.abis.exercise.form.Login;
import be.abis.exercise.form.Password;
import be.abis.exercise.form.PersonForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AbisPersonService implements PersonService {

	WebClient personClient = WebClient.create("http://localhost:8081/exercise/personapi");

	@Override
	public Flux<PersonDTO> getAllPersons() {
		return personClient.get()
				.uri("/persons")
				.retrieve()
				.bodyToFlux(PersonDTO.class);
	}

	@Override
	public Mono<PersonDTO> findPerson(int id) {
		return personClient.get()
				.uri("/persons/{id}",id)
				.retrieve()
				.onStatus(status -> status.is4xxClientError(),
						resp -> resp.bodyToMono(ProblemDetail.class)
								.flatMap(pd -> Mono.error(new PersonNotFoundException(pd.getDetail()))))
				.bodyToMono(PersonDTO.class);
	}

	@Override
	public Mono<PersonDTO> findPersonByEmail(String email) {
		return personClient.get()
				.uri(uriBuilder -> uriBuilder.path("/persons/emailquery").queryParam("email", email).build())
				.retrieve()
				.bodyToMono(PersonDTO.class);
	}

	@Override
	public Mono<LoginResult> findPerson(String emailAddress, String passWord) {
		Login login = new Login(emailAddress, passWord);
		return personClient.post()
				.uri("/persons/login")
				.bodyValue(login)
				.exchangeToMono(response -> {
					if (response.statusCode().is4xxClientError()) {
						return response.bodyToMono(ProblemDetail.class)
								.flatMap(pd -> Mono.error(new LoginException(pd.getDetail())));
					}

					String apiKey = response.headers().asHttpHeaders().getFirst("X-API-Key");
					System.out.println("api key is" + apiKey);
					return response.bodyToMono(PersonDTO.class)
							.map(person -> new LoginResult(person, apiKey));
				});

	}

	@Override
	public Mono<Persons> findPersonsByCompanyName(String compName) {
		return personClient.get()
				.uri(uriBuilder -> uriBuilder.path("/persons/query").queryParam("compname", compName).build())
				//.accept(MediaType.APPLICATION_XML)
				.retrieve()
				.onStatus(status -> status.is4xxClientError(),
						resp -> resp.bodyToMono(XMLProblemDetail.class)
								.flatMap(pd -> Mono.error(new NoPersonsFoundException(pd.getDetail()))))
				.bodyToMono(Persons.class);
	}

	@Override
	public Long countAllPersons() {
		return personClient.get()
				.uri("/persons/count")
				.retrieve()
				.bodyToMono(CountResult.class)
				.block()
				.getCount();
	}

	@Override
	public Mono<PersonDTO> addPerson(PersonForm p) {
		return personClient.post()
				.uri("/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(p)
				.retrieve()
				.onStatus(status -> status.is4xxClientError(),
						resp -> resp.bodyToMono(ProblemDetail.class)
								.flatMap(problem -> {
									if (resp.statusCode() == HttpStatus.BAD_REQUEST) {
										return Mono.error(new BadRequestException("bad request"));
									} else if (resp.statusCode() == HttpStatus.CONFLICT) {
										return Mono.error(new PersonAlreadyExistsException(problem.getDetail()));
									} else {
										return Mono.error(new RuntimeException("Other 4xx error"));
									}
								}))
				.bodyToMono(PersonDTO.class);
	}

	@Override
	public Mono<PersonDTO> changePasswordPut(int id, PersonForm p, String newPswd,String apiKey) {
		p.setPassword(newPswd);
		return personClient.put()
				.uri("/persons/"+id)
				.header("X-API-Key", apiKey)
				.bodyValue(p)
				.retrieve()
				.onStatus(
						status -> status.value() == HttpStatus.UNAUTHORIZED.value(), // only catch 401
						response -> response.bodyToMono(ProblemDetail.class)
								.flatMap(pd -> Mono.error(new ApiKeyWrongException(pd.getDetail())))
				)
				.bodyToMono(PersonDTO.class);
	}

	@Override
	public Mono<PersonDTO> changePasswordPatch(int id, PersonForm p, String newPswd) {
		Password pwd = new Password(newPswd);
		return personClient.patch()
				.uri("/persons/"+id+"/password")
				.bodyValue(pwd)
				.retrieve()
				.bodyToMono(PersonDTO.class);
	}
	@Override
	public Mono<Void> deletePerson(int id) {
		return personClient.delete()
				.uri("/persons/{id}",id)
				.retrieve()
				.bodyToMono(Void.class);
	}

}
