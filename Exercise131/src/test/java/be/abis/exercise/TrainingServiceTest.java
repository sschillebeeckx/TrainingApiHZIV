package be.abis.exercise;

import be.abis.exercise.dto.EnrolmentDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.exception.EnrolException;
import be.abis.exercise.exception.PersonNotFoundException;
import be.abis.exercise.exception.SessionCanNotBeDeletedException;
import be.abis.exercise.form.PersonForm;
import be.abis.exercise.model.PublicSession;
import be.abis.exercise.model.Session;
import be.abis.exercise.repository.SessionJpaRepository;
import be.abis.exercise.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TrainingServiceTest {

	@Autowired
	TrainingService trainingService;

	@Autowired
	SessionJpaRepository sessionJpaRepository;

	@Test
	public void thereIs1NonCancelledSessionForDB2BAS(){
		List<SessionDTO> sessions = trainingService.findSessionsForCourse("DB2BAS").collectList().block();
		assertEquals(1,sessions.size());
	}

	@Test
	public void thereAre3NonCancelledEnrolmentForPerson25(){
		List<EnrolmentDTO> enrolments = trainingService.findEnrolments(25).collectList().block();
		assertEquals(3,enrolments.size());
	}


	@Test
	public void deleteSession17works(){
		trainingService.deleteSession(17).block();
	}

	@Test
	public void deleteSession1ThrowsException(){
		assertThrows(SessionCanNotBeDeletedException.class,()->trainingService.deleteSession(1).block());
	}

	@Test
	public void cancelSession1() {
		trainingService.cancelSession(1).block();
		assertTrue(sessionJpaRepository.findById(1).isCancelled());
	}

	@Test
	public void enrolNewPersonForExistingSession() throws EnrolException {
		PersonForm p = new PersonForm("Sandy","Schillebeeckx", LocalDate.of(1978,04,10),"sschillebeeckx@abis.be","abis123","nl","BBIS","016/123455","BE12345678","Some Street","32","1000","BRUSSEL","B");
        trainingService.enrolForSession(p,1);
		//check if company, person and enrolment added
	}

	@Test
	public void enrolExistingPersonForExistingSession() throws EnrolException {
		PersonForm p = new PersonForm("ANN","DE KEYSER",LocalDate.of(1982,02,02),"ann.dekeyser@abis.be","blabla","NL");
		trainingService.enrolForSession(p,1);
		//check if enrolment added for person 3
	}

	@Test
	public void enrolNewPersonForWrongSession() throws EnrolException {
		PersonForm p = new PersonForm("An","Schillebeeckx", LocalDate.of(1982,05,23),"an.schillebeeckx@some.be","abis123","nl","BBIS","016/123455","BE12345678","Some Street","32","1000","BRUSSEL","B");
		assertThrows(EnrolException.class,()->trainingService.enrolForSession(p,150));
		//check if everything rolled back
	}

	@Test
	public void session1shouldBeAPublicSession(){
		Session s = sessionJpaRepository.findById(1);
		System.out.println(s);
		System.out.println(" session is a " + s.getClass().getSimpleName());
		assertTrue(s instanceof PublicSession);
	}

	@Test
	//@Transactional
	public void addNewCompanySession() throws PersonNotFoundException {
		long sessionsBefore = sessionJpaRepository.count();
		Session added = trainingService.addNewCompanySession(LocalDate.of(2025,12,8),3,7850);
		System.out.println(sessionJpaRepository.findById(added.getSessionId()));
		long sessionsAfter = sessionJpaRepository.count();
		assertEquals(1,sessionsAfter-sessionsBefore);
		assertEquals("c",added.getKind());
		assertEquals("c",sessionJpaRepository.findSkindValue(added.getSessionId()));
	}

}
