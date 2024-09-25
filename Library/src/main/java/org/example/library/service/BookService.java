package org.example.library.service;

import org.example.library.dto.BookDto;
import org.example.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {

    List<Book> findAllBooks();

    Book findBookById(Long id);

    Book addBook(MultipartFile images,BookDto bookDto);

    Book update(MultipartFile images, BookDto bookDto);

    BookDto findById(Long id);

    BookDto convertToBookDto(Book book);

    void deleteBookById(Long id);

    List<String> findDistinctAuthor();

    Page<Book> findByAuthor(String author, Pageable pageable);

    Page<Book> findByCategoryName(String categoryName, Pageable pageable);

    Page<Book> pageBooks(Pageable pageable);

    Page<Book> searchBooks(String keyword, Pageable pageable);

    List<Book> top6Book();

//    Page<Book> searchBook(String keyword, Pageable pageable);
}
