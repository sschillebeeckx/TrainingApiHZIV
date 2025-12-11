package be.abis.exercise.repository;

import be.abis.exercise.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionJpaRepository extends JpaRepository<Session,Integer> {

    Session findById(int id);

    @Query("select s from Session s where s.courseId=:courseId")
    List<Session> findByCourseId(@Param("courseId") int courseId);

}
