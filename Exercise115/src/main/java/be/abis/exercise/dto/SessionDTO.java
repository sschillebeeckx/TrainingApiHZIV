package be.abis.exercise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record SessionDTO(
        int sessionNumber,
        @JsonFormat(pattern="dd/MM/yyyy")
        LocalDate startDate,
        String instructorFirstName,
        String instructorLastName,
        String kind,
        boolean cancelled,
        String courseTitle) {
}
