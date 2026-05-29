# 🛒 Lista de Compras - Backend

Este repositório contém o backend da aplicação **Lista de Compras**, uma API REST desenvolvida em Java com Spring Boot. A API gerencia listas de compras, controle de itens, autenticação baseada em JWT e inclui uma integração inovadora com Inteligência Artificial para gerar listas de compras completas a partir de receitas fornecidas em linguagem natural.

---

## 🚀 Funcionalidades Principais

*   **Autenticação Segura (JWT):** Login e renovação de sessão via Access Token e Refresh Token.
*   **Gestão de Listas de Compras:** Criação, edição, visualização e remoção de listas de compras com orçamento planejado (*budget*).
*   **Controle de Itens:** Inserção, edição e exclusão de itens dentro de cada lista, com controle de quantidade, unidade de medida, preço estimado e marcação de status (comprado ou não).
*   **Assistente de Receitas com IA:** Integração com LLM (via API compatível com OpenAI, ex: Groq/Llama) para extrair ingredientes e quantidades de qualquer receita e transformá-los automaticamente em uma lista estruturada de compras.
*   **Documentação Interativa:** API totalmente documentada via Swagger UI e OpenAPI 3.

---

## 🛠️ Tecnologias Utilizadas

O ecossistema do projeto é baseado em tecnologias modernas e robustas para o ecossistema Java:

*   **Java 21:** Recursos modernos de linguagem e melhor desempenho.
*   **Spring Boot 3.4.0:** Base do backend, utilizando:
    *   *Spring Web* para os endpoints REST.
    *   *Spring Security* para o controle de acesso e autenticação.
    *   *Spring Data JPA* para abstração da camada de persistência.
*   **SQLite:** Banco de dados relacional local, leve e de configuração rápida.
*   **Hibernate Community Dialects (v6.4.4):** Para suporte nativo ao dialeto SQLite na JPA.
*   **JJWT (io.jsonwebtoken v0.12.6):** Biblioteca moderna para geração e validação de tokens JWT (HS256).
*   **Springdoc OpenAPI UI (v2.5.0):** Para geração e visualização automatizada da documentação interativa no Swagger.

---

## 📁 Estrutura de Pastas e Componentes

A arquitetura do backend segue uma estrutura organizada por camadas:

```
src/main/java/com/lista/compras/
├── controllers/      # Controladores REST que expõem os endpoints da API.
├── dto/              # Objetos de Transferência de Dados (AuthRequest, AuthResponse, etc.).
├── models/           # Entidades de domínio mapeadas para JPA (User, ShoppingList, ShoppingItem, UnitEnum).
├── repositories/     # Interfaces de repositório do Spring Data JPA.
├── security/         # Filtros, serviços e configurações do Spring Security e JWT.
└── services/         # Camada de lógica de negócio (ex: processamento de receitas por IA).
```

---

## ⚙️ Configuração e Instalação

### Pré-requisitos

Para executar este projeto localmente, certifique-se de ter instalado:
*   **Java Development Kit (JDK) 21**
*   **Gradle** (ou utilize o wrapper `./gradlew` incluso no projeto)

### 1. Clonar o repositório e preparar o ambiente
Crie um arquivo `.env` na raiz do diretório do backend (onde se encontra o arquivo `build.gradle`) com a configuração da API de IA desejada.

```env
AI_API_KEY=sua_chave_de_api_aqui
AI_API_URL=https://api.groq.com/openai/v1/chat/completions
AI_API_MODEL=llama-3.1-8b-instant
```

> [!NOTE]
> O backend suporta qualquer provedor com API compatível com o formato OpenAI (como Groq, OpenRouter ou o próprio OpenAI).

### 2. Configurações de Banco de Dados e Porta
A aplicação já vem pré-configurada para rodar na porta `8090` e utilizar o SQLite local (`lista-compras.db`). Essas definições estão localizadas em:
`src/main/resources/application.properties`

### 3. Compilar e Rodar o Projeto
Execute os comandos abaixo a partir da raiz do projeto:

**No Windows (PowerShell):**
```powershell
./gradlew bootRun
```

**No Linux/macOS:**
```bash
./gradlew bootRun
```

A API estará acessível em: `http://localhost:8090`

---

## 🔐 Autenticação (JWT)

A API protege todos os endpoints de gerenciamento de dados de forma padrão.
No primeiro início, a aplicação realiza a semeadura automática (*seeding*) de um usuário administrador padrão para testes:

*   **E-mail:** `admin@gmail.com`
*   **Senha:** `123456`

Para testar os endpoints protegidos, faça uma requisição POST para `/api/auth/login` para receber seu `accessToken` e adicione-o como cabeçalho `Authorization: Bearer <seu_token>` nas chamadas subsequentes.

---

## 📚 Documentação da API (Swagger / OpenAPI)

Com o servidor rodando, você pode acessar a documentação interativa e realizar testes diretos nos endpoints através do Swagger UI no seu navegador:

🔗 **[http://localhost:8090/swagger-ui/index.html](http://localhost:8090/swagger-ui/index.html)**

---

## 📡 Detalhamento dos Endpoints Principais

### 🔑 Autenticação

#### `POST /api/auth/login`
Autentica o usuário e gera os tokens de acesso.
*   **Body:**
    ```json
    {
      "email": "admin@gmail.com",
      "password": "123456"
    }
    ```
*   **Resposta (200 OK):**
    ```json
    {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
      "tokenType": "Bearer",
      "expiresIn": 900
    }
    ```

#### `POST /api/auth/refresh`
Gera novos pares de Access Token e Refresh Token sem exigir nova senha.
*   **Body:**
    ```json
    {
      "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
    }
    ```

---

### 📝 Listas de Compras (`Authorization` requerido)

#### `GET /api/lists`
Retorna todas as listas de compras cadastradas.

#### `POST /api/lists`
Cria uma nova lista de compras vazia.
*   **Body:**
    ```json
    {
      "name": "Supermercado Mensal",
      "budget": 500.0
    }
    ```

#### `GET /api/lists/{id}`
Busca detalhes de uma lista específica, incluindo seus itens.

#### `PUT /api/lists/{id}`
Atualiza o nome ou orçamento de uma lista de compras.

#### `DELETE /api/lists/{id}`
Exclui uma lista de compras e todos os seus itens associados.

---

### 🍎 Itens das Listas de Compras (`Authorization` requerido)

#### `POST /api/lists/{listId}/items`
Adiciona um novo item a uma lista.
*   **Body:**
    ```json
    {
      "description": "Leite Integral",
      "quantity": 3.0,
      "unit": "l",
      "price": 4.50,
      "isChecked": false
    }
    ```
    *(Unidades válidas: `und`, `g`, `kg`, `l`, `ml`)*

#### `PUT /api/lists/{listId}/items/{itemId}`
Atualiza as informações de um item específico (descrição, quantidade, preço ou marcar como comprado/não comprado).

#### `DELETE /api/lists/{listId}/items/{itemId}`
Remove um item específico de uma lista de compras.

---

### 🤖 Assistente de Receitas com IA (`Authorization` requerido)

#### `POST /api/ai/recipe-to-list`
Recebe o texto/receita em linguagem natural e gera dinamicamente uma nova lista com todos os ingredientes devidamente catalogados e com unidades estruturadas.
*   **Body:**
    ```json
    {
      "recipe": "Quero fazer um bolo de cenoura simples. Leva 3 cenouras médias, 4 ovos, 1 xícara de óleo de soja, 2 xícaras de açúcar, 2 xícaras de farinha de trigo e 1 colher de sopa de fermento em pó."
    }
    ```
*   **Resposta (200 OK):** Retorna a lista de compras gerada e já salva no banco de dados com seus itens.
    ```json
    {
      "id": "abc-123-xyz...",
      "name": "Bolo de Cenoura Simples",
      "budget": 0.0,
      "items": [
        { "description": "Cenoura média", "quantity": 3.0, "unit": "und", "price": 0.0, "isChecked": false },
        { "description": "Ovos", "quantity": 4.0, "unit": "und", "price": 0.0, "isChecked": false },
        { "description": "Óleo de soja", "quantity": 1.0, "unit": "und", "price": 0.0, "isChecked": false },
        ...
      ]
    }
    ```
