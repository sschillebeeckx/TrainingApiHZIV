package be.abis.exercise;

import be.abis.exercise.dto.EnrolmentDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.exception.SessionCanNotBeDeletedException;
import be.abis.exercise.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class TrainingServiceTest {

	@Autowired
	TrainingService trainingService;

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



	
	

}
