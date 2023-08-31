package ru.pyatkov.librarybackend.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponseDTO {

    private final List<Violation> violations;

}