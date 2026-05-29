package com.lista.compras.controllers;

import com.lista.compras.models.ShoppingItem;
import com.lista.compras.models.ShoppingList;
import com.lista.compras.repositories.ShoppingItemRepository;
import com.lista.compras.repositories.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lists")
@CrossOrigin(origins = "*")
public class ListController {

    @Autowired
    private ShoppingListRepository listRepository;

    @Autowired
    private ShoppingItemRepository itemRepository;

    @GetMapping
    public List<ShoppingList> getAllLists() {
        return listRepository.findAll();
    }

    @PostMapping
    public ShoppingList createList(@RequestBody ShoppingList list) {
        return listRepository.save(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingList> getListById(@PathVariable String id) {
        Optional<ShoppingList> list = listRepository.findById(id);
        return list.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoppingList> updateList(@PathVariable String id, @RequestBody ShoppingList listDetails) {
        return listRepository.findById(id).map(list -> {
            list.setName(listDetails.getName());
            list.setBudget(listDetails.getBudget());
            return ResponseEntity.ok(listRepository.save(list));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable String id) {
        if (listRepository.existsById(id)) {
            listRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{listId}/items")
    public ResponseEntity<ShoppingItem> addItem(@PathVariable String listId, @RequestBody ShoppingItem item) {
        return listRepository.findById(listId).map(list -> {
            item.setShoppingList(list);
            return ResponseEntity.ok(itemRepository.save(item));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{listId}/items/{itemId}")
    public ResponseEntity<ShoppingItem> updateItem(@PathVariable String listId, @PathVariable String itemId, @RequestBody ShoppingItem itemDetails) {
        return itemRepository.findById(itemId).map(item -> {
            item.setDescription(itemDetails.getDescription());
            item.setQuantity(itemDetails.getQuantity());
            item.setPrice(itemDetails.getPrice());
            item.setIsChecked(itemDetails.getIsChecked());
            item.setUnit(itemDetails.getUnit());
            return ResponseEntity.ok(itemRepository.save(item));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{listId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable String listId, @PathVariable String itemId) {
        if (itemRepository.existsById(itemId)) {
            itemRepository.deleteById(itemId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
