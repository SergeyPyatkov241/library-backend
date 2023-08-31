package ru.pyatkov.librarybackend.exceptions;

import lombok.Getter;

public abstract class EntityException extends RuntimeException {

    @Getter
    private final String className;

    public EntityException(String message, String className) {
        super(message);
        this.className = className;
    }

}
