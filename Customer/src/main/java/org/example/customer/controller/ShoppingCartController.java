package org.example.customer.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.library.dto.BookDto;
import org.example.library.model.Book;
import org.example.library.model.Customer;
import org.example.library.model.ShoppingCart;
import org.example.library.repository.BookRepository;
import org.example.library.service.BookService;
import org.example.library.service.CustomerService;
import org.example.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;



    @GetMapping("/cart")
    public String cart(Model model, Principal principal, HttpSession session) {

        if (principal == null){
            return "login";
        }

        Customer customer = customerService.findByUsername(principal.getName());
        ShoppingCart cart = customer.getCart();
        if (cart == null) {
            model.addAttribute("check", " You don't have any items in your cart");
            return "cart";
        }
        model.addAttribute("grandTotal", cart.getTotalPrice());
        session.setAttribute("totalItems", cart.getTotalItems());
        model.addAttribute("shoppingCart", cart);


        return "cart";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("id") Long bookId,
                            @RequestParam(value = "quantity", defaultValue = "1", required = false) int quantity,
                            Model model, Principal principal, HttpSession session) {

        try {

//            BookDto bookDto = bookService.findById(bookId);
//            shoppingCartService.addItemToCartSession(bookDto, quantity);

            Book book = bookRepository.findById(bookId).orElse(null);
            if (book == null) {
                return "redirect:/shop";
            }
            BookDto bookDto = bookService.convertToBookDto(book);
            if (principal == null){
                return "login";
            }
            String username = principal.getName();

            ShoppingCart shoppingCart = cartService.addItemToCart(bookDto, quantity, username);

            session.setAttribute("totalItems", shoppingCart.getTotalItems());
            model.addAttribute("shoppingCart", shoppingCart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/shop";
    }

    @PostMapping("/updateCart")
    public String updateCart(@RequestParam("id") Long bookId, @RequestParam("quantity") int quantity, Model model, Principal principal, HttpSession session) {

//        BookDto bookDto = bookService.findById(bookId);
//        shoppingCartService.updateCartSession(bookDto, quantity);

        if (principal == null){
            return "login";
        }
        String username = principal.getName();

        Book book = bookRepository.findById(bookId).orElse(null);
        if(book == null){
            return "redirect:/cart";
        }
        BookDto bookDto = bookService.convertToBookDto(book);
        ShoppingCart shoppingCart = cartService.updateCart(bookDto, quantity, username);

        session.setAttribute("totalItems", shoppingCart.getTotalItems());
        model.addAttribute("shoppingCart", shoppingCart);

        return "redirect:/cart";
    }

    @PostMapping("/deleteItem")
    public String deleteItem(@RequestParam("bookId") Long bookId, Model model, Principal principal, HttpSession session) {
        try {
            if (principal == null){
                return "login";
            }
            Book book = bookRepository.findById(bookId).orElse(null);
            if (book == null){
                return "redirect:/cart";
            }
            System.out.println(book.getId());

            BookDto bookDto = bookService.convertToBookDto(book);
            String username = principal.getName();
            ShoppingCart shoppingCart = cartService.removeItemFromCart(bookDto, username);

            session.setAttribute("totalItems", shoppingCart.getTotalItems());
            model.addAttribute("shoppingCart", shoppingCart);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cart";
        }

        return "cart";
    }


}
