package be.abis.exercise.service;

import be.abis.exercise.exception.CourseNotFoundException;
import be.abis.exercise.model.Course;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AbisCourseService implements CourseService {

    WebClient courseClient = WebClient.create("http://localhost:8080/courseapi");

    @Override
    public List<Course> findAllCourses() {
        return courseClient.get()
                .uri("/courses")
                .retrieve()
                .bodyToFlux(Course.class)
                .collectList()
                .block();
    }

    @Override
    public Course findCourseById(int id) throws CourseNotFoundException {
        try {
            return courseClient.get()
                    .uri("/courses/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            resp -> resp.bodyToMono(ProblemDetail.class)
                                    .flatMap(pd -> Mono.error(new CourseNotFoundException(pd.getDetail()))))
                    .bodyToMono(Course.class)
                    .block();
        } catch (RuntimeException re){
                throw new CourseNotFoundException(re.getMessage());
        }
    }

    @Override
    public Course findCourseByShortTitle(String shortTitle) {
        return courseClient.get()
                .uri(uriBuilder -> uriBuilder.path("/courses/query").queryParam("shortTitle", shortTitle).build())
                .retrieve()
                .bodyToMono(Course.class)
                .block();
    }

    @Override
    public void addCourse(Course c) {

    }

    @Override
    public void deleteCourse(int courseId) {

    }

    @Override
    public void updateCourse(Course c) {

    }

}
