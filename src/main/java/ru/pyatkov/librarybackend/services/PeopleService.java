package ru.pyatkov.librarybackend.services;

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

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElseThrow(() -> { throw new EntityNotFoundException("Человека с таким id не существует", "PeopleService");});
    }

    @Transactional
    public void save(Person person) {
        validateFullName(person.getFullName());
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        findOne(updatedPerson.getId());
        validateFullName(updatedPerson.getFullName());
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        findOne(id);
        peopleRepository.deleteById(id);
    }

    public Optional<Person> findByFullName(String fullName) {
        return peopleRepository.findByFullName(fullName);
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
        if (findByFullName(fullName).isPresent()) {
            throw new EntityNotCreatedException("Человек с таким ФИО уже существует", "PeopleService");
        }
    }

}
