package com.javarush.domain.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(long id) {
        super("Entity not found with id %d".formatted(id));
    }
}
