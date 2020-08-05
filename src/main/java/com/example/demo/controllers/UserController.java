package com.example.demo.controllers;
import com.example.demo.logging.CsvLogger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private CsvLogger csvLogger;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);

		 if(user == null){
			 csvLogger.logToCsv(null,"findByUserName", null, null, "Not found user with name  " + user.getUsername() , "NotFound");
			 return	ResponseEntity.notFound().build();
		} else {
			 csvLogger.logToCsv(user.getId(),"findByUserName", null, null, "Getting user with name  " + user.getUsername() , "Success");
			 return  ResponseEntity.ok(user);

		 }
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		Cart cart = new Cart();

		cartRepository.save(cart);
		user.setCart(cart);

		if(createUserRequest.getPassword().length() < 7 ||
			!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			return ResponseEntity.badRequest().build();
		}

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		try {
			User newUser = userRepository.save(user);
			csvLogger.logToCsv(newUser.getId(),"createNewUserwithCart", "cart", newUser.getCart().getId(), "Successfully created user with username " + newUser.getUsername() , "Success");
			// tcpInput.submit("INFO: New user create request received");
		}catch (Error e) {  // | IOException e
			csvLogger.logToCsv(null,"createNewUserwithCart", "cart", null, "Failed creating user with username " + user.getUsername() , "Error");
		}

		return ResponseEntity.ok(user);
	}
}
