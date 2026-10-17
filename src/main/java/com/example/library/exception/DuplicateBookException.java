package com.example.library.exception;

public class DuplicateBookException extends RuntimeException {

    public DuplicateBookException(Long id) {
        super("A book with id " + id + " already exists");
    }
}
