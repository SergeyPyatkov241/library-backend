package ru.pyatkov.librarybackend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pyatkov.librarybackend.dto.BookDTO;
import ru.pyatkov.librarybackend.dto.response.GetBookResponseDTO;
import ru.pyatkov.librarybackend.models.Book;
import ru.pyatkov.librarybackend.models.Person;
import ru.pyatkov.librarybackend.services.BooksService;
import ru.pyatkov.librarybackend.services.PeopleService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/books")
public class BooksController {

    private final BooksService booksService;

    private final PeopleService peopleService;

    private final ModelMapper modelMapper;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService, ModelMapper modelMapper) {
        this.booksService = booksService;
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity<List<BookDTO>> getBooks(@RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                            @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {
        log.info("Entering endpoint: /books");
        if(page == null || booksPerPage == null) {
            List<BookDTO> response = booksService.findAll(sortByYear).stream()
                    .map(book -> convertToDTO(book, BookDTO.class))
                    .collect(Collectors.toList());
            ResponseEntity<List<BookDTO>> responseEntity = ResponseEntity.ok(response);
            log.info("Endpoint: /books return result without pagination");
            return responseEntity;
        } else {
            List<BookDTO> response = booksService.findAllWithPagination(page, booksPerPage, sortByYear).stream()
                    .map(book -> convertToDTO(book, BookDTO.class))
                    .collect(Collectors.toList());
            ResponseEntity<List<BookDTO>> responseEntity = ResponseEntity.ok(response);
            log.info("Endpoint: /books return result with pagination");
            return responseEntity;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetBookResponseDTO> getBook(@PathVariable("id") int id) {
        log.info("Entering endpoint: /books/{}", id);
        GetBookResponseDTO response = convertToDTO(booksService.findOne(id), GetBookResponseDTO.class);
        ResponseEntity<GetBookResponseDTO> responseEntity = ResponseEntity.ok(response);
        log.info("Endpoint: /books/{} return result - {}", id, responseEntity);
        return responseEntity;
    }

    @PostMapping()
    public ResponseEntity<BookDTO> createBook(@RequestBody @Valid BookDTO bookDTO) {
        log.info("Entering endpoint: /books request - {}", bookDTO);
        booksService.save(convertToBook(bookDTO));
        ResponseEntity<BookDTO> responseEntity = ResponseEntity.ok(bookDTO);
        log.info("Endpoint: /books return result - {}", responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@RequestBody @Valid BookDTO bookDTO, @PathVariable("id") int id) {
        log.info("Entering endpoint: /books/{}  request - {}", id, bookDTO);
        booksService.update(id, convertToBook(bookDTO));
        ResponseEntity<BookDTO> responseEntity = ResponseEntity.ok(bookDTO);
        log.info("Endpoint: /books/{} return result - {}", id, responseEntity);
        return responseEntity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") int id) {
        log.info("Entering endpoint: /books/{}", id);
        booksService.delete(id);
        ResponseEntity<?> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        log.info("Endpoint: /books/{} return result - {}", id, responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<GetBookResponseDTO> assignBook(@PathVariable("id") int id, @RequestParam("setOwnerId") int ownerId) {
        log.info("Entering endpoint: /books/{}/assign?setOwnerId={}", id, ownerId);
        Person newOwner = peopleService.findOne(ownerId);
        booksService.assign(id, newOwner);
        GetBookResponseDTO response = convertToDTO(booksService.findOne(id), GetBookResponseDTO.class);
        ResponseEntity<GetBookResponseDTO> responseEntity = ResponseEntity.ok(response);
        log.info("Endpoint: /books/{}/assign?setOwnerId={} return result - {}", id, ownerId, responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<GetBookResponseDTO> releaseBook(@PathVariable("id") int id) {
        log.info("Entering endpoint: /books/{}/release", id);
        booksService.release(id);
        GetBookResponseDTO response = convertToDTO(booksService.findOne(id), GetBookResponseDTO.class);
        ResponseEntity<GetBookResponseDTO> responseEntity = ResponseEntity.ok(response);
        log.info("Endpoint: /books/{}/release return result - {}", id, responseEntity);
        return responseEntity;
    }

    @PostMapping("/search")
    public ResponseEntity<List<BookDTO>> startSearchBook(@RequestParam("query") String searchQuery) {
        log.info("Entering endpoint: /books/search?query={}", searchQuery);
        List<BookDTO> response = booksService.findBooksByTitle(searchQuery).stream()
                .map(book -> convertToDTO(book, BookDTO.class))
                .collect(Collectors.toList());
        ResponseEntity<List<BookDTO>> responseEntity = ResponseEntity.ok(response);
        log.info("Endpoint: /books/search return result");
        return responseEntity;
    }

    private Book convertToBook(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }

    private <T> T convertToDTO(Book book, Class<T> targetClass) {
        return modelMapper.map(book, targetClass);
    }

}
