# 🏗️ Asset Tracker - Sistema de Gerenciamento de Ativos

![Status](https://img.shields.io/badge/status-healthy-brightgreen)
![Java](https://img.shields.io/badge/java-17+-blue)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.2.4-brightgreen)
![Database](https://img.shields.io/badge/database-postgresql-336791)

## 📋 Visão Geral

Asset Tracker é uma API REST desenvolvida em **Spring Boot 3.2.4** para gerenciamento centralizado de ativos de uma organização. O sistema permite criar, ler, atualizar e deletar registros de ativos, com suporte a busca por status e paginação.

### Tecnologias Stack
- **Backend:** Java 17, Spring Boot 3.2.4, Spring Data JPA
- **Database:** PostgreSQL com Flyway para migrações
- **Build:** Maven 3.8+
- **Arquitetura:** REST API com camadas (Entity → Repository → Controller)

---

## 🚀 Quick Start

### Pré-requisitos
- Java 17 ou superior
- PostgreSQL 12+
- Maven 3.8+ (ou usar o Maven Wrapper fornecido)
- Windows 10+ (para scripts de setup)

### Setup Automático (Recomendado)

```powershell
# 1. Execute o script de setup (Windows PowerShell)
.\setup-env.ps1

# 2. Feche e reabra o PowerShell/IDE para carregar variáveis de ambiente

# 3. Compile e execute
.\mvnw clean compile
.\mvnw spring-boot:run
```

### Setup Manual

```bash
# 1. Criar banco de dados PostgreSQL
psql -U postgres -c "CREATE DATABASE asset_db;"

# 2. Configurar variáveis de ambiente (Windows)
[Environment]::SetEnvironmentVariable("DB_URL", "jdbc:postgresql://localhost:5432/asset_db", "User")
[Environment]::SetEnvironmentVariable("DB_USERNAME", "postgres", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "sua_senha", "User")

# 3. Compilar o projeto
.\mvnw clean compile

# 4. Executar a aplicação
.\mvnw spring-boot:run
```

---

## 📚 Endpoints da API

### GET - Listar Assets com Paginação
```bash
curl "http://localhost:8080/api/assets?page=0&size=10&sort=nome,asc"
```

**Resposta (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "tagPatrimonio": "TAG001",
      "nome": "Notebook Dell",
      "tipo": "Eletrônico",
      "status": "Ativo",
      "dataAquisicao": "2024-01-15",
      "descricao": "Notebook para desenvolvimento",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10
}
```

---

### POST - Criar Novo Asset
```bash
curl -X POST "http://localhost:8080/api/assets" \
  -H "Content-Type: application/json" \
  -d '{
    "tagPatrimonio": "TAG001",
    "nome": "Notebook Dell",
    "tipo": "Eletrônico",
    "status": "Ativo",
    "dataAquisicao": "2024-01-15",
    "descricao": "Notebook para desenvolvimento"
  }'
```

**Resposta (201 CREATED):**
```json
{
  "id": 1,
  "tagPatrimonio": "TAG001",
  "nome": "Notebook Dell",
  "tipo": "Eletrônico",
  "status": "Ativo",
  "dataAquisicao": "2024-01-15",
  "descricao": "Notebook para desenvolvimento",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### GET - Buscar Asset por ID
```bash
curl "http://localhost:8080/api/assets/1"
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "tagPatrimonio": "TAG001",
  "nome": "Notebook Dell",
  "tipo": "Eletrônico",
  "status": "Ativo",
  "dataAquisicao": "2024-01-15",
  "descricao": "Notebook para desenvolvimento",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Se não encontrado (404 NOT FOUND):**
```json
(Resposta vazia)
```

---

### PUT - Atualizar Asset
```bash
curl -X PUT "http://localhost:8080/api/assets/1" \
  -H "Content-Type: application/json" \
  -d '{
    "tagPatrimonio": "TAG001",
    "nome": "Notebook Dell XPS",
    "tipo": "Eletrônico",
    "status": "Manutenção",
    "dataAquisicao": "2024-01-15",
    "descricao": "Notebook atualizado para manutenção"
  }'
```

**Resposta (200 OK):**
```json
{
  "id": 1,
  "tagPatrimonio": "TAG001",
  "nome": "Notebook Dell XPS",
  "tipo": "Eletrônico",
  "status": "Manutenção",
  "dataAquisicao": "2024-01-15",
  "descricao": "Notebook atualizado para manutenção",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T11:45:00"
}
```

---

### DELETE - Deletar Asset
```bash
curl -X DELETE "http://localhost:8080/api/assets/1"
```

**Resposta (204 NO CONTENT):**
```
(Sem corpo de resposta)
```

---

### GET - Filtrar por Status
```bash
curl "http://localhost:8080/api/assets/status/Ativo"
```

**Resposta (200 OK):**
```json
[
  {
    "id": 1,
    "tagPatrimonio": "TAG001",
    "nome": "Notebook Dell",
    "tipo": "Eletrônico",
    "status": "Ativo",
    "dataAquisicao": "2024-01-15",
    "descricao": "Notebook para desenvolvimento",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

## ❌ Tratamento de Erros

### Erro de Validação (400 BAD REQUEST)
```bash
curl -X POST "http://localhost:8080/api/assets" \
  -H "Content-Type: application/json" \
  -d '{"nome": "", "tipo": ""}' # Faltam campos obrigatórios
```

**Resposta (400):**
```json
{
  "tagPatrimonio": "O campo tagPatrimonio não pode ser vazio",
  "nome": "O campo nome não pode ser vazio",
  "tipo": "O campo tipo não pode ser vazio",
  "status": "O campo status não pode ser vazio"
}
```

### Recurso Não Encontrado (404 NOT FOUND)
```bash
curl "http://localhost:8080/api/assets/999"
```

**Resposta (404):**
```
(Sem corpo de resposta)
```

---

## 🗄️ Modelo de Dados

### Tabela: `assets`

| Campo | Tipo | Restrições | Descrição |
|-------|------|-----------|-----------|
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único |
| `tag_patrimonio` | VARCHAR(50) | NOT NULL, UNIQUE | Tag/código do patrimônio |
| `nome` | VARCHAR(255) | NOT NULL | Nome do ativo |
| `tipo` | VARCHAR(100) | NOT NULL | Tipo/categoria do ativo |
| `status` | VARCHAR(50) | NOT NULL | Status do ativo |
| `data_aquisicao` | DATE | NULLABLE | Data de aquisição |
| `descricao` | TEXT | NULLABLE | Descrição detalhada |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Timestamp de criação |
| `updated_at` | TIMESTAMP | DEFAULT NOW() | Timestamp de atualização |

### Índices
- `idx_assets_tag_patrimonio` - Busca rápida por tag
- `idx_assets_status` - Busca rápida por status

### Trigger
- `trigger_update_assets_updated_at` - Atualiza `updated_at` automaticamente em modificações

---

## 🔐 Segurança e Configuração

### Variáveis de Ambiente Obrigatórias

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/asset_db
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_segura

# JPA/Hibernate
HIBERNATE_SHOW_SQL=true
HIBERNATE_FORMAT_SQL=true

# Application
SPRING_PROFILES_ACTIVE=dev
```

### ⚠️ IMPORTANTE - Segurança de Credenciais

**NUNCA** faça commit de senhas em `application.yaml`:

```yaml
# ❌ NUNCA faça isso:
datasource:
  password: ${DB_PASSWORD:nico10}

# ✅ FAÇA ISSO em produção:
datasource:
  password: ${DB_PASSWORD}  # Sem valor padrão, força variável de ambiente
```

### Configuração por Perfil

```bash
# Desenvolvimento
SPRING_PROFILES_ACTIVE=dev
HIBERNATE_SHOW_SQL=true
HIBERNATE_FORMAT_SQL=true

# Produção
SPRING_PROFILES_ACTIVE=prod
HIBERNATE_SHOW_SQL=false
```

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
│   │   │   │   │   └── AssetController.java
│   │   │   │   └── exception/
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   └── domain/
│   │   │       ├── entity/
│   │   │       │   └── Asset.java
│   │   │       └── repository/
│   │   │           └── AssetRepository.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── db/migration/
│   │           └── V1__create_table_assets.sql
│   └── test/
│       └── java/.../AssetTrackerApplicationTests.java
├── pom.xml
├── mvnw / mvnw.cmd
├── HEALTH_CHECK_REPORT.md
├── README.md
└── setup-env.ps1
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

### Erro: "Connection to localhost:5432 refused"
```bash
# Verificar se PostgreSQL está rodando
psql -U postgres -h localhost

# Se não estiver, inicie o serviço:
net start postgresql-x64-15  # Windows

# Ou use Docker:
docker run -d --name postgres \
  -e POSTGRES_PASSWORD=nico10 \
  -p 5432:5432 \
  postgres:15-alpine
```

### Erro: "Database asset_db does not exist"
```bash
# Criar banco de dados
psql -U postgres -c "CREATE DATABASE asset_db;"
```

### Erro: "Variável de ambiente não definida"
```bash
# Windows PowerShell - Configurar variáveis
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "sua_senha", "User")

# Verificar se foi definida
[Environment]::GetEnvironmentVariable("DB_PASSWORD", "User")

# Fechar e reabrir PowerShell/IDE para aplicar
```

### Porta 8080 já em uso
```bash
# Encontrar processo usando a porta
netstat -ano | findstr :8080

# Matar o processo (ex: PID 1234)
taskkill /PID 1234 /F

# Ou alterar a porta em application.yaml
server.port=8081
```

---

## 📖 Documentação Adicional

- **[HEALTH_CHECK_REPORT.md](./HEALTH_CHECK_REPORT.md)** - Diagnóstico completo de integridade do projeto
- **[.env.example](./.env.example)** - Exemplo de arquivo de variáveis de ambiente

---

## 🤝 Contribuindo

1. Crie uma branch para sua feature: `git checkout -b feature/minha-feature`
2. Commit suas mudanças: `git commit -am 'Adiciona nova feature'`
3. Push para a branch: `git push origin feature/minha-feature`
4. Abra um Pull Request

---

## 📝 Licença

Este projeto é propriedade da organização e está sob desenvolvimento ativo.

---

## 👨‍💻 Desenvolvedor Sênior - Diagnóstico

**Data:** 24 de Março de 2026  
**Status:** ✅ Saudável  
**Próximos Passos:** Implementar Actuator, melhorar tratamento de exceções, adicionar testes

Para mais detalhes técnicos, consulte **HEALTH_CHECK_REPORT.md**

