package be.abis.exercise.controller;

import be.abis.exercise.model.Course;
import be.abis.exercise.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("courses")
public class CourseApiController {

    @Autowired
    CourseService courseService;

    @GetMapping("")
    public Flux<Course> findAllCourses(){
        return courseService.findAllCourses();
    }

    @GetMapping("{id}")
    public Mono<Course> printCourse(@PathVariable("id") int myId){
       return courseService.findCourseById(myId);
    }

    @GetMapping("/query")
    public Mono<Course> findCourseByTitle(@RequestParam("title") String shortTitle){
        return  courseService.findCourseByShortTitle(shortTitle);
    }

    @PostMapping("")
    public Mono<Course> addCourse(@RequestBody Course course){
      return courseService.addCourse(course);
    }

    @PutMapping("{id}")
    public Mono<Course> updateCourse(@RequestBody Course course, @PathVariable("id") int id){
        return courseService.updateCourse(course);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteCourse(@PathVariable("id") int id){
        return courseService.deleteCourse(id);
    }

}
