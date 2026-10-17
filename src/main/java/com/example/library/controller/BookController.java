package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.addBook(request.id(), request.title(), request.author());
    }

    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String search) {
        return bookService.findBooks(search);
    }

    @PostMapping("/{id}/borrow")
    public Book borrowBook(@PathVariable @NotNull @Positive Long id) {
        return bookService.borrowBook(id);
    }

    @PostMapping("/{id}/return")
    public Book returnBook(@PathVariable @NotNull @Positive Long id) {
        return bookService.returnBook(id);
    }

    public record CreateBookRequest(
            @NotNull(message = "id is required") @Positive(message = "id must be positive") Long id,
            @NotBlank(message = "title is required") String title,
            @NotBlank(message = "author is required") String author) {
    }
}
