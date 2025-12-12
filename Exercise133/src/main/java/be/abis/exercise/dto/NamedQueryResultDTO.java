package be.abis.exercise.dto;

import java.time.LocalDate;

public record NamedQueryResultDTO(
        int personId, LocalDate sessionStartDate, int courseId
) { }
