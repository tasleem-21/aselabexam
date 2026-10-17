package com.example.library;

import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void clearDatabase() {
        bookRepository.deleteAll();
    }

    @Test
    void addsAndListsBooks() throws Exception {
        addBook(1, "Clean Code", "Robert Martin");

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Clean Code")))
                .andExpect(jsonPath("$[0].available", is(true)));
    }

    @Test
    void searchesByTitleOrAuthor() throws Exception {
        addBook(1, "Clean Code", "Robert Martin");
        addBook(2, "Effective Java", "Joshua Bloch");

        mockMvc.perform(get("/api/books").param("search", "bloch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    void borrowsAndReturnsBook() throws Exception {
        addBook(1, "Clean Code", "Robert Martin");

        mockMvc.perform(post("/api/books/1/borrow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)));
        mockMvc.perform(post("/api/books/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"title\":\"\",\"author\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void rejectsBorrowingUnavailableBook() throws Exception {
        addBook(1, "Clean Code", "Robert Martin");
        mockMvc.perform(post("/api/books/1/borrow")).andExpect(status().isOk());

        mockMvc.perform(post("/api/books/1/borrow"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    void returnsNotFoundForUnknownBook() throws Exception {
        mockMvc.perform(post("/api/books/99/borrow"))
                .andExpect(status().isNotFound());
    }

    private void addBook(long id, String title, String author) throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":%d,"title":"%s","author":"%s"}
                                """.formatted(id, title, author)))
                .andExpect(status().isCreated());
    }
}
