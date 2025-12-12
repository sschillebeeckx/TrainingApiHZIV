package be.abis.exercise.repository;

import be.abis.exercise.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionJpaRepository extends JpaRepository<Session,Integer> {

    Session findById(int id);

    @Query(value = "select * from sessions where s_cid=:courseId and scancel is null",nativeQuery = true)
    List<Session> findByCourseId(@Param("courseId") int courseId);

    @Modifying
    @Query(value = "update sessions set scancel = 'c' where sno = :sessionId", nativeQuery = true)
    void cancelSession(int sessionId);
}
