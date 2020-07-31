package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() throws IllegalAccessException {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
        TestUtils.injectObject(orderController, "userRepository", userRepository);
    }

    @Test
    public void submit_order_success() {
        String username = "danman";

        User user = new User();
        user.setUsername(username);

        Cart cart = new Cart();
        cart.setId((long)1);
        List<Item> items = new ArrayList<Item>();
        items.add(new Item());
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(44.55));

        user.setCart(cart);

        when(userRepository.findByUsername(username)).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit(username);

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 200);

        UserOrder order = response.getBody();

        assertEquals(order.getItems().size(), items.size());
        assertEquals(order.getTotal(), cart.getTotal());
    }

    @Test
    public void submit_order_not_found_error() {
        final ResponseEntity<UserOrder> response = orderController.submit("dan");

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 404);
    }

    @Test
    public void order_history_success() {
        String username = "danman";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(userRepository.findByUsername(username)).thenReturn(user);

        List<UserOrder> userOrders = new ArrayList<>();
        UserOrder userOrder = new UserOrder();
        userOrders.add(userOrder);

        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 200);

        List<UserOrder> responseUserOrders = response.getBody();

        assertNotNull(responseUserOrders);
        assertEquals(responseUserOrders.size(), userOrders.size());
        assertEquals(responseUserOrders.get(0), userOrders.get(0));
    }

    @Test
    public void order_history_not_found_error() {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("dan");

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 404);
    }
}
