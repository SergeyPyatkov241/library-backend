package ru.pyatkov.librarybackend.services;

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

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(Sort.by("year"));
        else
            return booksRepository.findAll();
    }

    public Book findOne(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        return foundBook.orElseThrow(() -> { throw new EntityNotFoundException("Книги с таким id не существует", "BooksService");});
    }

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        Book bookOwner = findOne(id);
        updatedBook.setId(id);
        updatedBook.setOwner(bookOwner.getOwner());
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        findOne(id);
        booksRepository.deleteById(id);
    }

    public Person getBookOwner(int id) {
        return booksRepository.findById(id).map(Book::getOwner).orElseThrow(
                () -> { throw new EntityNotFoundException("Человека с таким id не существует", "BooksService");}
        );
    }

    @Transactional
    public void release(int id) {
        Book releaseBook = findOne(id);
        releaseBook.setOwner(null);
        releaseBook.setTakenAt(null);
        booksRepository.save(releaseBook);
    }

    @Transactional
    public void assign(int id, Person newOwner) {
        Book updatedBook = findOne(id);
        updatedBook.setOwner(newOwner);
        updatedBook.setTakenAt(new Date());
        booksRepository.save(updatedBook);
    }

    public List<Book> findAllWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if(sortByYear)
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public List<Book> findBooksByTitle(String searchQuery) {
        List<Book> foundBooks = booksRepository.findByTitleStartingWith(searchQuery);
        if(!foundBooks.isEmpty()){
            return foundBooks;
        } else {
            throw new EntityNotFoundException("По вашему запросу книг не найдено", "BooksService");
        }
    }

}
