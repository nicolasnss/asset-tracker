# 🆘 GUIA DE TROUBLESHOOTING
## Asset Tracker - Problemas Comuns e Soluções

---

## 🔴 PROBLEMAS CRÍTICOS

### Erro 1: "Connection to localhost:5432 refused"

**Mensagem Completa:**
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused.
Check that the hostname and port are correct and that the post accepts TCP/IP connections.
```

**Causas Possíveis:**
1. PostgreSQL não está instalado
2. PostgreSQL não está rodando
3. PostgreSQL está em porta diferente
4. Firewall bloqueando conexão

**Soluções:**

#### Solução 1: Iniciar PostgreSQL (Windows)
```powershell
# Verificar se serviço está rodando
Get-Service postgresql* | Select-Object Name, Status

# Se não estiver rodando, iniciar
net start postgresql-x64-15

# Verificar se funcionou
psql -U postgres -h localhost
```

#### Solução 2: Usar Docker (Recomendado)
```bash
# Instalar Docker (se não tiver)
# https://www.docker.com/products/docker-desktop

# Iniciar PostgreSQL em container
docker run -d \
  --name asset-tracker-db \
  -e POSTGRES_PASSWORD=nico10 \
  -p 5432:5432 \
  postgres:15-alpine

# Verificar se está rodando
docker ps | findstr postgres
```

#### Solução 3: Alterar conexão para banco diferente
```yaml
# application.yaml
datasource:
  url: jdbc:postgresql://seu_servidor:5432/asset_db
  username: seu_usuario
  password: ${DB_PASSWORD}
```

---

### Erro 2: "FATAL: database 'asset_db' does not exist"

**Mensagem Completa:**
```
FATAL: database "asset_db" does not exist
```

**Causa:** Banco de dados ainda não foi criado

**Soluções:**

#### Solução 1: Criar via psql
```bash
# Conectar ao PostgreSQL
psql -U postgres

# Dentro do psql:
CREATE DATABASE asset_db;
\q

# Ou em uma linha:
psql -U postgres -c "CREATE DATABASE asset_db;"
```

#### Solução 2: Criar via script PowerShell
```powershell
$env:PGPASSWORD = "sua_senha_postgres"
psql -h localhost -U postgres -c "CREATE DATABASE asset_db;"
```

#### Solução 3: Executar setup automático
```bash
.\setup-env.ps1
# O script cria o banco automaticamente
```

---

### Erro 3: "FATAL: password authentication failed for user 'postgres'"

**Mensagem Completa:**
```
FATAL: password authentication failed for user "postgres"
```

**Causa:** Senha do PostgreSQL incorreta

**Soluções:**

#### Solução 1: Verificar senha padrão
```bash
# Tentar senha padrão do PostgreSQL
psql -U postgres -h localhost
# Quando pedir senha, deixar vazio ou usar "postgres"
```

#### Solução 2: Resetar senha PostgreSQL
```bash
# Se você é admin do SO, pode resetar via pgAdmin
# Abra C:\Program Files\PostgreSQL\15\bin\pgAdmin

# Ou via linha de comando (Windows)
cd "C:\Program Files\PostgreSQL\15\bin"
psql -U postgres

# Dentro do psql:
ALTER USER postgres WITH PASSWORD 'nova_senha';
\q
```

#### Solução 3: Atualizar variável de ambiente
```powershell
# Atualizar a senha
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "sua_senha_real", "User")

# Fechar e reabrir terminal
```

---

### Erro 4: "Unable to resolve placeholder 'DB_PASSWORD'"

**Mensagem Completa:**
```
Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'DB_PASSWORD' in value "${DB_PASSWORD}"
```

**Causa:** Variável de ambiente não definida

**Soluções:**

#### Solução 1: Definir variável de ambiente (Windows PowerShell)
```powershell
# Definir permanentemente (ao usuário)
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "nico10", "User")

# Verificar se foi definida
[Environment]::GetEnvironmentVariable("DB_PASSWORD", "User")

# IMPORTANTE: Fechar e reabrir PowerShell/IDE para aplicar
```

#### Solução 2: Definir para sessão atual apenas
```powershell
# Apenas para sessão atual (não persiste ao fechar)
$env:DB_PASSWORD = "nico10"

# Verificar
echo $env:DB_PASSWORD
```

#### Solução 3: Usar valores padrão em desenvolvimento
```yaml
# application.yaml
datasource:
  url: ${DB_URL:jdbc:postgresql://localhost:5432/asset_db}
  username: ${DB_USERNAME:postgres}
  password: ${DB_PASSWORD:nico10}
```

#### Solução 4: Executar setup automático
```bash
.\setup-env.ps1
```

---

### Erro 5: "Port 8080 already in use"

**Mensagem Completa:**
```
ERROR in app - tomcat.Server: Address already in use: bind
```

**Causa:** Outra aplicação ou instância anterior está usando porta 8080

**Soluções:**

#### Solução 1: Encontrar e matar processo
```powershell
# Encontrar processo usando porta 8080
netstat -ano | findstr :8080

# Resultado esperado:
# TCP    127.0.0.1:8080    0.0.0.0:0    LISTENING    1234

# Matar processo (PID 1234 no exemplo)
taskkill /PID 1234 /F

# Tentar novamente
.\mvnw spring-boot:run
```

#### Solução 2: Usar porta diferente
```yaml
# application.yaml
server:
  port: 8081

# Ou variável de ambiente
server:
  port: ${SERVER_PORT:8080}
```

```powershell
# Via variável de ambiente
[Environment]::SetEnvironmentVariable("SERVER_PORT", "8081", "User")
```

#### Solução 3: Reiniciar máquina
```powershell
# Último recurso
Restart-Computer
```

---

## 🟡 PROBLEMAS MODERADOS

### Erro 6: "Compilation failed"

**Mensagem Completa:**
```
[ERROR] BUILD FAILURE
[ERROR] COMPILATION ERROR
```

**Causas Possíveis:**
1. Java version incompatível
2. Dependências não baixadas
3. Erro de sintaxe no código

**Soluções:**

#### Solução 1: Limpar e recompilar
```bash
# Limpar cache Maven
.\mvnw clean

# Recompilar
.\mvnw compile

# Se persistir:
.\mvnw clean compile -X
# -X mostra mais detalhes do erro
```

#### Solução 2: Verificar versão Java
```bash
java -version
# Deve ser Java 17 ou superior
```

#### Solução 3: Atualizar dependências
```bash
.\mvnw dependency:resolve
```

---

### Erro 7: "Flyway migration failed"

**Mensagem Completa:**
```
ERROR: Flyway migration failed
ERROR: Cannot acquire lock for V1__create_table_assets.sql
```

**Causas Possíveis:**
1. Banco de dados não existe
2. Histórico de migrações corrompido
3. Permissões de acesso insuficientes

**Soluções:**

#### Solução 1: Verificar banco e tabela
```bash
# Conectar ao PostgreSQL
psql -U postgres -d asset_db

# Verificar tabelas
\dt

# Verificar histórico Flyway
SELECT * FROM flyway_schema_history;

# Sair
\q
```

#### Solução 2: Recriar banco (desenvolvimento apenas!)
```bash
# AVISO: Isso deleta TODOS os dados!
psql -U postgres -c "DROP DATABASE IF EXISTS asset_db;"
psql -U postgres -c "CREATE DATABASE asset_db;"

# Reexecutar aplicação para refazer migração
.\mvnw spring-boot:run
```

#### Solução 3: Limpar histórico Flyway (desenvolvimento apenas!)
```bash
psql -U postgres -d asset_db

# Limpar histórico
TRUNCATE TABLE flyway_schema_history;

# Sair e reexecutar
\q
.\mvnw spring-boot:run
```

---

### Erro 8: "Cannot find a match for ${DB_URL}"

**Mensagem Completa:**
```
[ERROR] Unable to find [version] for [com....]
[ERROR] Could not transfer artifact
```

**Causa:** Problema ao baixar dependências

**Soluções:**

#### Solução 1: Limpar cache Maven
```bash
# Remover cache
rmdir -Force -Recurse "$env:USERPROFILE\.m2"

# Recompilar (vai redownload)
.\mvnw clean compile
```

#### Solução 2: Usar repositório diferente
```xml
<!-- Adicionar ao pom.xml -->
<repositories>
    <repository>
        <id>maven-central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
</repositories>
```

#### Solução 3: Verificar conexão internet
```bash
ping repo.maven.apache.org
```

---

## 🟢 PROBLEMAS LEVES

### Erro 9: "Hibernate show_sql is verbose"

**Não é erro, mas muito output**

**Solução:** Desabilitar em application.yaml
```yaml
jpa:
  show-sql: false  # ou ${HIBERNATE_SHOW_SQL:false}
```

---

### Erro 10: "Devtools not available"

**Aviso:** Spring Boot Devtools não carregado

**Solução:** Adicionar ao pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

---

## 📋 CHECKLIST RÁPIDO DE TROUBLESHOOTING

### [ ] Passo 1: Verificar Ambiente
```bash
java -version              # Java 17+
psql --version             # PostgreSQL
.\mvnw -v                  # Maven
echo $env:DB_PASSWORD      # Variáveis de ambiente
```

### [ ] Passo 2: Verificar Serviços
```bash
Get-Service postgresql*    # PostgreSQL rodando?
netstat -ano | findstr :5432  # Porta 5432 aberta?
netstat -ano | findstr :8080  # Porta 8080 livre?
```

### [ ] Passo 3: Executar Scripts
```bash
.\health-check.ps1         # Validar ambiente
.\setup-env.ps1            # Configurar de novo (se necessário)
```

### [ ] Passo 4: Recompilar
```bash
.\mvnw clean compile       # Limpeza e compilação
```

### [ ] Passo 5: Executar
```bash
.\mvnw spring-boot:run
```

---

## 🎯 FLUXO DE RESOLUÇÃO RECOMENDADO

```
┌─ Erro de conexão ao banco?
├─ SIM → Verificar PostgreSQL rodando → Iniciar serviço
├─ NÃO ↓
├─ Variável de ambiente não setada?
├─ SIM → Executar setup-env.ps1
├─ NÃO ↓
├─ Compilação falhando?
├─ SIM → mvnw clean compile
├─ NÃO ↓
├─ Porta 8080 em uso?
├─ SIM → Mudar porta ou matar processo
├─ NÃO ↓
└─ ✅ Aplicação rodando!
```

---

## 📞 ESCALAÇÃO DE SUPORTE

### Nível 1: Self-Help (Você)
- Executar health-check.ps1
- Consultar este documento
- Verificar logs da aplicação

### Nível 2: Team Lead
- Problemas de compilação persistentes
- Questões de segurança/credenciais
- Problemas de ambiente corporativo

### Nível 3: DevOps/DBA
- Problemas de performance PostgreSQL
- Replicação/backup de banco
- Infraestrutura

---

## 💡 DICAS E TRICKS

### Aumentar verbosidade de logs
```xml
<!-- Adicionar ao pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
</dependency>
```

```yaml
# application.yaml
logging:
  level:
    root: INFO
    com.nicolas: DEBUG
    org.springframework: DEBUG
    org.hibernate: DEBUG
```

### Acessar banco via psql rapidamente
```bash
# Criar alias (adicionar ao seu perfil PowerShell)
function psql-asset { psql -U postgres -d asset_db }

# Usar
psql-asset
```

### Monitorar logs em tempo real
```bash
# Redirecionar logs para arquivo
.\mvnw spring-boot:run > app.log 2>&1

# Monitorar em outra janela
Get-Content app.log -Wait -Tail 20
```

---

**Última atualização:** 24 de Março de 2026  
**Mantido por:** GitHub Copilot (Senior Developer)

