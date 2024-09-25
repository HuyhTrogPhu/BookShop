package org.example.library.service;

import org.example.library.model.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    List<OrderDetail> getOrderDetailByIdCustomer(Long id);

    List<OrderDetail> getOrderDetailByOrderId(Long id);
}
