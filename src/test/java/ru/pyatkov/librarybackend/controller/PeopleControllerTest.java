package ru.pyatkov.librarybackend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.pyatkov.librarybackend.controllers.PeopleController;
import ru.pyatkov.librarybackend.dto.PersonDTO;
import ru.pyatkov.librarybackend.models.Person;
import ru.pyatkov.librarybackend.security.PersonDetails;
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
        Person person = Person.builder().username("user1").password("password1").build();
        PersonDetails user = new PersonDetails(person);

        List<Person> people = List.of(Person.builder().fullName("Person 1").yearOfBirth(25).build(),
                Person.builder().fullName("Person 2").yearOfBirth(30).build());
        doReturn(people).when(peopleService).findAll();

        // when
        ResponseEntity<List<PersonDTO>> responseEntity = peopleController.getPeople();

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
