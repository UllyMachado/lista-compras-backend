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
        String systemPrompt = "You are a shopping list assistant. " +
                "Extract the recipe name and ingredients from the recipe. Return ONLY a valid JSON object with two fields: 'recipeName' (string) and 'items' (array of objects). " +
                "Each item object must have the following fields: 'description' (string), 'quantity' (number), 'unit' (string: UND, G, KG, L, ML), 'price' (number: 0.0), 'isChecked' (boolean: false). " +
                "If the unit is not clear, use 'UND'. Do not include markdown formatting or json code blocks, just the raw JSON object.";

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
        list = listRepository.save(list);

        for (ShoppingItem item : parsed.items) {
            item.setShoppingList(list);
            item.setPrice(0.0); // Enforce 0.0
            itemRepository.save(item);
        }

        list.setItems(parsed.items);
        return list;
    }
}
