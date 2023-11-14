package ru.pyatkov.librarybackend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import ru.pyatkov.librarybackend.controllers.PeopleController;
import ru.pyatkov.librarybackend.dto.PersonDTO;
import ru.pyatkov.librarybackend.models.Person;
import ru.pyatkov.librarybackend.services.PeopleService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PeopleControllerTest {

    @Mock
    PeopleService peopleService;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    PeopleController peopleController;

    @Test
    @DisplayName("GET /people возвращает список задач")
    void getPeople_returnsValidData() {
        // given
        // TODO: с билдером не работает тест, поэтому добавил конструктор. Переделать
        // var people = List.of(Person.builder().fullName("Person 1").yearOfBirth(25),
        // Person.builder().fullName("Person 2").yearOfBirth(30));
        var people = List.of(new Person("Person 1", 25),
                new Person("Person 2", 30));
        doReturn(people).when(peopleService).findAll();

        // when
        var response = peopleController.getPeople();

        // then
        assertNotNull(response);
    }

    @Test
    @DisplayName("POST /people создает задачу и возвращает статус 200")
    void create_payloadIsValid_returnValidStatus() {
        // given
        var person = new PersonDTO("Person 2", 30);

        // when
        var response = peopleController.create(person);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
