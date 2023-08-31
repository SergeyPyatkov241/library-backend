package ru.pyatkov.librarybackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorEntityResponseDTO {

    private String message;

    private String className;

    private Date timestamp;

}
