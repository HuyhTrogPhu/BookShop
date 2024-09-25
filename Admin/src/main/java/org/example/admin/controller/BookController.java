package org.example.admin.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.library.dto.BookDto;
import org.example.library.model.Book;
import org.example.library.model.Caterogy;
import org.example.library.repository.BookRepository;
import org.example.library.repository.CategoryRepository;
import org.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.exceptions.TemplateAssertionException;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final int pageSize = 5;

    @GetMapping("/books")
    public String listBook(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<Book> listBook = bookRepository.findAll();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Book> bookPage = bookRepository.findAllPage(pageable);
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("size", listBook.size());
        model.addAttribute("title", "Book");

        return "list-book";
    }

    @GetMapping("/manager-books")
    public String managerBook(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            List<Book> listBook = bookRepository.findAll();
            List<Caterogy> listCategory = categoryRepository.findAll();
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Book> bookPage = bookRepository.findAllPage(pageable);
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("categories", listCategory);
            model.addAttribute("size", listBook.size());
            model.addAttribute("bookDto", new BookDto());
        } catch (TemplateAssertionException e) {
            e.printStackTrace();
        }
        return "manager-book";
    }

    @PostMapping("/addBook")
    public String addBook(@Valid @ModelAttribute("bookDto") BookDto bookDto, @RequestParam("imageBook") MultipartFile imageBook,
                          Model model, Principal principal, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        try {

            if (bindingResult.hasErrors()) {
                return "redirect:/manager-books";
            }
            bookService.addBook(imageBook, bookDto);
            System.out.println(bookDto);
            redirectAttributes.addFlashAttribute("success", "Add new book successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed add!");
        }
        return "redirect:/manager-books";
    }

    @GetMapping("/editBook")
    public String editBook(@RequestParam("bookId") Long id, Model model, Principal principal) {

        try {
            Book book = bookRepository.findById(id).get();
            model.addAttribute("book", book);

            if(principal == null){
                return "redirect:/manager-books";
            }


            BookDto bookDto = new BookDto();
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setCategory(book.getCategory());
            bookDto.setDescription(book.getDescription());
            bookDto.setCostPrice(book.getCostPrice());
            bookDto.setCurrentQuantity(book.getCurrentQuantity());
            bookDto.setImage(book.getImage());

            model.addAttribute("bookDto", bookDto);
            model.addAttribute("categories", categoryRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manager-books";
        }


        return "editBook";
    }

    @PostMapping("/updateBook")
    public String updateBook(@Valid @ModelAttribute("bookDto") BookDto bookDto, @RequestParam Long id,
                             @RequestParam("imageBook") MultipartFile imageBook, Principal principal,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        try {
            Book book = bookRepository.findById(id).get();
            model.addAttribute("book", book);


            if (bindingResult.hasErrors()) {
                return "redirect:/editBook";
            }

            bookService.update(imageBook, bookDto);
            redirectAttributes.addFlashAttribute("success", "Book updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed update!");
            return "redirect:/manager-books";
        }
        return "redirect:/manager-books";
    }


    @GetMapping("/removeBook")
    public String removeBook(@RequestParam("bookId") Long bookId, RedirectAttributes redirectAttributes) {
        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + bookId));

            Caterogy category = book.getCategory();

            List<Book> booksInCategory = bookRepository.findByCategory(category);
            if (booksInCategory.size() > 1) {
                bookRepository.delete(book);
                redirectAttributes.addFlashAttribute("success", "Book removed successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Cannot delete book because it's the last book in the category.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to remove book.");
        }
        return "redirect:/manager-books";
    }

    //    search book by keyword
    @PostMapping("/searchByKeyword")
    public String searchByKeyword(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page, Model model,
                                  HttpSession session) {
        try {
            if (keyword.isEmpty()) {
                keyword = (String) session.getAttribute("keyword");
            } else {
                session.setAttribute("keyword", keyword);
            }

            if (keyword.isEmpty()) {
                return "redirect:/books";
            }
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Book> bookPage = bookService.searchBooks(keyword, pageable);
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("keyword", keyword);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "list-book";
    }

    @GetMapping("/searchByKeyword")
    public String searchByKeywordGet(@RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(defaultValue = "0") int page, Model model,
                                     HttpSession session) {
        return searchByKeyword(keyword, page, model, session);
    }

    //    search book by keyword in manager
    @PostMapping("/searchByKeywordInManager")
    public String searchByKeywordInManager(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page, Model model,
                                  HttpSession session) {
        try {
            if (keyword.isEmpty()) {
                keyword = (String) session.getAttribute("keyword");
            } else {
                session.setAttribute("keyword", keyword);
            }

            if (keyword.isEmpty()) {
                return "redirect:/manager-books";
            }
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Book> bookPage = bookService.searchBooks(keyword, pageable);
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("size", bookPage.getSize());
            model.addAttribute("keyword", keyword);
            model.addAttribute("bookDto", new BookDto());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "manager-book";
    }

    @GetMapping("/searchByKeywordInManager")
    public String searchByKeywordInManagerGet(@RequestParam(value = "keyword", required = false) String keyword,
                                              @RequestParam(defaultValue = "0") int page, Model model,
                                              HttpSession session) {
        return searchByKeywordInManager(keyword, page, model, session);
    }
}
