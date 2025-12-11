package be.abis.exercise.service;

import be.abis.exercise.model.Course;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CourseService {

	Flux<Course> findAllCourses();
	Mono<Course> findCourseById(int id);
	Mono<Course> findCourseByShortTitle(String shortTitle);
	Mono<Course> addCourse(Course c)	;
	Mono<Void> deleteCourse(int courseId);
	Mono<Course> updateCourse(Course c);
	Long countAllCourses();
}
