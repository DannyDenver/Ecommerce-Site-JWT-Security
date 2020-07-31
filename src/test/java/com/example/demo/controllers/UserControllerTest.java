package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.apache.coyote.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() throws IllegalAccessException {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("password123")).thenReturn("thisIsHashed");

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("danman");
        r.setPassword("password123");
        r.setConfirmPassword("password123");

        final ResponseEntity<User> response =  userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(u.getId(), 0);
        assertEquals(u.getUsername(), "danman");
        assertEquals(u.getPassword(), "thisIsHashed");
    }

    @Test
    public void password_min_length_error() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("danman");
        r.setPassword("pass");
        r.setConfirmPassword("pass");

        final ResponseEntity<User> response =  userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void confirm_password_does_not_match_password_error() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("danman");
        r.setPassword("password123");
        r.setConfirmPassword("pass125");

        final ResponseEntity<User> response =  userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void find_by_username_success() {
        String password = "HashedPassword";
        String username = "danman";

        User user = new User();
        user.setPassword(password);
        user.setUsername(username);

        when(userRepository.findByUsername("danman")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName(username);

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 200);

        User returnedUser = response.getBody();

        assertEquals(returnedUser.getPassword(), password);
        assertEquals(returnedUser.getUsername(), username);
    }

    @Test
    public void find_by_username_not_found() {
        final ResponseEntity<User> response = userController.findByUserName("beepboop");

        assertEquals(response.getStatusCodeValue(), 404);
    }

    @Test
    public void find_by_user_id_success() {
        User user = new User();

        when(userRepository.findById((long)1)).thenReturn(java.util.Optional.of(user));

        final ResponseEntity<User> response = userController.findById((long)1);

        assertNotNull(response);

        assertEquals(response.getStatusCodeValue(), 200);
        User returnedUser = response.getBody();

        assertEquals(returnedUser, user);
    }

    @Test
    public void find_by_user_id_not_found() {
        final ResponseEntity<User> response = userController.findById((long)1);

        assertNotNull(response);

        assertEquals(response.getStatusCodeValue(), 404);
    }

}
