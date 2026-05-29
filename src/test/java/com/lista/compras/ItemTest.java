package com.lista.compras;

import com.lista.compras.controllers.AiAgentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testAi() {
        try {
            AiAgentController.RecipeRequest req = new AiAgentController.RecipeRequest();
            req.recipe = "Bolo de cenoura";
            
            ResponseEntity<String> res = restTemplate.postForEntity("/api/ai/recipe-to-list", req, String.class);
            System.out.println("AI Response: " + res.getStatusCode() + " " + res.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
