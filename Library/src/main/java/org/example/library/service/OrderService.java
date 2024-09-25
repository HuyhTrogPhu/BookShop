package org.example.library.service;

import org.aspectj.weaver.ast.Or;
import org.example.library.dto.OrderDto;
import org.example.library.model.Order;
import org.example.library.model.ShoppingCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;


import java.sql.Date;
import java.util.List;

public interface OrderService {
    List<Order> findAll();

    List<Order> findByUser(String username);

    Order save(ShoppingCart shoppingCart, OrderDto orderDto);

    Order updateOrder(Long id, boolean is_accepted, java.util.Date date);

    Page<Order> findListOrder(Pageable pageable);

    Page<Order> findOrdersToday(Pageable pageable);

    List<Order> findOrdersToDate();

    Page<Order> findAllOrder(Pageable pageable);

    Page<Order> findOrderBetweenDate(Date startDate, Date endDate, Pageable pageable);

    List<Order> listOrderBetweenDate(Date startDate, Date endDate);

    Page<Order> searchOrdersToDate(Date today, Pageable pageable);

    List<Order> searchOrdersToDate(Date today);

    List<Order> findOrderByCustomerId(Long customerId);
}
