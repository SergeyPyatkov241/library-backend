package ru.pyatkov.librarybackend.exceptions;

public class EntityNotFoundException extends EntityException {

    public EntityNotFoundException(String message, String className) {super(message, className);}

}
