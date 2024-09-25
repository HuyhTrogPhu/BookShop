package org.example.library.repository;

import org.example.library.model.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("select od from OrderDetail od where od.id = ?1")
    List<OrderDetail> getOrderDetail(Long id);

    @Query("select od from OrderDetail od join od.order o join o.customer c where c.id = ?1")
    List<OrderDetail> getOrderDetailByIdCustomer(Long id);

    @Query("select od from OrderDetail od join od.order o where o.id = ?1")
    List<OrderDetail> getOrderDetailByIdOrder(Long id);


}
