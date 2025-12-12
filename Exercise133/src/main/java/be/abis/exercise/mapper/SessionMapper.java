package be.abis.exercise.mapper;

import be.abis.exercise.dto.Course;
import be.abis.exercise.dto.PersonDTO;
import be.abis.exercise.dto.SessionDTO;
import be.abis.exercise.model.Session;

public class SessionMapper {

    public static SessionDTO toDTO(Course c, Session session, PersonDTO person) {
       return new SessionDTO(
               session.getSessionId(),
               session.getStartDate(),
               person.getFirstName(),
               person.getLastName(),
               session.getKind(),
               session.isCancelled(),
               c.getLongTitle()
       );

    }
}
