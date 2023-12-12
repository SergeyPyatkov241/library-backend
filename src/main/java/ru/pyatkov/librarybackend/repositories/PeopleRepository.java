package ru.pyatkov.librarybackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pyatkov.librarybackend.models.Person;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByFullName(String fullName);

    Optional<Person> findByUsername(String username);

}
