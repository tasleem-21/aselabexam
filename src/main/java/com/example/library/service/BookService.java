package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.exception.BookNotFoundException;
import com.example.library.exception.BookUnavailableException;
import com.example.library.exception.DuplicateBookException;
import com.example.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book addBook(Long id, String title, String author) {
        if (bookRepository.existsById(id)) {
            throw new DuplicateBookException(id);
        }
        return bookRepository.save(new Book(id, title.trim(), author.trim()));
    }

    @Transactional(readOnly = true)
    public List<Book> findBooks(String search) {
        if (search == null || search.isBlank()) {
            return bookRepository.findAll();
        }
        String term = search.trim();
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(term, term);
    }

    @Transactional
    public Book borrowBook(Long id) {
        Book book = findById(id);
        if (!book.isAvailable()) {
            throw new BookUnavailableException(id);
        }
        book.borrow();
        return book;
    }

    @Transactional
    public Book returnBook(Long id) {
        Book book = findById(id);
        book.returnBook();
        return book;
    }

    private Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }
}
