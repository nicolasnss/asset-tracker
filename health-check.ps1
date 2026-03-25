# Health Check Script - Asset Tracker
# ===================================
#
# Este script valida se o ambiente está corretamente configurado
# para executar o projeto Asset Tracker.
#
# Uso: .\health-check.ps1

Write-Host "╔════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Asset Tracker - Validation Health Check  ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

$checksPass = 0
$checksFail = 0

# ========================================
# 1. Verificar Java
# ========================================
Write-Host "[1] Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String -Pattern "version"
    if ($javaVersion) {
        Write-Host "    ✅ Java instalado: $javaVersion" -ForegroundColor Green
        $checksPass++
    }
} catch {
    Write-Host "    ❌ Java não encontrado" -ForegroundColor Red
    Write-Host "    📥 Instale Java 17+: https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Red
    $checksFail++
}

# ========================================
# 2. Verificar Maven Wrapper
# ========================================
Write-Host "[2] Verificando Maven Wrapper..." -ForegroundColor Yellow
if ((Test-Path ".\mvnw") -or (Test-Path ".\mvnw.cmd")) {
    Write-Host "    ✅ Maven Wrapper encontrado" -ForegroundColor Green
    $checksPass++
} else {
    Write-Host "    ❌ Maven Wrapper não encontrado" -ForegroundColor Red
    $checksFail++
}

# ========================================
# 3. Verificar PostgreSQL
# ========================================
Write-Host "[3] Verificando PostgreSQL..." -ForegroundColor Yellow
try {
    $pgVersion = psql --version 2>&1
    if ($pgVersion -match "psql") {
        Write-Host "    ✅ PostgreSQL CLI instalado: $pgVersion" -ForegroundColor Green
        $checksPass++
    }
} catch {
    Write-Host "    ⚠️  PostgreSQL CLI não encontrado" -ForegroundColor Yellow
    Write-Host "    💡 (Você ainda pode usar Docker para PostgreSQL)" -ForegroundColor Gray
}

# ========================================
# 4. Verificar Variáveis de Ambiente
# ========================================
Write-Host "[4] Verificando Variáveis de Ambiente..." -ForegroundColor Yellow

$envVars = @("DB_URL", "DB_USERNAME", "DB_PASSWORD")
$envVarsMissing = @()

foreach ($var in $envVars) {
    $value = [Environment]::GetEnvironmentVariable($var, "User")
    if ($value) {
        $displayValue = if ($var -eq "DB_PASSWORD") { "****" } else { $value }
        Write-Host "    ✅ $var = $displayValue" -ForegroundColor Green
        $checksPass++
    } else {
        Write-Host "    ❌ $var não configurada" -ForegroundColor Red
        $envVarsMissing += $var
        $checksFail++
    }
}

if ($envVarsMissing.Count -gt 0) {
    Write-Host ""
    Write-Host "    💡 Execute o setup:" -ForegroundColor Yellow
    Write-Host "       .\setup-env.ps1" -ForegroundColor Gray
    Write-Host ""
}

# ========================================
# 5. Verificar Arquivos Críticos
# ========================================
Write-Host "[5] Verificando Arquivos Críticos..." -ForegroundColor Yellow

$criticalFiles = @(
    "pom.xml",
    "src/main/java/com/nicolas/assettracker/AssetTrackerApplication.java",
    "src/main/resources/application.yaml",
    "src/main/resources/db/migration/V1__create_table_assets.sql"
)

foreach ($file in $criticalFiles) {
    if (Test-Path $file) {
        Write-Host "    ✅ $file" -ForegroundColor Green
        $checksPass++
    } else {
        Write-Host "    ❌ $file não encontrado" -ForegroundColor Red
        $checksFail++
    }
}

# ========================================
# 6. Verificar Conectividade com PostgreSQL
# ========================================
Write-Host "[6] Verificando Conexão com PostgreSQL..." -ForegroundColor Yellow

$dbUrl = [Environment]::GetEnvironmentVariable("DB_URL", "User")
if ($dbUrl -match "localhost:(\d+)") {
    $port = $matches[1]
    $tcpClient = New-Object System.Net.Sockets.TcpClient
    try {
        $tcpClient.Connect("localhost", $port)
        if ($tcpClient.Connected) {
            Write-Host "    ✅ PostgreSQL acessível em localhost:$port" -ForegroundColor Green
            $checksPass++
        }
        $tcpClient.Close()
    } catch {
        Write-Host "    ⚠️  Não foi possível conectar a localhost:$port" -ForegroundColor Yellow
        Write-Host "    💡 Verifique se PostgreSQL está rodando" -ForegroundColor Gray
    }
} else {
    Write-Host "    ⚠️  DB_URL não configurada corretamente" -ForegroundColor Yellow
}

# ========================================
# 7. Verificar Compilação
# ========================================
Write-Host "[7] Testando Compilação Maven..." -ForegroundColor Yellow
try {
    $compileOutput = & .\mvnw -DskipTests -q compile 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "    ✅ Compilação bem-sucedida" -ForegroundColor Green
        $checksPass++
    } else {
        Write-Host "    ❌ Erro na compilação" -ForegroundColor Red
        Write-Host "       $compileOutput" -ForegroundColor Red
        $checksFail++
    }
} catch {
    Write-Host "    ⚠️  Não foi possível testar compilação" -ForegroundColor Yellow
}

# ========================================
# RELATÓRIO FINAL
# ========================================
Write-Host ""
Write-Host "╔════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║           RELATÓRIO FINAL                   ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

$total = $checksPass + $checksFail
$percentage = if ($total -gt 0) { [math]::Round(($checksPass / $total) * 100) } else { 0 }

Write-Host "✅ Verificações Passadas: $checksPass" -ForegroundColor Green
Write-Host "❌ Verificações Falhadas: $checksFail" -ForegroundColor Red
Write-Host "📊 Taxa de Sucesso: $percentage%" -ForegroundColor Cyan
Write-Host ""

if ($checksFail -eq 0) {
    Write-Host "🎉 Ambiente configurado corretamente!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos passos:" -ForegroundColor Cyan
    Write-Host "  1. .\mvnw spring-boot:run" -ForegroundColor Gray
    Write-Host "  2. Acesse http://localhost:8080/api/assets" -ForegroundColor Gray
} else {
    Write-Host "⚠️  Existem problemas que precisam ser resolvidos." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Sugestões:" -ForegroundColor Cyan
    Write-Host "  1. Execute .\setup-env.ps1 para configurar variáveis de ambiente" -ForegroundColor Gray
    Write-Host "  2. Verifique se PostgreSQL está instalado e rodando" -ForegroundColor Gray
    Write-Host "  3. Consulte HEALTH_CHECK_REPORT.md para mais detalhes" -ForegroundColor Gray
}

Write-Host ""
Write-Host "════════════════════════════════════════════" -ForegroundColor Cyan

