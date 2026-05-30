package com.lista.compras.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lista.compras.models.ShoppingItem;
import com.lista.compras.models.ShoppingList;
import com.lista.compras.repositories.ShoppingItemRepository;
import com.lista.compras.repositories.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiRecipeService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.model}")
    private String apiModel;

    @Autowired
    private ShoppingListRepository listRepository;

    @Autowired
    private ShoppingItemRepository itemRepository;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public AiRecipeService() {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    public static class AiResponse {
        public String recipeName;
        public List<ShoppingItem> items;
    }

    public ShoppingList generateListFromRecipe(String recipe) throws Exception {
        String systemPrompt = "You are an expert shopping list assistant. Extract the recipe name and ingredients from the recipe. " +
                "Return ONLY a valid JSON object with two fields: 'recipeName' (string) and 'items' (array of objects). " +
                "Each item object must have: 'description' (string), 'quantity' (number), 'unit' (string: 'und', 'g', 'kg', 'l', 'ml'), 'price' (number: 0.0), 'isChecked' (boolean: false). " +
                "Follow these strict parsing and commercial rounding rules to format the list for actual grocery shopping: " +
                "1. Allowed units are ONLY: 'und', 'g', 'kg', 'l', 'ml'. " +
                "2. COMMERCIAL ROUND-UP RULE: Generate quantities and units that represent standard packages available for purchase in a store. Do not suggest fractions of standard containers. Put the exact recipe requirements in parentheses in the 'description'. " +
                "- Liquids like Milk (Leite) or Water (Água) must be rounded up to the nearest Liter ('l'). E.g., 800ml -> description: \"Leite (800ml)\", quantity: 1.0, unit: \"l\"; 1.5 liters -> description: \"Leite (1.5L)\", quantity: 2.0, unit: \"l\". " +
                "- Flour, Sugar, Rice, Beans, etc. (Farinha, Açúcar, Arroz, Feijão) are sold in 1kg packages. Round up to the nearest kg ('kg'). E.g., 400g of flour -> description: \"Farinha de trigo (400g)\", quantity: 1.0, unit: \"kg\". " +
                "- Items sold in cans, cartons, cups, spoons, bags, bottles, or packages must use unit 'und' representing standard containers. E.g.: " +
                "\"2 latas de leite condensado (395g cada)\" -> description: \"Leite condensado (395g)\", quantity: 2.0, unit: \"und\"; " +
                "\"1 caixinha de creme de leite (200g)\" -> description: \"Creme de leite (200g)\", quantity: 1.0, unit: \"und\"; " +
                "\"1 xícara de óleo de soja\" -> description: \"Óleo de soja (1 xícara)\", quantity: 1.0, unit: \"und\"; " +
                "\"1 colher de sopa de fermento em pó\" -> description: \"Fermento em pó (1 colher)\", quantity: 1.0, unit: \"und\". " +
                "- If the unit of measure is not clear, default to 'und'. " +
                "Diverse Examples: " +
                "- Recipe: \"800ml de leite, 400g de farinha de trigo, 3 ovos, 1 colher de fermento e 1 lata de leite condensado de 395g.\" -> " +
                "JSON items: [ " +
                "{\"description\": \"Leite (800ml)\", \"quantity\": 1.0, \"unit\": \"l\", \"price\": 0.0, \"isChecked\": false}, " +
                "{\"description\": \"Farinha de trigo (400g)\", \"quantity\": 1.0, \"unit\": \"kg\", \"price\": 0.0, \"isChecked\": false}, " +
                "{\"description\": \"Ovos\", \"quantity\": 3.0, \"unit\": \"und\", \"price\": 0.0, \"isChecked\": false}, " +
                "{\"description\": \"Fermento em pó (1 colher)\", \"quantity\": 1.0, \"unit\": \"und\", \"price\": 0.0, \"isChecked\": false}, " +
                "{\"description\": \"Leite condensado (395g)\", \"quantity\": 1.0, \"unit\": \"und\", \"price\": 0.0, \"isChecked\": false} " +
                "]. " +
                "3. The 'price' must always be 0.0. " +
                "4. Do not include markdown formatting or json code blocks, just the raw JSON object.";

        String requestBody = """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "system",
                      "content": "%s"
                    },
                    {
                      "role": "user",
                      "content": "Recipe:\\n%s"
                    }
                  ]
                }
                """.formatted(
                    apiModel, 
                    systemPrompt.replace("\"", "\\\"").replace("\n", "\\n"), 
                    recipe.replace("\"", "\\\"").replace("\n", "\\n")
                );

        String response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        // Parse OpenRouter/Groq Response
        Map<String, Object> jsonResponse = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> choices = (List<Map<String, Object>>) jsonResponse.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("No response from AI");
        }
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String text = (String) message.get("content");

        // Clean text from markdown if the LLM hallucinated markdown
        text = text.replaceAll("```json", "").replaceAll("```", "").trim();

        AiResponse parsed = objectMapper.readValue(text, AiResponse.class);

        ShoppingList list = new ShoppingList();
        list.setName(parsed.recipeName != null ? parsed.recipeName : "Nova Lista");
        list.setBudget(0.0);

        for (ShoppingItem item : parsed.items) {
            item.setPrice(0.0); // Enforce 0.0
        }

        list.setItems(parsed.items);
        return list;
    }
}
