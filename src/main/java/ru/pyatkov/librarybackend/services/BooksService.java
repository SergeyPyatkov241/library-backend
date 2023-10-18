package ru.pyatkov.librarybackend.services;

import lombok.extern.slf4j.Slf4j;
import ru.pyatkov.librarybackend.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pyatkov.librarybackend.models.Person;
import ru.pyatkov.librarybackend.models.Book;
import ru.pyatkov.librarybackend.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll(boolean sortByYear) {
        log.info("BooksService.findAll entering: args {}", sortByYear);
        if (sortByYear) {
            log.info("BooksService.findAll result: sort by year");
            return booksRepository.findAll(Sort.by("year"));
        }
        else {
            log.info("BooksService.findAll result: without sort");
            return booksRepository.findAll();
        }
    }

    public Book findOne(int id) {
        log.info("BooksService.findOne entering: args {}", id);
        Optional<Book> foundBook = booksRepository.findById(id);
        if(foundBook.isPresent()) {
            Book returnBook = foundBook.get();
            log.info("BooksService.findOne result: found book - '{}'", returnBook);
            return returnBook;
        } else {
            log.error("BooksService.findOne error: book with id = '{}' doesn't exists", id);
            throw new EntityNotFoundException("Книги с таким id не существует", "BooksService");
        }
    }

    @Transactional
    public void save(Book book) {
        log.info("BooksService.save entering: args {}", book);
        booksRepository.save(book);
        log.info("BooksService.save result: сreated new book - '{}'", book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        log.info("BooksService.update entering: args {}, {}", id, updatedBook);
        Book bookOwner = findOne(id);
        updatedBook.setId(id);
        updatedBook.setOwner(bookOwner.getOwner());
        booksRepository.save(updatedBook);
        log.info("BooksService.update result: updated book - '{}'", updatedBook);
    }

    @Transactional
    public void delete(int id) {
        log.info("BooksService.delete entering: args {}", id);
        findOne(id);
        booksRepository.deleteById(id);
        log.info("BooksService.delete result: deleted book with id = '{}'", id);
    }

    public Person getBookOwner(int id) {
        return booksRepository.findById(id).map(Book::getOwner).orElseThrow(
                () -> { throw new EntityNotFoundException("Человека с таким id не существует", "BooksService");}
        );
    }

    @Transactional
    public void release(int id) {
        log.info("BookService.release entering: args {}", id);
        Book releaseBook = findOne(id);
        releaseBook.setOwner(null);
        releaseBook.setTakenAt(null);
        booksRepository.save(releaseBook);
        log.info("BookService.release result: book with id = '{}' is released", id);
    }

    @Transactional
    public void assign(int id, Person newOwner) {
        log.info("BookService.assign entering: args {}, {}", id, newOwner);
        Book updatedBook = findOne(id);
        updatedBook.setOwner(newOwner);
        updatedBook.setTakenAt(new Date());
        booksRepository.save(updatedBook);
        log.info("BookService.assign result: the book with id = '{}' is assigned to a person = '{}'", id, newOwner);
    }

    public List<Book> findAllWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        log.info("BookService.findAllWithPagination entering: args {}, {}, {}", page, booksPerPage, sortByYear);
        if(sortByYear) {
            log.info("BookService.findAllWithPagination: sort by year with page");
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        }
        else {
            log.info("BookService.findAllWithPagination: only page");
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
        }
    }

    public List<Book> findBooksByTitle(String searchQuery) {
        log.info("BookService.findBooksByTitle entering: args {}", searchQuery);
        List<Book> foundBooks = booksRepository.findByTitleStartingWith(searchQuery);
        if(!foundBooks.isEmpty()){
            log.info("BookService.findBooksByTitle result: found book(s) - {}", foundBooks);
            return foundBooks;
        } else {
            log.error("BooksService.findBooksByTitle error: no books were found for search - '{}'", searchQuery);
            throw new EntityNotFoundException("По вашему запросу книг не найдено", "BooksService");
        }
    }

}
