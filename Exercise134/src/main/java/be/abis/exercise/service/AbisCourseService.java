package be.abis.exercise.service;

import be.abis.exercise.dto.CountResult;
import be.abis.exercise.dto.Course;
import be.abis.exercise.exception.BadRequestException;
import be.abis.exercise.exception.CourseNotFoundException;
import be.abis.exercise.exception.NotAuthenticatedException;
import be.abis.exercise.exception.NotAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AbisCourseService implements CourseService {

   // WebClient courseClient = WebClient.create("http://localhost:8080/courseapi");

    WebClient courseClient = WebClient.builder()
            .baseUrl("http://localhost:8080/courseapi")
            .defaultHeaders(h -> h.setBasicAuth("abis01", "abis01"))
            .build();


    @Override
    public Flux<Course> findAllCourses() {
        return courseClient.get()
                .uri("/courses")
                .retrieve()
                .bodyToFlux(Course.class);
    }

    @Override
    public Mono<Course> findCourseById(int id) {
        return courseClient.get()
                .uri("/courses/{id}",id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        resp -> resp.bodyToMono(ProblemDetail.class)
                                .flatMap(pd -> Mono.error(new CourseNotFoundException(pd.getDetail()))))
                .bodyToMono(Course.class);
    }

    @Override
    public Mono<Course> findCourseByShortTitle(String shortTitle) {
        return courseClient.get()
                .uri(uriBuilder -> uriBuilder.path("/courses/query").queryParam("shortTitle", shortTitle).build())
                .retrieve()
                .bodyToMono(Course.class);
    }

    @Override
    public Mono<Course> addCourse(Course c) {
      return  courseClient.post()
                .uri("/courses")
                .headers(headers -> headers.setBasicAuth("abis01", "abis01"))
                .bodyValue(c)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        resp -> resp.bodyToMono(ProblemDetail.class).defaultIfEmpty(ProblemDetail.forStatusAndDetail(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "No ProblemDetail body in response"
                                ))
                                .flatMap(problem -> {
                                    if (resp.statusCode() == HttpStatus.BAD_REQUEST) {
                                        return Mono.error(new BadRequestException("bad request"));
                                    }  if (resp.statusCode() == HttpStatus.UNAUTHORIZED) {
                                        System.out.println("not authenticated");
                                        return Mono.error(new NotAuthenticatedException("you are not authenticated"));
                                    } else {
                                        return Mono.error(new RuntimeException("Other 4xx error"));
                                    }
                                }))
                .bodyToMono(Course.class);
    }

    @Override
    public Mono<Void> deleteCourse(int courseId) {
        return courseClient.delete()
                 .uri("/courses/{id}",courseId)
                .headers(headers -> headers.setBasicAuth("abis02", "abis02"))
                 .retrieve()
                 .onStatus(status -> status.is4xxClientError(),
                        resp -> resp.bodyToMono(ProblemDetail.class).defaultIfEmpty(ProblemDetail.forStatusAndDetail(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "No ProblemDetail body in response"
                                ))
                                .flatMap(problem -> {
                                    if (resp.statusCode() == HttpStatus.BAD_REQUEST) {
                                        return Mono.error(new BadRequestException("bad request"));
                                    }  if (resp.statusCode() == HttpStatus.UNAUTHORIZED) {
                                        System.out.println("not authenticated");
                                        return Mono.error(new NotAuthenticatedException("you are not authenticated"));
                                    } if (resp.statusCode() == HttpStatus.FORBIDDEN) {
                                        System.out.println("not authorized");
                                        return Mono.error(new NotAuthorizedException("you are not authenticated"));
                                    }
                                    else {
                                        return Mono.error(new RuntimeException("Other 4xx error"));
                                    }
                                }))
                 .bodyToMono(Void.class);
    }

    @Override
    public Mono<Course> updateCourse(Course c) {
        return courseClient.put()
                .uri("/courses/{id}",c.getCourseId())
                .bodyValue(c)
                .retrieve()
                .bodyToMono(Course.class);
    }

    @Override
    public Long countAllCourses() {
       return courseClient.get()
                .uri("/courses/count")
                .retrieve()
                .bodyToMono(CountResult.class)
                .block()
                .getCount();
    }

}
