package be.abis.exercise;

import be.abis.exercise.model.Course;
import be.abis.exercise.service.CourseService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseServiceTest {

    @Autowired
    CourseService cs;

    @Test
    @Order(1)
    public void startNumberOfCoursesIs5() {
        List<Course> cList= cs.findAllCourses();
        assertEquals(5,cList.size());
    }

    @Test
    @Order(2)
    public void course8100IsSpring() {
        Course course = cs.findCourseById(8100);
        assertEquals("Spring",course.getShortTitle());
    }

    @Test
    @Order(3)
    public void courseSpringHasDuration3() {
        Course course = cs.findCourseByShortTitle("Spring");
        assertEquals(3,course.getNumberOfDays());
    }

}
