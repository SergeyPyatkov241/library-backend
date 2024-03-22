package ru.pyatkov.librarybackend.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pyatkov.librarybackend.dto.PersonDTO;
import ru.pyatkov.librarybackend.dto.response.GetPersonResponseDTO;
import ru.pyatkov.librarybackend.models.Person;
//import ru.pyatkov.librarybackend.security.PersonDetails;
import ru.pyatkov.librarybackend.services.PeopleService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/people")
public class PeopleController {

    private final PeopleService peopleService;

    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getPeople() {
        log.info("Entering endpoint: /people");
        List<PersonDTO> response = peopleService.findAll().stream()
                .map(person -> convertToDTO(person, PersonDTO.class))
                .collect(Collectors.toList());
        ResponseEntity<List<PersonDTO>> responseEntity = ResponseEntity.ok(response);
        log.info("Endpoint: /people return result");
        return responseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPersonResponseDTO> getPerson(@PathVariable("id") int id) {
        log.info("Entering endpoint: /people/{}", id);
        GetPersonResponseDTO response = convertToDTO(peopleService.findOne(id), GetPersonResponseDTO.class);
        ResponseEntity<GetPersonResponseDTO> responseEntity = ResponseEntity.ok(response);
        log.info("Endpoint: /people/{} return result - {}", id, responseEntity);
        return responseEntity;
    }

    @PostMapping()
    public ResponseEntity<PersonDTO> createPerson(@RequestBody @Valid PersonDTO personDTO) {
        log.info("Entering endpoint: /people request - {}", personDTO);
        peopleService.save(convertToPerson(personDTO));
        ResponseEntity<PersonDTO> responseEntity = ResponseEntity.ok(personDTO);
        log.info("Endpoint: /people return result - {}", responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@RequestBody @Valid PersonDTO personDTO, @PathVariable("id") int id) {
        log.info("Entering endpoint: /people/{} request - {}", id, personDTO);
        peopleService.update(id, convertToPerson(personDTO));
        ResponseEntity<PersonDTO> responseEntity = ResponseEntity.ok(personDTO);
        log.info("Endpoint: /people/{} return result - {}", id, responseEntity);
        return responseEntity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable("id") int id) {
        log.info("Entering endpoint: /people/{}", id);
        peopleService.delete(id);
        ResponseEntity<?> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        log.info("Endpoint: /people/{} return result - {}", id, responseEntity);
        return responseEntity;
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private <T> T convertToDTO(Person person, Class<T> targetClass) {
        return modelMapper.map(person, targetClass);
    }

}
