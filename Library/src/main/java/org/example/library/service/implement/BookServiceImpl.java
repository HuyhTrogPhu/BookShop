package org.example.library.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.library.dto.BookDto;
import org.example.library.dto.CartItemDto;
import org.example.library.dto.ShoppingCartDto;
import org.example.library.model.Book;
import org.example.library.model.CartItem;
import org.example.library.model.Caterogy;
import org.example.library.repository.BookRepository;
import org.example.library.repository.CategoryRepository;
import org.example.library.service.BookService;
import org.example.library.utils.ImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;


    @Autowired
    private CategoryRepository categoryRepository;

    private final ImageUpload imageUpload;


    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book findBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        Book bookFound = null;
        if (book.isPresent()) {
            bookFound = book.get();
        } else {
            throw new RuntimeException("Book not found");
        }
        return bookFound;
    }

    @Override
    public Book addBook(MultipartFile images, BookDto bookDto) {
        try {
            Book book = new Book();
            if (images == null) {
                book.setImage(null);
            } else {
                imageUpload.uploadFile(images);
                book.setImage(Base64.getEncoder().encodeToString(images.getBytes()));
            }
            book.setTitle(bookDto.getTitle());
            book.setAuthor(bookDto.getAuthor());
            book.setCategory(getManagedCategory(bookDto.getCategory().getId()));
            book.setDescription(bookDto.getDescription());
            book.setCostPrice(bookDto.getCostPrice());
            book.setCurrentQuantity(bookDto.getCurrentQuantity());
            book.set_delete(false);
            book.set_active(true);
            return bookRepository.save(book);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Book update(MultipartFile images, BookDto bookDto) {
        try {
            Book book = bookRepository.getReferenceById(bookDto.getId());
            if (images.getBytes().length > 0) {
                if (imageUpload.checkExist(images)) {
                    book.setImage(book.getImage());
                } else {
                    imageUpload.uploadFile(images);
                    book.setImage(Base64.getEncoder().encodeToString(images.getBytes()));
                }
            }
            book.setId(book.getId());
            book.setTitle(bookDto.getTitle());
            book.setAuthor(bookDto.getAuthor());
            book.setCategory(getManagedCategory(bookDto.getCategory().getId()));
            book.setDescription(bookDto.getDescription());
            book.setCostPrice(bookDto.getCostPrice());
            book.setCurrentQuantity(bookDto.getCurrentQuantity());
            return bookRepository.save(book);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public BookDto findById(Long id) {
        BookDto bookDto = new BookDto();
        Book book = bookRepository.getById(id);
        if (book == null) {
            return null;
        }
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setDescription(book.getDescription());
        bookDto.setCostPrice(book.getCostPrice());
        bookDto.setSalePrice(book.getSalePrice());
        bookDto.setCurrentQuantity(book.getCurrentQuantity());
        bookDto.setCategory(book.getCategory());
        bookDto.setImage(book.getImage());

        return bookDto;

    }

    @Override
    public BookDto convertToBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setDescription(book.getDescription());
        bookDto.setCostPrice(book.getCostPrice());
        bookDto.setSalePrice(book.getSalePrice());
        bookDto.setCurrentQuantity(book.getCurrentQuantity());
        bookDto.setCategory(book.getCategory());
        bookDto.setImage(book.getImage());
        bookDto.set_active(book.is_active());
        bookDto.set_delete(book.is_delete());
        bookDto.setAuthor(book.getAuthor());
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<String> findDistinctAuthor() {
        return bookRepository.findDistinctAuthor();
    }

    @Override
    public Page<Book> findByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthor(author, pageable);
    }

    @Override
    public Page<Book> findByCategoryName(String categoryName, Pageable pageable) {
        return bookRepository.findByCategoryName(categoryName, pageable);
    }


    @Override
    public Page<Book> pageBooks(Pageable pageable) {
        return bookRepository.findAllPage(pageable);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.findByKeyword(keyword, pageable);
    }

    @Override
    public List<Book> top6Book() {
        return bookRepository.findTop6ByOrderByCurrentQuantityAsc();
    }

    private Caterogy getManagedCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category Id: " + categoryId));
    }
}
