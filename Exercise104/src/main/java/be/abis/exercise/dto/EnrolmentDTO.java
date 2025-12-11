package be.abis.exercise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record EnrolmentDTO(
        String firstName,
        String lastName,
        String enrolleeCompanyName,
        @JsonFormat(pattern="dd/MM/yyyy")
        LocalDate startDate,
        String coursetitle) {
}
