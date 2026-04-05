# Égide.IA

Uma plataforma de compliance que utiliza Inteligência Artificial (IA) para interceptar e anonimizar automaticamente informações identificáveis em denúncias, garantindo o anonimato real do denunciante e a imparcialidade do ouvidor.

Projeto desenvolvido para a disciplina de Projeto Detalhado de Software disciplina do bacharelado em Tecnologia da Informação com enfase em Desenvolvimento de Software da UFRN/IMD.

## Tecnologias utilizadas

- **Java 21**
- **Spring Boot 4.0.4**
- **Spring Data JPA** (Persistência de dados)
- **Spring Web** (API RESTful)
- **Spring Validation** (Validação de dados)
- **Spring Security** (Segurança e autenticação)
- **Flyway** (Migrações de banco de dados)
- **PostgreSQL** (Banco de dados)
- **SpringDoc OpenAPI (Swagger)** (Documentação da API)
- **Lombok** (Produtividade no desenvolvimento)
- **Docker & Docker Compose** (Containerização do banco de dados)

## Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina as seguintes ferramentas:
- [Java 25](https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/)

## Como executar a aplicação

### 1. Banco de dados (Docker)

A aplicação utiliza o PostgreSQL. Você pode subir o banco de dados facilmente usando o Docker Compose:

```powershell
docker-compose up postgres -d
```

O banco de dados estará disponível na porta `5434` (conforme configurado no `docker-compose.yaml`).

### 2. Configuração do banco

Verifique o arquivo `src/main/resources/application.properties` para garantir que as credenciais do banco coincidam com o seu ambiente local ou com o container Docker.

### 3. Executar a aplicação

Para iniciar o projeto via Maven:

```powershell
./mvnw spring-boot:run
```

A aplicação iniciará por padrão na porta **8081** com o context-path **/api**.

## Equipe

- Francisca Gabrielly Lopes Freire [(gabrielly-freire)](https://github.com/gabrielly-freire)
- Gabriel Ribeiro Barbosa da Silva [(gabriel-ribeiro-099)](https://github.com/gabriel-ribeiro-099)
- Nicole Carvalho Nogueira [(nicolecnogueira)](https://github.com/nicolecnogueira)
