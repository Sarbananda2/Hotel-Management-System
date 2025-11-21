# Hotel Management Platform - Desktop Application Launcher
# This script sets environment variables and launches the desktop GUI
# Usage: .\run-desktop.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Hotel Management Platform" -ForegroundColor Green
Write-Host "Starting Desktop Application..." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Load environment variables from set-env.ps1
if (Test-Path ".\set-env.ps1") {
    Write-Host "Loading environment variables..." -ForegroundColor Yellow
    . .\set-env.ps1
    Write-Host ""
} else {
    Write-Host "ERROR: set-env.ps1 not found!" -ForegroundColor Red
    Write-Host "Please create set-env.ps1 with your database credentials." -ForegroundColor Red
    exit 1
}

# Check if Maven is available
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Maven (mvn) not found in PATH!" -ForegroundColor Red
    Write-Host "Please install Maven and add it to your PATH." -ForegroundColor Red
    exit 1
}

Write-Host "Compiling project..." -ForegroundColor Yellow
mvn clean compile -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Compilation failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Starting desktop application..." -ForegroundColor Green
Write-Host ""

# Run the desktop application
$mavenArgs = @(
    "exec:java",
    "-Dexec.mainClass=com.hotel.desktop.DesktopLauncher"
)
& mvn $mavenArgs

