# 🛒 Lista de Compras - Backend

Este repositório contém o backend da aplicação **Lista de Compras**, uma API REST desenvolvida em Java com Spring Boot. A API gerencia listas de compras, controle de itens, autenticação baseada em JWT e inclui uma integração com Inteligência Artificial para gerar listas de compras completas a partir de receitas fornecidas em linguagem natural.

---

## 🚀 Funcionalidades Principais

*   **Autenticação Segura (JWT):** Login e renovação de sessão via Access Token e Refresh Token.
*   **Gestão de Listas de Compras:** Criação, edição, visualização e remoção de listas de compras com orçamento planejado (*budget*).
*   **Controle de Itens:** Inserção, edição e exclusão de itens dentro de cada lista, com controle de quantidade, unidade de medida, preço estimado e marcação de status (comprado ou não).
*   **Assistente de Receitas com IA:** Integração com LLM (via API compatível com OpenAI, ex: Groq/Llama) para extrair ingredientes e quantidades de qualquer receita e transformá-los automaticamente em uma lista estruturada de compras.
*   **Documentação Interativa:** API totalmente documentada via Swagger UI e OpenAPI 3.

---

## 🔐 Credenciais de Teste

O banco de dados PostgreSQL local é semeado (*seeded*) automaticamente na primeira execução com um usuário administrador padrão:

*   **E-mail:** `admin@gmail.com`
*   **Senha:** `123456`

---

## 🛠️ Tecnologias Utilizadas (Ecosistema Global)

O ecossistema do projeto é baseado em tecnologias modernas e robustas:

### Backend
*   **Java 21:** Recursos modernos de linguagem e melhor desempenho.
*   **Spring Boot 3.4.0:** Base do backend, utilizando:
    *   *Spring Web* para os endpoints REST.
    *   *Spring Security* para o controle de acesso e autenticação JWT.
    *   *Spring Data JPA* para abstração da camada de persistência.
*   **PostgreSQL:** Banco de dados relacional robusto e escalável para persistência em produção.
*   **PostgreSQL JDBC Driver:** Para conexão e comunicação com o banco de dados.
*   **JJWT (io.jsonwebtoken v0.12.6):** Biblioteca moderna para geração e validação de tokens JWT (HS256).
*   **Springdoc OpenAPI UI (v2.5.0):** Para geração e visualização automatizada da documentação interativa no Swagger.
*   **LLM AI Integration:** Conectividade com provedores compatíveis com a API OpenAI (ex: Groq/Llama 3.1) para conversão inteligente de receitas em linguagem natural em itens estruturados de compras.

### Frontend (Flutter App)
*   **Provider** para gerência reativa do estado global.
*   **Chopper & OpenAPI Client Generator** para geração de código de API.
*   **flutter_secure_storage** para persistência encriptada de tokens JWT de forma segura no dispositivo.
*   **fl_chart** para gráficos de pizza responsivos que ilustram a conclusão das compras.
*   **share_plus** para formatação de texto e envio de listas através dos canais nativos do dispositivo.

---

## ⚙️ Configuração e Instalação

### Pré-requisitos
Para executar o backend localmente, certifique-se de ter instalado:
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
> O backend suporta qualquer provedor com API compatível com o formato OpenAI (como Groq, OpenRouter ou a própria OpenAI).

### 2. Configurações de Banco de Dados e Porta
A aplicação já vem pré-configurada para rodar na porta `8090` e utilizar o PostgreSQL local (`localhost:5432/lista_compras` com as credenciais padrão do `postgres`). Certifique-se de ter o banco criado. Essas definições estão localizadas em:
`src/main/resources/application.properties`

### 3. Compilar e Rodar o Backend
Execute o comando correspondente à sua plataforma na pasta do backend:

**No Windows (PowerShell):**
```powershell
./gradlew bootRun
```

**No Linux/macOS:**
```bash
./gradlew bootRun
```

A API estará acessível em: `http://localhost:8090`
A documentação Swagger estará disponível em: **[http://localhost:8090/swagger-ui/index.html](http://localhost:8090/swagger-ui/index.html)**

---

## 📲 Como Executar o Frontend (Flutter)
Para rodar a interface do aplicativo e conectá-la ao servidor local:

1.  Navegue até a pasta do aplicativo Flutter:
    ```bash
    cd C:\Workspace\Projetos\lista-compras
    ```
2.  Instale os pacotes de dependências:
    ```bash
    flutter pub get
    ```
3.  Execute a aplicação no emulador ou navegador de preferência:
    ```bash
    flutter run
    ```
