# PowerShell script pour démarrer localement les services (ouvre une fenêtre par service)
param(
  [string]$BasePath = "C:\Users\user\Desktop\SN\digitrans-cm"
)

Set-StrictMode -Version Latest

# Start Docker Compose
Write-Host "Démarrage de Docker Compose (MySQL + Redis + services Docker)..."
Push-Location $BasePath
docker-compose up -d
Start-Sleep -Seconds 5
Pop-Location

# Helper to open a new PowerShell window and run a service
function Start-ServiceWindow($servicePath, $port, $envVars) {
  $cmd = @"
cd '$servicePath'
`$env:DB_HOST='localhost'
`$env:DB_PORT='3306'
`$env:DB_NAME='digitrans_db'
`$env:DB_USERNAME='root'
`$env:DB_PASSWORD='root'
$envVars
mvn spring-boot:run
"@
  Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd
}

# Start ERP
Start-ServiceWindow "${BasePath}\erp-service" 8081 ""
Start-Sleep -Milliseconds 500
# Start Auth
Start-ServiceWindow "${BasePath}\auth-service" 8082 ""
Start-Sleep -Milliseconds 500
# Start Supply Chain
Start-ServiceWindow "${BasePath}\supply-chain-service" 8083 ""

Write-Host "Fenêtres de services démarrées. Vérifiez les logs dans chaque fenêtre."