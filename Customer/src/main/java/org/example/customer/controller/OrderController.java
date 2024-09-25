package org.example.customer.controller;


import jakarta.servlet.http.HttpSession;
import org.example.library.dto.CartItemDto;
import org.example.library.dto.CustomerDto;
import org.example.library.dto.OrderDto;
import org.example.library.dto.OrderItemDto;
import org.example.library.model.*;
import org.example.library.repository.CartItemRepository;
import org.example.library.service.*;
import org.example.library.service.implement.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.w3c.dom.stylesheets.LinkStyle;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
public class OrderController {

    @Autowired
    private CityService cityService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BookService bookService;

    @Autowired
    private OrderService orderService;


    @GetMapping("/checkout")
    public String checkOut(Principal principal, Model model, HttpSession session) {


        Customer customer = customerService.findByUsername(principal.getName());
        ShoppingCart cart = customer.getCart();
        ShoppingCart shoppingCart = customer.getCart();
        if (shoppingCart != null) {
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
        } else {
            session.setAttribute("totalItems", 0);
        }

        model.addAttribute("firstName", customer.getFirstName());
        model.addAttribute("lastName", customer.getLastName());
        model.addAttribute("Email", customer.getUsername());
        model.addAttribute("orderDto", new OrderDto());
        model.addAttribute("customerDto", new CustomerDto());

        model.addAttribute("grandTotal", cart.getTotalPrice());
        model.addAttribute("shoppingCart", cart);


//      List<City> citesCountry = cityService.findAllCitesCountry();
//      model.addAttribute("citesCountry", citesCountry);
        List<Country> countries = countryService.findAll();
        model.addAttribute("countries", countries);
        List<City> cities = cityService.findAll();
        model.addAttribute("cities", cities);


        return "checkout";
    }

    @PostMapping("/placeOrder")
    public String placeOrder(@ModelAttribute("orderDto") OrderDto orderDto,
                             @ModelAttribute("customerDto") CustomerDto customerDto,
                             Principal principal, Model model, HttpSession session) {
        if (principal != null) {
            Customer customer = customerService.findByUsername(principal.getName());
            customerDto.setUsername(customer.getUsername());
            customerService.updateCheckOut(customerDto);

            ShoppingCart cart = customer.getCart();
            orderService.save(cart, orderDto);

            ShoppingCart shoppingCart = customer.getCart();
            if (shoppingCart != null) {
                session.setAttribute("totalItems", shoppingCart.getTotalItems());
            } else {
                session.setAttribute("totalItems", 0);
            }
        }


        return "thankyou";
    }



}
