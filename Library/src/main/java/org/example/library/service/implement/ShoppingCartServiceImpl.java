package org.example.library.service.implement;

import jakarta.servlet.http.HttpSession;
import org.example.library.dto.BookDto;
import org.example.library.dto.CartItemDto;
import org.example.library.dto.ShoppingCartDto;
import org.example.library.model.Book;
import org.example.library.model.CartItem;
import org.example.library.model.Customer;
import org.example.library.model.ShoppingCart;
import org.example.library.repository.CartItemRepository;
import org.example.library.repository.ShoppingCartRepository;
import org.example.library.service.CustomerService;
import org.example.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.beans.Transient;
import java.util.HashSet;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private CartItemRepository itemRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HttpSession session;

    @Override
    @Transactional
    public ShoppingCart addItemToCart(BookDto bookDto, int quantity, String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getCart();

        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();

        }

        Set<CartItem> cartItemsList = shoppingCart.getCartItems();
        CartItem cartItem = find(cartItemsList, bookDto.getId());
        Book book = transfer(bookDto);

        double unitPrice = bookDto.getCostPrice();
        int itemQuantity = 0;

        if (cartItemsList == null) {
            cartItemsList = new HashSet<>();

        }
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setCart(shoppingCart);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(unitPrice);
            cartItem.setCart(shoppingCart);
            cartItemsList.add(cartItem);
            itemRepository.save(cartItem);
        } else {
            itemQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(itemQuantity);
            itemRepository.save(cartItem);
        }

        shoppingCart.setCartItems(cartItemsList);

        double totalPrice = totalPrice(shoppingCart.getCartItems());
        int totalItem = totalItem(shoppingCart.getCartItems());

        shoppingCart.setTotalItems(totalItem);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setCustomer(customer);

        return cartRepository.save(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCart updateCart(BookDto bookDto, int quantity, String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getCart();

        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setCustomer(customer);
        }

        Set<CartItem> cartItemList = shoppingCart.getCartItems();
        CartItem item = find(cartItemList, bookDto.getId());

        if (item != null) {
            item.setQuantity(quantity);
            itemRepository.save(item);
        }

        shoppingCart.setCartItems(cartItemList);
        shoppingCart.setTotalPrice(totalPrice(cartItemList));
        shoppingCart.setTotalItems(totalItem(cartItemList));


        return cartRepository.save(shoppingCart);

    }

    @Override
    public ShoppingCart removeItemFromCart(BookDto bookDto, String username) {
        ShoppingCart shoppingCart = null;
        try {
            Customer customer = customerService.findByUsername(username);
            shoppingCart = customer.getCart();

            Set<CartItem> cartItemList = shoppingCart.getCartItems();
            CartItem item = find(cartItemList, bookDto.getId());

            if (item != null) {
                cartItemList.remove(item);
                itemRepository.delete(item);
                System.out.println("Item removed: " + item.getBook().getTitle());
            } else {
                System.out.println("Item not found: " + bookDto.getId());
            }

            shoppingCart.setCartItems(cartItemList);
            shoppingCart.setTotalItems(totalItem(cartItemList));
            shoppingCart.setTotalPrice(totalPrice(cartItemList));
            cartRepository.save(shoppingCart);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return shoppingCart;
    }


    @Override
    public ShoppingCartDto addItemToCartSession(BookDto bookDto, int quantity) {
        ShoppingCartDto cartDto = (ShoppingCartDto) session.getAttribute("cart");
        if (cartDto == null) {
            cartDto = new ShoppingCartDto();
            session.setAttribute("cart", cartDto);
        }

        Set<CartItemDto> cartItemDtoList = cartDto.getCartItems();
        if (cartItemDtoList == null) {
            cartItemDtoList = new HashSet<>();
        }

        CartItemDto cartItemDto = findDto(cartDto, bookDto.getId());
        double unitPrice = bookDto.getCostPrice();
        int itemQuantity;

        if (cartItemDto == null) {
            cartItemDto = new CartItemDto();
            cartItemDto.setProduct(bookDto);
            cartItemDto.setUnitPrice(unitPrice);
            cartItemDto.setQuantity(quantity);
            cartItemDtoList.add(cartItemDto);
        } else {
            itemQuantity = cartItemDto.getQuantity() + quantity;
            cartItemDto.setQuantity(itemQuantity);
        }

        cartDto.setCartItems(cartItemDtoList);
        updateCartDtoTotals(cartDto);
        session.setAttribute("cart", cartDto);

        return cartDto;
    }

    @Override
    public ShoppingCartDto updateCartSession(BookDto bookDto, int quantity) {
        ShoppingCartDto cartDto = (ShoppingCartDto) session.getAttribute("cart");
        if (cartDto == null) {
            return null;
        }

        CartItemDto cartItemDto = findDto(cartDto, bookDto.getId());
        if (cartItemDto != null) {
            cartItemDto.setQuantity(quantity);
        }

        updateCartDtoTotals(cartDto);
        session.setAttribute("cart", cartDto);

        return cartDto;
    }

    @Override
    public ShoppingCartDto removeItemFromCartSession(BookDto bookDto) {
        ShoppingCartDto cartDto = (ShoppingCartDto) session.getAttribute("cart");

        if (cartDto == null) {
            return null;
        }

        Set<CartItemDto> cartItemDtoSet = cartDto.getCartItems();
        CartItemDto cartItemDto = findDto(cartDto, bookDto.getId());
        cartItemDtoSet.remove(cartItemDto);

        updateCartDtoTotals(cartDto);
        session.setAttribute("cart", cartDto);

        return cartDto;
    }

    @Override
    public ShoppingCart combineCart(ShoppingCartDto cartDto, ShoppingCart cart) {
        if (cart == null) {
            cart = new ShoppingCart();
        }
        Set<CartItem> cartItemList = cart.getCartItems();
        if (cartItemList == null) {
            cartItemList = new HashSet<>();
        }
        Set<CartItem> cartItemDto = convertCartItem(cartDto.getCartItems(), cart);
        cartItemDto.addAll(cartItemDto);

        updateCartTotals(cart);

        return cart;
    }

    @Override
    public void deleteCartById(Long id) {
        ShoppingCart shoppingCart = cartRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(shoppingCart) && !ObjectUtils.isEmpty(shoppingCart.getCartItems())) {
            itemRepository.deleteAll(shoppingCart.getCartItems());
        }

        shoppingCart.getCartItems().clear();
        shoppingCart.setTotalItems(0);
        shoppingCart.setTotalPrice(0);

        cartRepository.save(shoppingCart);
    }


    @Override
    public ShoppingCart getCart(String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart cart = customer.getCart();
        return cart;
    }


    private CartItem find(Set<CartItem> cartItems, Long bookId) {
        return cartItems.stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);
    }


    private CartItemDto findDto(ShoppingCartDto shoppingCartDto, long bookId) {
        if (shoppingCartDto == null) {
            return null;
        } else {
            for (CartItemDto item : shoppingCartDto.getCartItems()) {
                if (item.getProduct().getId() == bookId) {
                    return item;
                }
            }
            return null;
        }
    }

    private int totalItem(Set<CartItem> cartItems) {
        int totalItem = 0;
        for (CartItem item : cartItems) {
            totalItem += item.getQuantity();
        }
        return totalItem;
    }

    private double totalPrice(Set<CartItem> cartItems) {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getUnitPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    private void updateCartTotals(ShoppingCart shoppingCart) {
        Set<CartItem> cartItemList = shoppingCart.getCartItems();
        shoppingCart.setTotalPrice(totalPrice(cartItemList));
        shoppingCart.setTotalItems(totalItem(cartItemList));
    }

    private int totalItemDto(Set<CartItemDto> cartItems) {
        int totalItem = 0;
        for (CartItemDto item : cartItems) {
            totalItem += item.getQuantity();
        }
        return totalItem;
    }

    private double totalPriceDto(Set<CartItemDto> cartItems) {
        double totalPrice = 0;
        for (CartItemDto item : cartItems) {
            totalPrice += item.getUnitPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    private void updateCartDtoTotals(ShoppingCartDto shoppingCartDto) {
        Set<CartItemDto> cartItemDtoList = shoppingCartDto.getCartItems();
        shoppingCartDto.setTotalPrice(totalPriceDto(cartItemDtoList));
        shoppingCartDto.setTotalItems(totalItemDto(cartItemDtoList));
    }

    private Book transfer(BookDto bookDto) {
        Book book = new Book();
        book.setId(bookDto.getId());
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setCurrentQuantity(bookDto.getCurrentQuantity());
        book.setCostPrice(bookDto.getCostPrice());
        book.setSalePrice(bookDto.getSalePrice());
        book.setImage(bookDto.getImage());
        book.setDescription(bookDto.getDescription());
        book.set_active(bookDto.is_active());
        book.set_delete(bookDto.is_delete());
        book.setCategory(bookDto.getCategory());

        return book;
    }

    private Set<CartItem> convertCartItem(Set<CartItemDto> cartItemDtoSet, ShoppingCart cart) {
        Set<CartItem> cartItems = new HashSet<>();
        for (CartItemDto cartItemDto : cartItemDtoSet) {
            CartItem cartItem = new CartItem();

            cartItem.setQuantity(cartItemDto.getQuantity());
            cartItem.setBook(transfer(cartItemDto.getProduct()));
            cartItem.setUnitPrice(cartItemDto.getUnitPrice());
            cartItem.setId(cartItemDto.getId());
            cartItem.setCart(cart);
            cartItems.add(cartItem);

        }
        return cartItems;
    }


}
