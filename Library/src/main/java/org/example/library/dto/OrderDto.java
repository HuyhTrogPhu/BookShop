package org.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {

    private Long id;

    private String orderStatus;

    private double totalPrice;

    private double tax;

//    total items
    private int quantity;

    private String paymentMethod;

    private boolean isAccept;

    private List<OrderItemDto> orderItems;
}
