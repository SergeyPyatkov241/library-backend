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
import ru.pyatkov.librarybackend.services.PeopleService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;

    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<PersonDTO> getPeople() {
        log.info("Entering endpoint: /people");
        return peopleService.findAll().stream()
                .map(person -> convertToDTO(person, PersonDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public GetPersonResponseDTO getPerson(@PathVariable("id") int id) {
        log.info("Entering endpoint: /people/{}", id);
        return convertToDTO(peopleService.findOne(id), GetPersonResponseDTO.class);

    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO) {
        log.info("Entering endpoint: /people/create request - {}", personDTO);
        peopleService.save(convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid PersonDTO personDTO, @PathVariable("id") int id) {
        log.info("Entering endpoint: /people/{} request - {}", id, personDTO);
        peopleService.update(id, convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        log.info("Entering endpoint: /people/{}", id);
        peopleService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private <T> T convertToDTO(Person person, Class<T> targetClass) {
        return modelMapper.map(person, targetClass);
    }

}
