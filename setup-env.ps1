# Asset Tracker - Setup Script (Windows PowerShell)
# ================================================
#
# Este script configura automaticamente as variáveis de ambiente
# necessárias para executar o projeto em um novo ambiente local.
#
# Uso: .\setup-env.ps1
#
# Pré-requisitos:
# - Windows 10+
# - PowerShell 5.1+
# - PostgreSQL instalado e em execução
# - Java 17+
# - Maven 3.8+ (ou usar mvnw)

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "  Asset Tracker - Environment Setup Script" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar se PostgreSQL está rodando
Write-Host "[1/5] Verificando PostgreSQL..." -ForegroundColor Yellow
try {
    $pgStatus = Get-Service postgresql* -ErrorAction SilentlyContinue
    if ($pgStatus) {
        Write-Host "    ✅ PostgreSQL encontrado: $($pgStatus.Name)" -ForegroundColor Green
    } else {
        Write-Host "    ⚠️  PostgreSQL não encontrado. Iniciando..." -ForegroundColor Yellow
        # Tentar iniciar serviço PostgreSQL (se instalado)
        $pgService = Get-Service postgresql* -ErrorAction SilentlyContinue
        if ($pgService) {
            Start-Service $pgService.Name -ErrorAction SilentlyContinue
            Write-Host "    ✅ PostgreSQL iniciado" -ForegroundColor Green
        } else {
            Write-Host "    ❌ PostgreSQL não está instalado" -ForegroundColor Red
            Write-Host "    Instale PostgreSQL em: https://www.postgresql.org/download/windows/" -ForegroundColor Red
            exit 1
        }
    }
} catch {
    Write-Host "    ⚠️  Não foi possível verificar PostgreSQL automaticamente" -ForegroundColor Yellow
}

# 2. Verificar Java
Write-Host "[2/5] Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    if ($javaVersion -match "17|18|19|20|21") {
        Write-Host "    ✅ Java 17+ encontrado" -ForegroundColor Green
    } else {
        Write-Host "    ⚠️  Java versão pode estar desatualizada" -ForegroundColor Yellow
        Write-Host "    $javaVersion"
    }
} catch {
    Write-Host "    ❌ Java não encontrado no PATH" -ForegroundColor Red
    Write-Host "    Instale Java 17+: https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Red
    exit 1
}

# 3. Criar/atualizar banco de dados
Write-Host "[3/5] Configurando banco de dados..." -ForegroundColor Yellow

$dbHost = "localhost"
$dbPort = "5432"
$dbName = "asset_db"
$dbUser = "postgres"
$dbPassword = Read-Host "    Digite a senha do PostgreSQL (padrão: postgres)"
if ([string]::IsNullOrWhiteSpace($dbPassword)) {
    $dbPassword = "postgres"
}

try {
    $env:PGPASSWORD = $dbPassword
    # Verificar conexão
    $pingDb = & psql -h $dbHost -U $dbUser -l 2>&1
    if ($pingDb -match "List of databases") {
        Write-Host "    ✅ Conexão com PostgreSQL estabelecida" -ForegroundColor Green

        # Criar banco de dados se não existir
        & psql -h $dbHost -U $dbUser -tc "SELECT 1 FROM pg_database WHERE datname = '$dbName'" | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "    ✅ Banco de dados '$dbName' já existe" -ForegroundColor Green
        } else {
            Write-Host "    📝 Criando banco de dados '$dbName'..." -ForegroundColor Yellow
            & psql -h $dbHost -U $dbUser -c "CREATE DATABASE $dbName;" | Out-Null
            Write-Host "    ✅ Banco de dados criado" -ForegroundColor Green
        }
    } else {
        Write-Host "    ❌ Não foi possível conectar ao PostgreSQL" -ForegroundColor Red
        Write-Host "    Verifique se o PostgreSQL está rodando e a senha está correta" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "    ⚠️  Não foi possível criar banco automaticamente" -ForegroundColor Yellow
    Write-Host "    Por favor, execute manualmente:" -ForegroundColor Yellow
    Write-Host "    psql -U postgres -c `"CREATE DATABASE $dbName;`"" -ForegroundColor Gray
}

# 4. Configurar variáveis de ambiente do Windows
Write-Host "[4/5] Configurando variáveis de ambiente..." -ForegroundColor Yellow

$envVars = @{
    "DB_URL"                = "jdbc:postgresql://$($dbHost):$($dbPort)/$($dbName)"
    "DB_USERNAME"           = $dbUser
    "DB_PASSWORD"           = $dbPassword
    "HIBERNATE_SHOW_SQL"    = "true"
    "HIBERNATE_FORMAT_SQL"  = "true"
    "SPRING_PROFILES_ACTIVE" = "dev"
}

foreach ($key in $envVars.Keys) {
    [Environment]::SetEnvironmentVariable($key, $envVars[$key], "User")
    Write-Host "    ✅ $key = ****" -ForegroundColor Green
}

# 5. Validação e próximos passos
Write-Host "[5/5] Validando configuração..." -ForegroundColor Yellow

# Recarregar variáveis de ambiente do usuário
$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "User") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "Machine")

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "  ✅ Setup Concluído com Sucesso!" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📝 Próximas etapas:" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Feche e reabra o PowerShell/IDE para aplicar as variáveis de ambiente" -ForegroundColor White
Write-Host ""
Write-Host "2. Compile o projeto:" -ForegroundColor White
Write-Host "   .\mvnw clean compile" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Execute a aplicação:" -ForegroundColor White
Write-Host "   .\mvnw spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "4. Teste a API:" -ForegroundColor White
Write-Host "   curl http://localhost:8080/api/assets" -ForegroundColor Gray
Write-Host ""
Write-Host "5. Mais endpoints:" -ForegroundColor White
Write-Host "   GET    /api/assets                 - Listar assets" -ForegroundColor Gray
Write-Host "   POST   /api/assets                 - Criar asset" -ForegroundColor Gray
Write-Host "   GET    /api/assets/{id}            - Buscar por ID" -ForegroundColor Gray
Write-Host "   PUT    /api/assets/{id}            - Atualizar asset" -ForegroundColor Gray
Write-Host "   DELETE /api/assets/{id}            - Deletar asset" -ForegroundColor Gray
Write-Host "   GET    /api/assets/status/{status} - Filtrar por status" -ForegroundColor Gray
Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "ℹ️  Para mais informações, leia HEALTH_CHECK_REPORT.md" -ForegroundColor Yellow
Write-Host "===============================================" -ForegroundColor Cyan

