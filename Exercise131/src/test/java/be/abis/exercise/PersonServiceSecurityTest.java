package be.abis.exercise;

import be.abis.exercise.dto.LoginResult;
import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.exception.ApiKeyWrongException;
import be.abis.exercise.exception.LoginException;
import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.service.PersonService;
import be.abis.exercise.util.SqlScriptRunner;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonServiceSecurityTest {
	
	@Autowired
    PersonService personService;

	@Test
	@Order(1)
	public void person3WrongLogin()  {
		Throwable ex = assertThrows(RuntimeException.class, ()->personService.findPerson("gert.vanheijkoop@computrain.nl","wrong").block());
		assertTrue(ex.getCause() instanceof LoginException);
	}

	@Test
	@Order(2)
	public void person3ByEmailAndPassword()  {
		LoginResult result = personService.findPerson("gert.vanheijkoop@computrain.nl","gvh456").block();
		System.out.println("api key is :" + result.apiKey());
		assertEquals("GERT",result.person().getFirstName().trim());
	}

	@Test
	@Order(3)
	public void changePassWordOfPerson3WithCorrectKey()  {
		LoginResult result = personService.findPerson("ann.dekeyser@abis.be","adk789").block();
		String key = result.apiKey();
		System.out.println("key= "+ key);
		PersonDTO found = result.person();
		PersonForm p = new PersonForm("ANN","DE KEYSER",LocalDate.of(1982,02,02),"ann.dekeyser@abis.be","blabla","NL");
		PersonDTO updated = personService.changePasswordPut(found.getPersonId(),p,p.getPassword(),key).block();
		assertEquals("ANN",updated.getFirstName());
	}

	@Test
	@Order(4)
	public void changePassWordOfPerson3WithWrongKey()  {
		LoginResult result = personService.findPerson("gert.vanheijkoop@computrain.nl","gvh456").block();
		String key = result.apiKey();
		System.out.println("key= "+ key);
		PersonDTO found = result.person();
		PersonForm p = new PersonForm("ANN","DE KEYSER",LocalDate.of(1982,02,02),"ann.dekeyser@abis.be","abcde","NL");
		Throwable ex = assertThrows(RuntimeException.class,()-> personService.changePasswordPut(3,p,p.getPassword(),key).block());
		assertTrue(ex.getCause() instanceof ApiKeyWrongException);
	}








}


