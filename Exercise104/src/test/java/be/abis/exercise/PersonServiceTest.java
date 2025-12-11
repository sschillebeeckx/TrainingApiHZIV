package be.abis.exercise;

import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.exception.PersonAlreadyExistsException;
import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.service.PersonService;
import be.abis.exercise.util.DateUtils;
import be.abis.exercise.util.SqlScriptRunner;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersonServiceTest {
	
	@Autowired
    PersonService personService;

	@Test
	@Order(1)
	public void person1ShouldBeCalledJAN() throws PersonNotFoundException {
		String firstName = personService.findPerson(1).block().getFirstName();
		assertEquals("JAN",firstName);
	}

	@Test
	@Order(2)
	public void thereShouldBe45PersonsInTheDB(){
		int nrOfPersons = personService.getAllPersons().collectList().block().size();
		assertEquals(45,nrOfPersons);
	}

	@Test
	@Order(3)
	public void person3ByEmailAndPassword() throws PersonNotFoundException {
		PersonDTO person = personService.findPerson("gert.vanheijkoop@computrain.nl","gvh456").block();
		assertEquals("GERT",person.getFirstName().trim());
	}

	@Test
	@Order(4)
	public void thereAre3PersonsForAbis()  {
		List<PersonDTO> persons = personService.findPersonsByCompanyName("abis").block().getPersons();
		assertEquals(3,persons.size());
	}

	@Test
	@Order(5)
	public void addNewPersonWithNewCompany() throws PersonAlreadyExistsException {
		PersonForm p = new PersonForm("Sandy","Schillebeeckx", LocalDate.of(1978,04,10),"sschillebeeckx@abis.be","abis123","nl","BBIS","016/123455","BE12345678","Some Street","32","1000","BRUSSEL","B");
		long sizePersonsBefore = personService.countAllPersons();
		personService.addPerson(p).block();
		long sizePersonsAfter = personService.countAllPersons();
		assertEquals(1,sizePersonsAfter-sizePersonsBefore);
	}

	@Test
	@Order(6)
	public void addNewPersonWithExistingCompany() throws PersonAlreadyExistsException {
		PersonForm p = new PersonForm("Sandy","Schillebeeckx", LocalDate.of(1978,04,10),"sschillebeeckx@abis.be","abis123","nl","ABIS N.V.","016/455610","BE12345678","Diestsevest","32","3000","LEUVEN","B");
		long sizePersonsBefore = personService.countAllPersons();
		personService.addPerson(p).block();
		long sizePersonsAfter = personService.countAllPersons();
		assertEquals(1,sizePersonsAfter-sizePersonsBefore);
	}

	@Test
	@Order(7)
	public void addNewPersonWithoutCompany() throws PersonAlreadyExistsException {
		PersonForm p = new PersonForm("Sandy","Schillebeeckx", LocalDate.of(1978,04,10),"sschillebeeckx@abis.be","abis123","nl");
		long sizeBefore = personService.countAllPersons();
		PersonDTO added = personService.addPerson(p).block();
		long sizeAfter = personService.countAllPersons();
		assertEquals(1,sizeAfter-sizeBefore);
		assertTrue(added.getCompanyName()==null);
	}

	@Test
	@Order(8)
	public void changePassWordOfPerson3() throws PersonNotFoundException {
		PersonForm p = new PersonForm("ANN","DE KEYSER",LocalDate.of(1982,02,02),"ann.dekeyser@abis.be","blabla","NL");
		PersonDTO updated =personService.changePasswordPut(3,p,p.getPassword()).block();
		assertEquals("ANN",updated.getFirstName());
	}

	@Test
	@Order(9)
	public void deletePerson92WorksForComp20()  {
		long sizeBefore = personService.countAllPersons();
		personService.deletePerson(92).block();
		long sizeAfter = personService.countAllPersons();
		assertEquals(-1,sizeAfter-sizeBefore);
	}

	@Autowired
	private SqlScriptRunner sqlScriptRunner;

	@AfterAll
	void runAfterAllTests() {
		sqlScriptRunner.runSqlScript("/createOracleDB.sql"); // path in resources
	}
}


