package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import com.example.demo.logging.CsvLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private CsvLogger csvLogger;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			csvLogger.logToCsv(user.getId(),"addTocart", "items", request.getItemId(), "User not found with username " + request.getUsername(), "NotFound");

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());

		if(!item.isPresent()) {
			csvLogger.logToCsv(user.getId(),"addTocart", "items", request.getItemId(), "Item not found with id " + request.getItemId(), "NotFound");

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);

		csvLogger.logToCsv(user.getId(),"addTocart", "items", request.getItemId(), "Succsessfully added " + request.getQuantity() + " items with id " + request.getItemId(), "Success");

		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			csvLogger.logToCsv(user.getId(),"removeFromcart", "items", request.getItemId(), "User not found with username " + request.getUsername(), "NotFound");

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			csvLogger.logToCsv(user.getId(),"removeFromcart", "items", request.getItemId(), "Item not found with id " + request.getItemId(), "NotFound");

			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));

		cartRepository.save(cart);

		csvLogger.logToCsv(user.getId(),"removeFromcart", "items", request.getItemId(),  request.getQuantity() + " items with id " + request.getItemId() + " were removed", "Success");

		return ResponseEntity.ok(cart);
	}
		
}
