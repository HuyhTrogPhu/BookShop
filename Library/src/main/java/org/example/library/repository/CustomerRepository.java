package org.example.library.repository;

import org.example.library.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByUsername(String username);

    @Query("select c, count(o.id) as totalOrder, sum(o.totalPrice) as totalPrice " +
            "from Customer c left join c.orders o " +
            "group by c.id ")
    Page<Objects[]> findListCustomer(Pageable pageable);

//    @Query("select c as customer, count(o.id) as totalOrder, sum(o.totalPrice) as totalPrice " +
//            "from Customer c left join c.orders o " +
//            "group by c.id ")
//    List<Customer> listTotalOrderAndTotalPrice();


    // select list customer buy the most
    @Query("select c, count(o.id) as totalOrder, sum(o.totalPrice) as totalPrice " +
            "from Customer c left join c.orders o " +
            "group by c.id order by totalOrder desc ")
    Page<Objects[]> findBuyTheMost(Pageable pageable);

    // select list customer is order
    @Query("select c, count (o), sum(o.totalPrice) from Customer c " +
            "left join Order o on c.id = o.customer.id group by c.id having count(o) > 0")
    List<Object[]> listCustomerIsOrder();


    // select list customer buy the least
    @Query("select c, count(o.id) as totalOrder, sum(o.totalPrice) as totalPrice " +
            "from Customer c left join c.orders o " +
            "group by c.id order by totalOrder asc ")
    Page<Objects[]> findBuyTheLeast(Pageable pageable);

    // select list customer buy the least
    @Query("select c from Customer c join c.orders o group by c.id order by count(o.id) asc")
    List<Customer> listBuyTheLeast();

    // select list customer not buy
    @Query("select c from Customer c where c.id not in (select o.customer.id from Order o)")
    Page<Customer> findNotInCustomer(Pageable pageable);

    // select list customer not buy
    @Query("SELECT c, COALESCE(COUNT(o.id), 0), COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Customer c LEFT JOIN c.orders o " +
            "WHERE c.id NOT IN (SELECT o.customer.id FROM Order o) " +
            "GROUP BY c.id")
    Page<Object[]> listNotBuyCustomersWithOrderStats(Pageable pageable);


//    search customer by keyword
    @Query("select c from Customer c where c.firstName like %?1% or c.lastName like %?1% or c.username like %?1%")
    Page<Customer> searchCustomer(String keyword, Pageable pageable);
}
