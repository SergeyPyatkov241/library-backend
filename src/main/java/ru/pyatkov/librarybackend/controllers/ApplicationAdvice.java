package ru.pyatkov.librarybackend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.pyatkov.librarybackend.dto.response.ErrorEntityResponseDTO;
import ru.pyatkov.librarybackend.dto.response.ValidationErrorResponseDTO;
import ru.pyatkov.librarybackend.dto.response.Violation;
import ru.pyatkov.librarybackend.exceptions.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ApplicationAdvice {

    @ExceptionHandler(EntityException.class)
    private ResponseEntity<ErrorEntityResponseDTO> handleException(EntityException e) {
        ErrorEntityResponseDTO response = new ErrorEntityResponseDTO(
                e.getMessage(),
                e.getClassName(),
                new Date());
        log.error("Error message - '{}', error class - '{}'", e.getMessage(), e.getClassName());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ValidationErrorResponseDTO> handleException(MethodArgumentNotValidException e) {
        List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        ValidationErrorResponseDTO response = new ValidationErrorResponseDTO(violations);
        log.error("Error message: '{}'", response.getViolations());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
