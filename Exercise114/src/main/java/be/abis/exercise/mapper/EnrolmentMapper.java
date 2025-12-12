package be.abis.exercise.mapper;

import be.abis.exercise.dto.Course;
import be.abis.exercise.dto.EnrolmentDTO;
import be.abis.exercise.dto.NamedQueryResultDTO;
import be.abis.exercise.dto.PersonDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EnrolmentMapper {

    public static NamedQueryResultDTO toNamedQueryResultDTO(Object[] objArray) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.s");
        return new NamedQueryResultDTO(
                Integer.parseInt(objArray[0].toString()),
                LocalDate.parse(objArray[1].toString(),dtf),
                Integer.parseInt(objArray[2].toString())
        );
    }

    public static EnrolmentDTO toDTO(NamedQueryResultDTO nqDTO, PersonDTO p, Course c) {
       return new EnrolmentDTO(
              p.getFirstName(),
              p.getLastName(),
              p.getCompanyName(),
              nqDTO.sessionStartDate(),
              c.getLongTitle()
        );
    }
}
