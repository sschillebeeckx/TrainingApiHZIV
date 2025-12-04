package be.abis.exercise;

import be.abis.exercise.model.Address;
import be.abis.exercise.model.Company;
import be.abis.exercise.model.Person;
import be.abis.exercise.model.Persons;
import be.abis.exercise.service.PersonService;
import be.abis.exercise.util.DateUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonServiceTest {

    @Autowired
    PersonService ps;

    Company c;

    @BeforeEach
    public void setUp() {
        Address a = new Address("Diestsevest","32 bus 4b","3000","Leuven","B");
        c = new Company("Abis","016/455610","BE12345678",a);
    }

    @Test
    @Order(1)
    void person1IsJohn() {
        assertEquals("John",ps.findPerson(1).block().getFirstName());
    }

    @Test
    @Order(2)
    public void startSizeOfFileIs3() {
        List<Person> pList= ps.getAllPersons().collectList().block();
        System.out.println(pList);
        assertEquals(3,pList.size());
    }

    @Test
    @Order(3)
    public void thereAre2PersonsWorkingForAbis() {
        List<Person> pList= ps.findPersonsByCompanyName("Abis").block().getPersons();
        System.out.println(pList);
        assertEquals(2,pList.size());
    }

    @Test
    @Order(4)
    public void loginJohn()  {
        assertEquals("John",ps.findPerson("jdoe@abis.be","def456").block().getFirstName());
    }

    @Test
    @Order(5)
    public void addingNewPersonWorks() {
        Person newPerson = new Person(4,"Sandy","Schillebeeckx", DateUtils.parse("10/4/1978"),"sschillebeeckx@abis.be","mypass124","nl",c);
        ps.addPerson(newPerson).block();
    }

    @Test
    @Order(6)
    public void changePassWordOfAddedPersonViaPut()  {
        Person p = ps.findPerson("sschillebeeckx@abis.be","mypass124").block();
        ps.changePasswordPut(p,"blabla").block();
    }

    @Test
    @Order(7)
    public void changePassWordOfAddedPersonViaPatch()  {
        Person p = ps.findPerson("sschillebeeckx@abis.be","blabla").block();
        ps.changePasswordPatch(p,"abis23123").block();
    }

    @Test
    @Order(8)
    public void deleteAddedPerson(){
        ps.deletePerson(4).block();
    }




}
