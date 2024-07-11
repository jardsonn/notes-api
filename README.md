# API de Bloco de Notas com Autenticação

Este projeto é uma API de bloco de notas desenvolvida em Kotlin utilizando o framework Ktor. A API permite a criação, leitura, atualização e exclusão de notas, com funcionalidades de autenticação para garantir a segurança dos dados dos usuários. O [OnNotes](https://github.com/jardsonn/OnNotes) é um aplicativo Android que utiliza esta API como backend para gerenciar notas de forma eficiente e segura.

## Funcionalidades

- **Cadastro de Usuário**: Permite a criação de novos usuários.
- **Autenticação**: Gera tokens JWT para autenticação dos usuários.
- **Criação de Notas**: Permite a criação de novas notas.
- **Leitura de Notas**: Permite a leitura de notas existentes.
- **Atualização de Notas**: Permite a atualização de notas existentes.
- **Exclusão de Notas**: Permite a exclusão de notas existentes.

## Tecnologias Utilizadas

- **Kotlin**: Linguagem de programação utilizada.
- **Ktor**: Framework para construção da API.
- **JWT**: JSON Web Token para autenticação.
- **Exposed**: Biblioteca ORM para interação com o banco de dados.
- **PostgreSQL**: Banco de dados relacional.
- **Flyway**: Ferramenta para migração de banco de dados.
- **Dotenv**: Biblioteca para gerenciar variáveis de ambiente.
- **Logback**: Biblioteca de logging.

## Endpoints

### Autenticação

- **Registrar Usuário**: `POST /signup`
    ```json
    {
        "name": "Seu Nome",
        "email": "seu-email@example.com",
        "password": "sua-senha"
    }
    ```

- **Login**: `POST /signin`
    ```json
    {
        "email": "seu-email@example.com",
        "password": "sua-senha"
    }
    ```

### Notas

- **Criar Nota**: `POST /note`
    ```json
    {
        "title": "Título da Nota",
        "content": "Conteúdo da Nota"
    }
    ```
    - Headers: `Authorization: Bearer {token}`

- **Listar Notas**: `GET /notes`
    - Headers: `Authorization: Bearer {token}`

- **Obter Nota por ID**: `GET /note/{id}`
    - Headers: `Authorization: Bearer {token}`

- **Atualizar Nota**: `PUT /note/{id}`
    ```json
    {
        "title": "Novo Título",
        "content": "Novo Conteúdo"
    }
    ```
    - Headers: `Authorization: Bearer {token}`

- **Deletar Nota**: `DELETE /note/{id}`
    - Headers: `Authorization: Bearer {token}`

### Outros Endpoints

- **Autenticar Token**: `GET /authenticate`
    - Headers: `Authorization: Bearer {token}`
