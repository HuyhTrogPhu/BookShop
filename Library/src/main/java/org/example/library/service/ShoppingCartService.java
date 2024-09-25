package org.example.library.service;

import org.example.library.dto.BookDto;
import org.example.library.dto.ShoppingCartDto;
import org.example.library.model.Book;
import org.example.library.model.Customer;
import org.example.library.model.ShoppingCart;

public interface ShoppingCartService {

    ShoppingCart addItemToCart(BookDto bookDto, int quantity, String username);

    ShoppingCart updateCart(BookDto bookDto, int quantity, String username);

    ShoppingCart removeItemFromCart(BookDto bookDto, String username);

    ShoppingCartDto addItemToCartSession(BookDto bookDto, int quantity);

    ShoppingCartDto updateCartSession(BookDto bookDto, int quantity);

    ShoppingCartDto removeItemFromCartSession(BookDto bookDto);

    ShoppingCart combineCart(ShoppingCartDto cartDto, ShoppingCart cart);

    void deleteCartById(Long id);

    ShoppingCart getCart(String username);

}
