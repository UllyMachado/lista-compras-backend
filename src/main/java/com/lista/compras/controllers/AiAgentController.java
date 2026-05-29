package com.lista.compras.controllers;

import com.lista.compras.models.ShoppingItem;
import com.lista.compras.models.ShoppingList;
import com.lista.compras.services.AiRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiAgentController {

    @Autowired
    private AiRecipeService aiRecipeService;

    public static class RecipeRequest {
        public String recipe;
    }

    @PostMapping("/recipe-to-list")
    public ResponseEntity<ShoppingList> createListFromRecipe(@RequestBody RecipeRequest request) {
        try {
            ShoppingList list = aiRecipeService.generateListFromRecipe(request.recipe);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
