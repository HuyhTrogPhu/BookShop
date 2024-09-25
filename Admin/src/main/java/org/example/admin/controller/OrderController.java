package org.example.admin.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.aspectj.weaver.ast.Or;
import org.example.library.model.Order;
import org.example.library.model.OrderDetail;
import org.example.library.service.OrderDetailService;
import org.example.library.service.OrderService;
import org.example.library.utils.OrderExcelGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    private static final int pageSize = 7;


    @GetMapping("/list-order")
    public String listOrder(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {

        if(principal == null){
            return "login";
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Order> orderPage = orderService.findOrdersToday(pageable);
        model.addAttribute("orderList", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("size", orderPage.getSize());

        java.util.Date date = new java.util.Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(date);
        model.addAttribute("today","Today's bill: " + today);

        return "list-order";
    }

//    export excel today
    @GetMapping("/export-excel-order-today")
    public void exportExcelToday(HttpServletResponse response){

        try {
            response.setContentType("application/octet-stream");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormat.format(new java.util.Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=order_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);

            List<Order> orderList = orderService.findOrdersToDate();
            OrderExcelGenerator generator = new OrderExcelGenerator(orderList);
            generator.generateExcelFile(response);
        } catch (IOException e) {
            e.printStackTrace();

        }

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

    @PostMapping("/updateOrder/{id}")
    public String updateOrder(Model model, @PathVariable("id") Long orderId,
                              @RequestParam("orderStatus") String orderStatus) {

        java.util.Date date = new java.util.Date();
        Date deliveryDate = new Date(date.getTime());
        boolean isPaid = "paid".equalsIgnoreCase(orderStatus);
        Order order = orderService.updateOrder(orderId, isPaid, deliveryDate);
        model.addAttribute("order", order);

        return "redirect:/orderDetail?id=" + orderId;
    }

    @GetMapping("/statistical-order")
    public String statisticalOrder(Model model, @RequestParam(defaultValue = "0") int page, Principal principal) {

        if(principal == null){
            return "login";
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Order> orderPage = orderService.findAllOrder(pageable);
        model.addAttribute("orderList", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("size", orderPage.getSize());

        return "statisticalOrder";
    }

    @PostMapping("/findBetweenDate")
    public String findBetweenDate(Model model,
                                  @RequestParam("startDate") String startDateStr,
                                  @RequestParam("endDate") String endDateStr,
                                  @RequestParam(defaultValue = "0") int page,
                                  HttpSession session) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date startDateUtil = dateFormat.parse(startDateStr);
            java.util.Date endDateUtil = dateFormat.parse(endDateStr);

            Date startDate = new Date(startDateUtil.getTime());
            Date endDate = new Date(endDateUtil.getTime());

            session.setAttribute("startDate", startDate);
            session.setAttribute("endDate", endDate);

            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Order> orderPage = orderService.findOrderBetweenDate(startDate, endDate, pageable);
            model.addAttribute("orderList", orderPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("size", orderPage.getSize());
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "statisticalOrder";
        } catch (ParseException e) {
            e.printStackTrace();
            return "redirect:/statistical-order";
        }
    }


    //    export excel order statistical
    @GetMapping("/export-to-excel-order")
    public void exportToExcelOrder(HttpServletResponse response,
                                   @RequestParam("startDate") Date startDate,
                                   @RequestParam("endDate") Date endDate,
                                   HttpSession session) {

        try {
            response.setContentType("application/octet-stream");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormat.format(new java.util.Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=order_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);

            startDate = (Date) session.getAttribute("startDate");
            endDate = (Date) session.getAttribute("endDate");

            session.setAttribute("startDate", startDate);
            session.setAttribute("endDate", endDate);

//            export excel
            List<Order> orderList = orderService.listOrderBetweenDate(startDate, endDate);
            OrderExcelGenerator generator = new OrderExcelGenerator(orderList);
            generator.generateExcelFile(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    search order today
@PostMapping("/findOrderToday")
public String searchOrderToday(@RequestParam("searchDate") Date searchDate,
                               @RequestParam(defaultValue = "0") int page, Model model, HttpSession session) {
    try {
        if (searchDate == null) {
            searchDate = (Date) session.getAttribute("today");
        } else {
            session.setAttribute("today", searchDate);
        }

        if (searchDate == null) {
            model.addAttribute("size", 0);
            return "redirect:/list-order";
        } else {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Order> orderPage = orderService.searchOrdersToDate(searchDate, pageable);

            model.addAttribute("orderList", orderPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("size", orderPage.getSize());
            model.addAttribute("searchDate", searchDate);
            model.addAttribute("today", "Today's bill: " + searchDate);
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/list-order";
    }

    return "list-order";
}

    @GetMapping("/findOrderToday")
    public String searchOrderTodayGet(@RequestParam("searchDate") Date searchDate,
                                      @RequestParam(defaultValue = "0") int page, Model model, HttpSession session){
        return searchOrderToday(searchDate, page, model, session);
    }


    //    export excel search order today
    @GetMapping("/export-excel-date")
    public String exportSearchOrder(HttpServletResponse response, @RequestParam("searchDate") Date searchDate,
                                  HttpSession session) {
        try {
            if (searchDate == null) {
                searchDate = (Date) session.getAttribute("today");

            } else {
                session.setAttribute("searchDate", searchDate);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateTime = dateFormat.format(new java.util.Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=order_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);

            List<Order> orderList = orderService.searchOrdersToDate(searchDate);
            OrderExcelGenerator generator = new OrderExcelGenerator(orderList);
            generator.generateExcelFile(response);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/export-excel-date";
        }

        return "list-order";
    }



}


