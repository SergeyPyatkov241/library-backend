package ru.pyatkov.librarybackend.util;

public class PersonNotUpdatedException extends RuntimeException {

    public PersonNotUpdatedException(String msg) {
        super(msg);
    }

}
