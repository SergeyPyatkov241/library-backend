package ru.pyatkov.librarybackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.pyatkov.librarybackend.models.Person;
import ru.pyatkov.librarybackend.repositories.PeopleRepository;
import ru.pyatkov.librarybackend.security.PersonDetails;

import java.util.Optional;

@Service
public class ApplicationPersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public ApplicationPersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByUsername(username);

        if(person.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new PersonDetails(person.get());
    }
}
