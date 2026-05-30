package com.lista.compras.controllers;

import com.lista.compras.models.ShoppingItem;
import com.lista.compras.models.ShoppingList;
import com.lista.compras.repositories.ShoppingItemRepository;
import com.lista.compras.repositories.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
        if (list.getItems() != null) {
            for (ShoppingItem item : list.getItems()) {
                validateItem(item);
                item.setShoppingList(list);
            }
        }
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
        validateItem(item);
        return listRepository.findById(listId).map(list -> {
            item.setShoppingList(list);
            return ResponseEntity.ok(itemRepository.save(item));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{listId}/items/{itemId}")
    public ResponseEntity<ShoppingItem> updateItem(@PathVariable String listId, @PathVariable String itemId, @RequestBody ShoppingItem itemDetails) {
        validateItem(itemDetails);
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

    private void validateItem(ShoppingItem item) {
        if (item.getDescription() == null || item.getDescription().trim().length() < 2 || item.getDescription().trim().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição deve conter entre 2 e 100 caracteres.");
        }
        if (item.getPrice() == null || item.getPrice() < 0.0 || item.getPrice() > 99999.99) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço deve ser entre 0.00 e 99.999,99.");
        }
        if (item.getQuantity() == null || item.getQuantity() < 0.01 || item.getQuantity() > 9999.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser entre 0.01 e 9.999.");
        }
    }
}
