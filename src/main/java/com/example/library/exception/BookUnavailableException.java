package com.example.library.exception;

public class BookUnavailableException extends RuntimeException {

    public BookUnavailableException(Long id) {
        super("Book with id " + id + " is already borrowed");
    }
}
