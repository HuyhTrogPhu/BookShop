package org.example.library.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.library.dto.CustomerDto;
import org.example.library.model.Customer;
import org.example.library.repository.CityRepository;
import org.example.library.repository.CustomerRepository;
import org.example.library.repository.RoleRepository;
import org.example.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Customer save(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPassword(customerDto.getPassword());
        customer.setUsername(customerDto.getUsername());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        return customerRepository.save(customer);
    }

    @Override
    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer updateProfile(CustomerDto customerDto) {
        Customer customer = customerRepository.findByUsername(customerDto.getUsername());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPassword(customerDto.getPassword());
        customer.setAddress(customerDto.getAddress());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setCity(customerDto.getCity());
        customer.setCountry(customerDto.getCountry());

        return customerRepository.save(customer);
    }


    @Override
    public Customer updateCheckOut(CustomerDto customerDto) {
        Customer customer = customerRepository.findByUsername(customerDto.getUsername());

        customer.setAddress(customerDto.getAddress());
        customer.setCity(customerDto.getCity());
        customer.setCountry(customerDto.getCountry());
        customer.setPhoneNumber(customerDto.getPhoneNumber());

        return customerRepository.save(customer);
    }


    @Override
    public Customer changePass(CustomerDto customerDto) {
        Customer customer = customerRepository.findByUsername(customerDto.getUsername());
        customer.setPassword(customerDto.getPassword());
        return customerRepository.save(customer);
    }

    @Override
    public CustomerDto getCustomer(String username) {
        CustomerDto customerDto = new CustomerDto();
        Customer customer = customerRepository.findByUsername(username);
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPassword(customer.getPassword());
        customerDto.setAddress(customer.getAddress());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setCity(customer.getCity());
        customerDto.setCountry(customer.getCountry());
        return customerDto;
    }

    @Override
    public Page<Objects[]> getAllCustomers(Pageable pageable) {
        return customerRepository.findListCustomer(pageable);
    }

    @Override
    public List<Customer> listCustomerTotalOrderAndTotalPrice() {
        return null;
    }


    @Override
    public Page<Objects[]> getAllCustomerBuyTheMost(Pageable pageable) {
        return customerRepository.findBuyTheMost(pageable);
    }

    @Override
    public List<Object[]> listCustomerIsOrder() {
        return customerRepository.listCustomerIsOrder();
    }


    @Override
    public Page<Objects[]> getAllCustomerBuyTheLeast(Pageable pageable) {
        return customerRepository.findBuyTheLeast(pageable);
    }

    @Override
    public List<Customer> listCustomerBuyTheLeast() {
        return customerRepository.listBuyTheLeast();
    }

    @Override
    public Page<Object[]> getAllCustomerNotBuy(Pageable pageable) {
        return customerRepository.listNotBuyCustomersWithOrderStats(pageable);
    }

    @Override
    public Page<Customer> searchCustomerByKeyword(String keyword, Pageable pageable) {
        return customerRepository.searchCustomer(keyword, pageable);
    }


}
