package org.example.library.service;

import org.example.library.dto.CustomerDto;
import org.example.library.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

public interface CustomerService {

    Customer save(CustomerDto customerDto);

    Customer findByUsername(String username);

    Customer findById(Long id);

    Customer updateProfile(CustomerDto customerDto);

    Customer updateCheckOut(CustomerDto customerDto);

    Customer changePass(CustomerDto customerDto);

    CustomerDto getCustomer(String username);

    Page<Objects[]> getAllCustomers(Pageable pageable);

    List<Customer> listCustomerTotalOrderAndTotalPrice();

//    list customer buy the most
    Page<Objects[]> getAllCustomerBuyTheMost(Pageable pageable);

    List<Object[]> listCustomerIsOrder();

//    list customer buy the least
    Page<Objects[]> getAllCustomerBuyTheLeast(Pageable pageable);

    List<Customer> listCustomerBuyTheLeast();

//    list customer not buy
    Page<Object[]> getAllCustomerNotBuy(Pageable pageable);


//    search customer by keyword
    Page<Customer> searchCustomerByKeyword(String keyword, Pageable pageable);

}
