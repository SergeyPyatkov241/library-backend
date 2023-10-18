package ru.pyatkov.librarybackend.services;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pyatkov.librarybackend.exceptions.EntityNotCreatedException;
import ru.pyatkov.librarybackend.exceptions.EntityNotFoundException;
import ru.pyatkov.librarybackend.models.Person;
import ru.pyatkov.librarybackend.models.Book;
import ru.pyatkov.librarybackend.repositories.PeopleRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        log.info("PeopleService.findAll entering");
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        log.info("PeopleService.findOne entering: args {}", id);
        Optional<Person> foundPerson = peopleRepository.findById(id);
        if(foundPerson.isPresent()) {
            Person returnPerson = foundPerson.get();
            log.info("PeopleService.findOne result: found person - '{}'", returnPerson);
            return returnPerson;
        } else {
            log.error("PeopleService.findOne error: person with id = '{}' doesn't exists", id);
            throw new EntityNotFoundException("Человека с таким id не существует", "PeopleService");
        }
    }

    @Transactional
    public void save(Person person) {
        log.info("PeopleService.save entering: args {}", person);
        validateFullName(person.getFullName());
        peopleRepository.save(person);
        log.info("PeopleService.save result: created new person - '{}'", person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        log.info("PeopleService.update entering: args {}, {}", id, updatedPerson);
        updatedPerson.setId(id);
        findOne(updatedPerson.getId());
        validateFullName(updatedPerson.getFullName());
        peopleRepository.save(updatedPerson);
        log.info("PeopleService.update result: updated person - '{}'", updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        log.info("PeopleService.delete entering: args {}", id);
        findOne(id);
        peopleRepository.deleteById(id);
        log.info("PeopleService.delete result: deleted person with id = '{}'", id);
    }

    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);

        if(person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());

            person.get().getBooks().forEach(book -> {
                long diffInMilliseconds = Math.abs(book.getTakenAt().getTime() - new Date().getTime());

                if (diffInMilliseconds > 864000000)
                    book.setExpired(true);
            });

            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }
    }

    private void validateFullName(String fullName) {
        log.info("PeopleService.validateFullName entering: args {}", fullName);
        if (findByFullName(fullName).isPresent()) {
            log.error("PeopleService.validateFullName: error -- person with fullName = '{}' already exist", fullName);
            throw new EntityNotCreatedException("Человек с таким ФИО уже существует", "PeopleService");
        } else {
            log.info("PeopleService.validateFullName result: OK, person with fullName = '{}' doesn't exist", fullName);
        }
    }

    public Optional<Person> findByFullName(String fullName) {
        log.info("PeopleService.findByFullName entering: args {}", fullName);
        return peopleRepository.findByFullName(fullName);
    }

}
