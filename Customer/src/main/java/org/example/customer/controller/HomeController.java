package org.example.customer.controller;


import jakarta.servlet.http.HttpSession;
import org.example.customer.CustomerApplication;
import org.example.customer.config.CustomerConfiguration;
import org.example.library.model.Book;
import org.example.library.model.Caterogy;
import org.example.library.model.Customer;
import org.example.library.model.ShoppingCart;
import org.example.library.repository.BookRepository;
import org.example.library.repository.CategoryRepository;
import org.example.library.repository.CustomerRepository;
import org.example.library.service.BookService;
import org.example.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private static final int pageSize = 8;


    //    Just arrived
    @GetMapping({"/", "/index"})
    public String home(@RequestParam(defaultValue = "0") int page, Model model, Principal principal, HttpSession session) {

        List<Caterogy> categoryList = categoryRepository.findAll();
        model.addAttribute("categoryList", categoryList);

        List<Book> topBook = bookService.top6Book();
        model.addAttribute("topBook", topBook);

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Book> bookPage = bookRepository.findAllPage(pageable);
        model.addAttribute("bookList", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());

        if (principal != null) {
            Customer customer = customerService.findByUsername(principal.getName());
            if (customer != null) {
                session.setAttribute("username", customer.getFirstName() + " " + customer.getLastName());
                ShoppingCart shoppingCart = customer.getCart();
                if (shoppingCart != null) {
                    session.setAttribute("totalItems", shoppingCart.getTotalItems());
                } else {
                    session.setAttribute("totalItems", 0);
                }
            }
        }
        return "index";
    }


    //    Find by category name
    @PostMapping("/findByCategoryNameFromHome")
    public String findByCategoryName(@RequestParam(value = "categoryName", required = false) String categoryName,
                                     @RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
        try {
            if (categoryName.isEmpty()) {
                categoryName = (String) session.getAttribute("categoryName");
            } else {
                session.setAttribute("categoryName", categoryName);
            }
            if (categoryName.isEmpty()) {
                return "redirect:/index";
            }
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Book> bookPage = bookService.findByCategoryName(categoryName, pageable);

//            if ( bookPage.isEmpty()) {
//                model.addAttribute("message", "Book not found!");
//            }
            model.addAttribute("bookList", bookPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", bookPage.getTotalPages());
            model.addAttribute("categoryName", categoryName);

            List<Caterogy> categoryList = categoryRepository.findAll();
            model.addAttribute("categoryList", categoryList);

            List<String> authorBook = bookRepository.findDistinctAuthor();
            model.addAttribute("authorBook", authorBook);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "shop";
    }

}

