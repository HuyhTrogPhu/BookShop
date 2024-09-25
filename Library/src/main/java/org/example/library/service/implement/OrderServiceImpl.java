package org.example.library.service.implement;


import org.example.library.dto.OrderDto;
import org.example.library.model.*;
import org.example.library.repository.BookRepository;
import org.example.library.repository.CustomerRepository;
import org.example.library.repository.OrderDetailRepository;
import org.example.library.repository.OrderRepository;
import org.example.library.service.OrderService;
import org.example.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BookRepository bookRepository;


    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findByUser(String username) {
        Customer customer = customerRepository.findByUsername(username);
        List<Order> orderList = customer.getOrders();
        return orderList;
    }

    @Override
    public Order save(ShoppingCart shoppingCart, OrderDto orderDto) {
        Order order = new Order();
        order.setAccept(false);
        order.setOrderDate(new Date());
        order.setOrderStatus(orderDto.getOrderStatus());
        order.setPaymentMethod(orderDto.getPaymentMethod());
        order.setQuantity(shoppingCart.getTotalItems());
        order.setTax(2);
        order.setTotalPrice(shoppingCart.getTotalPrice());
        order.setCustomer(shoppingCart.getCustomer());

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (CartItem item : shoppingCart.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setBook(item.getBook());
            orderDetail.setQuantity(item.getQuantity());
            orderDetailRepository.save(orderDetail);
            orderDetailList.add(orderDetail);

            Book book = bookRepository.findById(item.getBook().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + item.getBook().getId()));
            int updatedQuantity = book.getCurrentQuantity() - item.getQuantity();
            book.setCurrentQuantity(updatedQuantity);
            bookRepository.save(book);
        }

        order.setOrderDetailList(orderDetailList);
        shoppingCartService.deleteCartById(shoppingCart.getId());
        return orderRepository.save(order);

    }

    @Override
    public Order updateOrder(Long id, boolean is_accept, Date date) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setAccept(is_accept);
            order.setDeliveryDate(date);
        } else {
            throw new IllegalArgumentException("Order not found with id: " + id);
        }
        return orderRepository.save(order);
    }


    @Override
    public Page<Order> findListOrder(Pageable pageable) {
        return orderRepository.getOrders(pageable);
    }

    @Override
    public Page<Order> findOrdersToday(Pageable pageable) {
        return orderRepository.findOrdersToDate(pageable);
    }

    @Override
    public List<Order> findOrdersToDate() {
        return orderRepository.findOrdersToDate();
    }

    @Override
    public Page<Order> findAllOrder(Pageable pageable) {
        return orderRepository.findAllOrder(pageable);
    }

    @Override
    public Page<Order> findOrderBetweenDate(java.sql.Date startDate, java.sql.Date endDate, Pageable pageable) {
        return orderRepository.findOrderBetweenDate(startDate, endDate, pageable);
    }

    @Override
    public List<Order> listOrderBetweenDate(java.sql.Date startDate, java.sql.Date endDate) {
        return orderRepository.listOrderBetweenDate(startDate, endDate);
    }

    @Override
    public Page<Order> searchOrdersToDate(java.sql.Date today, Pageable pageable) {
        return orderRepository.searchOrdersToDate(today, pageable);
    }

    @Override
    public List<Order> searchOrdersToDate(java.sql.Date today) {
        return orderRepository.searchOrdersToDate(today);
    }

    @Override
    public List<Order> findOrderByCustomerId(Long customerId) {
        return orderRepository.findOrderByCustomerId(customerId);
    }
}
