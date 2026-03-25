# 🏗️ Asset Tracker - Sistema de Gerenciamento de Ativos

![Java](https://img.shields.io/badge/java-17+-blue)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.2.4-brightgreen)
![Database](https://img.shields.io/badge/database-postgresql-336791)

## 📋 Visão Geral

Asset Tracker é uma API REST desenvolvida em **Spring Boot 3.2.4** para gerenciamento centralizado de ativos e funcionários de uma organização. O sistema permite operações CRUD completas, com suporte a paginação, validações e tratamento padronizado de erros.

### Tecnologias
- **Backend:** Java 17, Spring Boot 3.2.4, Spring Data JPA
- **Database:** PostgreSQL com Flyway para migrações
- **Build:** Maven 3.8+

### Arquitetura
O projeto segue uma arquitetura em camadas (Entity → Repository → Controller), utilizando DTOs (Request/Response) para separação clara entre domínio e API, e um GlobalExceptionHandler para respostas de erro padronizadas.

---

## 🚀 Quick Start

### Pré-requisitos
- Java 17 ou superior
- PostgreSQL 12+
- Maven 3.8+ (ou usar o Maven Wrapper fornecido)

### Configuração
1. Configure as variáveis de ambiente para segurança:
   ```bash
   DB_URL=jdbc:postgresql://localhost:5432/asset_db
   DB_USERNAME=seu_usuario
   DB_PASSWORD=sua_senha_segura
   ```

2. Compile e execute:
   ```bash
   .\mvnw clean compile
   .\mvnw spring-boot:run
   ```

---

## 📚 Endpoints da API

### Funcionários
- **GET** `/api/funcionarios` - Listar todos os funcionários
- **POST** `/api/funcionarios` - Criar novo funcionário
- **DELETE** `/api/funcionarios/{id}` - Excluir funcionário (se não houver ativos vinculados)

### Ativos
- **GET** `/api/assets` - Listar ativos com paginação (ex: `?page=0&size=10&sort=nome,asc`)
- **POST** `/api/assets` - Criar novo ativo
- **PATCH** `/api/assets/{id}/devolucao` - Devolver ativo (remove responsável e define status como DISPONIVEL)

---

## 🔐 Segurança
O projeto utiliza variáveis de ambiente para proteger credenciais sensíveis. Nunca faça commit de senhas no código. Configure `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` no ambiente de execução.

---

## 📊 Estrutura do Projeto

```
asset-tracker/
├── src/
│   ├── main/
│   │   ├── java/com/nicolas/assettracker/
│   │   │   ├── AssetTrackerApplication.java
│   │   │   ├── api/
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AssetController.java
│   │   │   │   │   └── FuncionarioController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── AssetRequestDTO.java
│   │   │   │   │   ├── AssetResponseDTO.java
│   │   │   │   │   ├── FuncionarioRequestDTO.java
│   │   │   │   │   └── FuncionarioResponseDTO.java
│   │   │   │   └── exception/
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   └── domain/
│   │   │       ├── entity/
│   │   │       │   ├── Asset.java
│   │   │       │   └── Funcionario.java
│   │   │       └── repository/
│   │   │           ├── AssetRepository.java
│   │   │           └── FuncionarioRepository.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── db/migration/
│   │           ├── V1__create_table_assets.sql
│   │           ├── V2__create_table_funcionarios.sql
│   │           └── V3__add_funcionario_to_assets.sql
│   └── test/
│       └── java/.../AssetTrackerApplicationTests.java
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## 🔨 Build e Execução

### Compilar
```bash
.\mvnw clean compile
```

### Executar
```bash
.\mvnw spring-boot:run
```

### Testes
```bash
.\mvnw test
```

### Build Executável
```bash
.\mvnw clean package
java -jar target/asset-tracker-0.0.1-SNAPSHOT.jar
```

---

## 🐛 Troubleshooting

### Conexão com PostgreSQL
- Verifique se o PostgreSQL está rodando e acessível na porta 5432.
- Certifique-se de que o banco `asset_db` existe.

### Variáveis de Ambiente
- Configure todas as variáveis necessárias (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).
- Reinicie o terminal/IDE após configurar.

### Porta 8080 em Uso
- Altere a porta em `application.yaml` se necessário: `server.port=8081`

---

## 🤝 Contribuindo

1. Crie uma branch para sua feature: `git checkout -b feature/minha-feature`
2. Commit suas mudanças: `git commit -am 'Adiciona nova feature'`
3. Push para a branch: `git push origin feature/minha-feature`
4. Abra um Pull Request

---

## 👨‍💻 Autor

Este projeto foi desenvolvido por **Nicolas**.

---

## 📝 Licença

Este projeto é propriedade da organização e está sob desenvolvimento ativo.
