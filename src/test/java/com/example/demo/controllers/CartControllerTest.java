package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.Option;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() throws IllegalAccessException {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart_success() {
        String username = "dude";
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername(username);

        User user = new User();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        user.setCart(cart);
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        Item item = new Item();
        item.setId((long)1);
        item.setName("milk");
        item.setPrice(BigDecimal.valueOf(2));
        Optional<Item> optionalItem = Optional.of(item);

        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(optionalItem);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);

        Cart responseCart = response.getBody();

        assertEquals(responseCart.getItems().size(), 2);
    }

    @Test
    public void add_to_cart_no_user_found_error() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertEquals(response.getStatusCodeValue(), 404);
    }

    @Test
    public void add_to_cart_no_item_found_error() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        String username = "danman";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(user);


        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertEquals(response.getStatusCodeValue(), 404);
    }

}
