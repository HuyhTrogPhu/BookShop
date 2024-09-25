package org.example.admin.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.LifecycleState;
import org.aspectj.weaver.ast.Or;
import org.example.library.model.Customer;
import org.example.library.model.Order;
import org.example.library.repository.CustomerRepository;
import org.example.library.service.CustomerService;
import org.example.library.service.OrderService;
import org.example.library.utils.CustomerExelGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    private static final int pageSize = 10;


    @GetMapping("/list-customer")
    public String listCustomer(Model model, @RequestParam(defaultValue = "0") int page, Principal principal) {

        if (principal == null){
            return "login";
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Objects[]> customerPage = customerService.getAllCustomers(pageable);

        model.addAttribute("customerList", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("size", customerPage.getSize());

        return "list-customer";
    }

    @GetMapping("/statistical-customer")
    public String statisticalCustomer(Model model, @RequestParam(defaultValue = "0") int page, Principal principal) {

        if(principal == null){
            return "login";
        }

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Objects[]> customerPage = customerService.getAllCustomers(pageable);

            model.addAttribute("customerList", customerPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("size", customerPage.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }

        return "statisticalCustomer";
    }

    @PostMapping("/default")
    public String getDefault(Model model, @RequestParam(defaultValue = "0") int page) {
        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Objects[]> customerPage = customerService.getAllCustomers(pageable);

            model.addAttribute("customerList", customerPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("size", customerPage.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/statistical-customer";
        }

        return "statisticalCustomer";

    }

    @PostMapping("/buyTheMost")
    public String buyTheMost(Model model, @RequestParam(defaultValue = "0") int page) {

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Objects[]> customerPage = customerService.getAllCustomerBuyTheMost(pageable);

            model.addAttribute("customerList", customerPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("size", customerPage.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/statistical-customer";
        }

        return "statisticalCustomer";
    }

    @PostMapping("/buyTheLeast")
    public String buyTheLeast(Model model, @RequestParam(defaultValue = "0") int page) {
        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Objects[]> customerPage = customerService.getAllCustomerBuyTheLeast(pageable);

            model.addAttribute("customerList", customerPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("size", customerPage.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/statistical-customer";
        }

        return "statisticalCustomer";

    }

    @PostMapping("/notBuy")
    public String notBuy(Model model, @RequestParam(defaultValue = "0") int page) {
        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Object[]> customerPage = customerService.getAllCustomerNotBuy(pageable);

            model.addAttribute("customerList", customerPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("size", customerPage.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/statistical-customer";
        }

        return "statisticalCustomer";
    }


    //    export to excel
    @GetMapping("/export-to-excel-buyTheMost")
    public void exportToExcelBuyTheMost(HttpServletResponse response) {

        try {
            response.setContentType("application/octet-stream");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=customer_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);

            List<Object[]> customerList = customerService.listCustomerIsOrder();
            CustomerExelGenerator generator = new CustomerExelGenerator(customerList);
            generator.generateExcelFile(response);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    //    Detail customer
    @GetMapping("/detailCustomer")
    public String detailCustomer(Model model, @RequestParam(defaultValue = "0") int page,
                                 @RequestParam("customerId") Long customerId) {

        Customer customer = customerService.findById(customerId);
        if (customer == null) {
            return "redirect:/list-customer";
        }
        model.addAttribute("customer", customer);

        List<Order> orderList = orderService.findOrderByCustomerId(customerId);
        if (orderList.isEmpty()) {
            model.addAttribute("error", "Order not found");
            return "redirect:/list-customer";
        }
        model.addAttribute("orderList", orderList);

        return "customerDetail";
    }

//    Search customer by keyword

    @PostMapping("/searchCustomer")
    public String searchCustomer(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page,
                                 Model model, HttpSession session) {

        if (keyword.isEmpty()) {
            keyword = (String) session.getAttribute("keyword");
        } else {
            session.setAttribute("keyword", keyword);
        }
        if (keyword.isEmpty()) {
            return "redirect:/list-customer";
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Customer> customerPage = customerService.searchCustomerByKeyword(keyword, pageable);

        model.addAttribute("customerList", customerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("size", customerPage.getSize());
        model.addAttribute("keyword", keyword);

        return "list-customer";
    }

    @GetMapping("searchCustomer")
    public String searchCustomerGet(@RequestParam("keyword") String keyword, @RequestParam(defaultValue = "0") int page,
                                    Model model, HttpSession session) {
        return searchCustomer(keyword, page, model, session);
    }

}
