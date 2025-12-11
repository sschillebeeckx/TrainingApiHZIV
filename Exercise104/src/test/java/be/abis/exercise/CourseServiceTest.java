package be.abis.exercise;

import be.abis.exercise.dto.Course;
import be.abis.exercise.exception.CourseNotFoundException;
import be.abis.exercise.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CourseServiceTest {
	
	@Autowired
    CourseService courseService;
	
	@Test
	public void course7900isWorkshopSQL() throws CourseNotFoundException {
		Course c = courseService.findCourseById(7900).block();
		assertEquals("SQLWS",c.getShortTitle().toUpperCase().trim());
	}

	@Test
	public void sizeListIs24(){
		List<Course> allCourses = courseService.findAllCourses().collectList().block();
		assertEquals(24,allCourses.size());
	}

	@Test
	public void courseSQLWStakes3Days() {
		String shortTitle = "SQLWS";
		Course found = courseService.findCourseByShortTitle(shortTitle).block();
		assertEquals(3,found.getNumberOfDays());
	}

	@Test
	@Transactional
	public void addCourse() {
		Course c = new Course("SPRINGJPA","Using JPA with Spring Boot",3,525.0);
		long sizeBefore = courseService.countAllCourses();
		courseService.addCourse(c).block();
		long sizeAfter = courseService.countAllCourses();
		assertEquals(1,sizeAfter-sizeBefore);
		//rollback
	}

	@Test
	@Transactional
	public void updateCourse() throws CourseNotFoundException {
		Course c = new Course("IMSADFII", "Using IMSADFII",4,530.45);
		c.setCourseId(7800);
		courseService.updateCourse(c).block();
		assertEquals("Using IMSADFII", courseService.findCourseById(7800).block().getLongTitle());
	}

	@Test
	@Transactional
	public void deleteCourse8055WhichHasNoChildren() {
		long sizeBefore = courseService.countAllCourses();
		int id = 8055;
		courseService.deleteCourse(id).block();
		long sizeAfter = courseService.countAllCourses();
		assertEquals(-1,sizeAfter-sizeBefore);
	}

	
	

}
