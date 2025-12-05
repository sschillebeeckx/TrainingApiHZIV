package be.abis.exercise.service;

import be.abis.exercise.model.Course;

import java.util.List;


public interface CourseService {

	List<Course> findAllCourses();
	Course findCourseById(int id);
	Course findCourseByShortTitle(String shortTitle);
	void addCourse(Course c)	;
	void deleteCourse(int courseId);
	void updateCourse(Course c);
}
