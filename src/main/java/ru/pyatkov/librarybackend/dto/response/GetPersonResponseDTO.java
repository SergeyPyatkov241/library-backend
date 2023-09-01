package ru.pyatkov.librarybackend.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pyatkov.librarybackend.dto.BookDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPersonResponseDTO {

    @NotEmpty(message = "ФИО не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов длиной")
    private String fullName;

    @Min(value = 1900, message = "Год рождения должен быть больше, чем 1900")
    private int yearOfBirth;

    private List<BookDTO> books;

}
