package org.example.library.repository;

import org.example.library.model.Customer;
import org.example.library.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.sql.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select concat(c.firstName, ' ', c.lastName ), c.phoneNumber, o.orderDate, o.quantity, o.tax, o.totalPrice, " +
            "o.paymentMethod, o.orderStatus, o.deliveryDate, o.isAccept" +
            " from Customer c join c.orders o join o.orderDetailList od group by o.id")
    Page<Order> getOrders(Pageable pageable);

    @Query("select o from Order o group by o.id")
    Page<Order> findAllOrder(Pageable pageable);

    //    select order between date
    @Query("select o from Order o where o.orderDate between :startDate and  :endDate")
    Page<Order> findOrderBetweenDate(Date startDate, Date endDate, Pageable pageable);

    @Query("select o from Order o where o.orderDate between :startDate and  :endDate")
    List<Order> listOrderBetweenDate(Date startDate, Date endDate);

    //    select order today
    @Query("select o from Order o where o.orderDate >= current_date ")
    Page<Order> findOrdersToDate(Pageable pageable);

    //    select order today excel
    @Query("select o from Order o where o.orderDate >= current_date ")
    List<Order> findOrdersToDate();

    //    search orders today
    @Query("select o from Order o where function('date_format', o.orderDate, '%Y-%m-%d') = " +
            "function('date_format', :today, '%Y-%m-%d')")
    Page<Order> searchOrdersToDate(@Param("today") Date today, Pageable pageable);


    //    search orders today export excel
    @Query("select o from Order o where function('date_format', o.orderDate, '%Y-%m-%d') " +
            "= function('date_format', :today, '%Y-%m-%d')")
    List<Order> searchOrdersToDate(@Param("today") Date today);

//    select customer order
    List<Order> findOrderByCustomerId(Long customerId);
}
