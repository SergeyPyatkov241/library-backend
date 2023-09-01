package ru.pyatkov.librarybackend.controllers;

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

@RestController
@RequestMapping("/books")
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
    public List<BookDTO> getBooks(@RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                            @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {
        if(page == null || booksPerPage == null) {
            return booksService.findAll(sortByYear).stream()
                    .map(book -> convertToDTO(book, BookDTO.class))
                    .collect(Collectors.toList());
        } else {
            return booksService.findAllWithPagination(page, booksPerPage, sortByYear).stream()
                    .map(book -> convertToDTO(book, BookDTO.class))
                    .collect(Collectors.toList());
        }
    }

    @GetMapping("/{id}")
    public GetBookResponseDTO getBook(@PathVariable("id") int id) {
        return convertToDTO(booksService.findOne(id), GetBookResponseDTO.class);
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid BookDTO bookDTO) {
        booksService.save(convertToBook(bookDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid BookDTO bookDTO, @PathVariable("id") int id) {
        booksService.update(id, convertToBook(bookDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<HttpStatus> release(@PathVariable("id") int id) {
        booksService.release(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<HttpStatus> assign(@PathVariable("id") int id, @RequestParam("setOwnerId") int ownerId) {
        Person newOwner = peopleService.findOne(ownerId);
        booksService.assign(id, newOwner);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/search")
    public List<BookDTO> startSearch(@RequestParam("query") String searchQuery) {
        return booksService.findBooksByTitle(searchQuery).stream()
                .map(book -> convertToDTO(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    private Book convertToBook(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }

    private <T> T convertToDTO(Book book, Class<T> targetClass) {
        return modelMapper.map(book, targetClass);
    }

}
