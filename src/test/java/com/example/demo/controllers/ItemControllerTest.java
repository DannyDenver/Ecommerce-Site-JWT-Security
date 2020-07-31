package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() throws IllegalAccessException {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_item_by_id_success() {
        Long id = (long)1;
        Item item = new Item();
        String itemName = "candy bar";
        item.setName(itemName);

        when(itemRepository.findById(id)).thenReturn(java.util.Optional.of(item));

        final ResponseEntity<Item> response =  itemController.getItemById(id);

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 200);

        Item responseItem = response.getBody();

        assertEquals(responseItem.getName(), itemName);
    }

    @Test
    public void get_item_not_found_error() {
        final ResponseEntity<Item> response =  itemController.getItemById((long)1);

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 404);
    }

    @Test
    public void get_items_by_name_success() {
        Long id = (long)1;
        Item item = new Item();
        String itemName = "Milk";
        item.setName(itemName);
        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findByName(itemName)).thenReturn(items);

        final ResponseEntity<List<Item>> response =  itemController.getItemsByName(itemName);

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 200);

        List<Item> responseItems = response.getBody();

        assertEquals(responseItems.size(), 1);
        assertEquals(responseItems.get(0).getName(), itemName);
    }

    @Test
    public void get_items_by_name_not_found_error() {
        final ResponseEntity<List<Item>> response =  itemController.getItemsByName("Dog food");

        assertNotNull(response);
        assertEquals(response.getStatusCodeValue(), 404);
    }
}
