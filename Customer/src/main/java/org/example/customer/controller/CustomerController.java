package org.example.customer.controller;

import jakarta.servlet.http.HttpSession;
import org.aspectj.weaver.ast.Or;
import org.example.library.dto.CustomerDto;
import org.example.library.model.*;
import org.example.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal, HttpSession session){
        if (principal == null){
            return "login";
        }
        String username = principal.getName();

        Customer customer = customerService.findByUsername(username);
        if(customer == null){
            return "login";
        }
        Long customerId = customer.getId();
        ShoppingCart shoppingCart = customer.getCart();
        if (shoppingCart != null) {
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
        } else {
            session.setAttribute("totalItems", 0);
        }
        model.addAttribute("customer", customer);

        List<Country> countries = countryService.findAll();
        model.addAttribute("countries", countries);
        List<City> cities = cityService.findAll();
        model.addAttribute("cities", cities);


        List<Order> orderList = orderService.findOrderByCustomerId(customerId);
        model.addAttribute("orderList", orderList);
        model.addAttribute("customerDto", new CustomerDto());

        return "profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute("customerDto") CustomerDto customerDto, Model model, Principal principal){

        if (principal == null){
            return "login";
        }
        customerService.updateProfile(customerDto);

        return "redirect:/profile";
    }

    @GetMapping("/orderDetail")
    public String orderDetail(Model model, @RequestParam("id") Long id) {

        List<OrderDetail> orderDetailList = orderDetailService.getOrderDetailByOrderId(id);
        model.addAttribute("orderDetailList", orderDetailList);
        if (!orderDetailList.isEmpty()) {
            model.addAttribute("order", orderDetailList.get(0).getOrder());
        } else {
            model.addAttribute("order", null);
        }
        return "orderDetail";
    }

}
