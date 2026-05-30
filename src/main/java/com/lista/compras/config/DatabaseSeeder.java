package com.lista.compras.config;

import com.lista.compras.models.Category;
import com.lista.compras.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        saveIfAbsent("Outros", "Itens diversos ou sem categoria específica.");
        saveIfAbsent("Hortifruti", "Frutas, legumes e verduras frescos.");
        saveIfAbsent("Açougue e Peixaria", "Carnes bovinas, suínas, aves e pescados.");
        saveIfAbsent("Padaria e Confeitaria", "Pães, bolos, tortas e doces.");
        saveIfAbsent("Frios e Laticínios", "Queijos, presuntos, iogurtes e manteigas.");
        saveIfAbsent("Mercearia Seca", "Itens não perecíveis como arroz, feijão, macarrão, café, farinhas e temperos.");
        saveIfAbsent("Mercearia Líquida", "Bebidas como águas, refrigerantes, sucos, cervejas e vinhos.");
        saveIfAbsent("Matinais e Doces", "Achocolatados, cereais, biscoitos e geleias.");
        saveIfAbsent("Limpeza e Lavanderia", "Sabão em pó, desinfetantes, esponjas e detergentes.");
        saveIfAbsent("Higiene e Beleza", "Sabonetes, xampus, papel higiênico e cosméticos.");
        saveIfAbsent("Utilidades Domésticas", "Plásticos, panelas, lâmpadas e itens de organização.");
        saveIfAbsent("Pet Shop", "Ração, petiscos, areia e produtos de higiene para animais.");
    }

    private void saveIfAbsent(String name, String description) {
        if (categoryRepository.findByName(name).isEmpty()) {
            categoryRepository.save(new Category(name, description));
        }
    }
}
