package org.example.customer.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.library.dto.CustomerDto;
import org.example.library.model.Customer;
import org.example.library.model.ShoppingCart;
import org.example.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.security.Principal;

@Controller
public class LoginController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        model.addAttribute("page", "Home");
        model.addAttribute("customerDto", new CustomerDto());
        return "login";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("customerDto", new CustomerDto());
        return "register";
    }

    @PostMapping("/do-register")
    public String registerCustomer(Model model, @Valid @ModelAttribute("customerDto") CustomerDto customerDto,
                                   BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("customerDto", customerDto);
                bindingResult.toString();
                return "register";
            }

            String username = customerDto.getUsername();
            Customer customer = customerService.findByUsername(username);
            if (customer != null) {
                model.addAttribute("customerDto", customerDto);
                model.addAttribute("error", "Email has been register!");

            }
            if (customerDto.getPassword().equals(customerDto.getConfirmPassword())) {
                customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
                customerService.save(customerDto);
                model.addAttribute("success", "Register successfully!");
                System.out.println(customerDto);
                return "register";
            } else {
                model.addAttribute("error", "Password is incorrect!");
                model.addAttribute("customerDto", customerDto);
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Server is error, please try again!");
        }

        return "register";
    }
}
