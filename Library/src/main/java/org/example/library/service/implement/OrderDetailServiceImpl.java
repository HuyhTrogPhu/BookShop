package org.example.library.service.implement;

import org.example.library.model.OrderDetail;
import org.example.library.repository.OrderDetailRepository;
import org.example.library.repository.OrderRepository;
import org.example.library.service.OrderDetailService;
import org.example.library.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;


    @Override
    public List<OrderDetail> getOrderDetailByIdCustomer(Long id) {
        return orderDetailRepository.getOrderDetailByIdCustomer(id);
    }

    @Override
    public List<OrderDetail> getOrderDetailByOrderId(Long id) {
        return orderDetailRepository.getOrderDetailByIdOrder(id);
    }
}
