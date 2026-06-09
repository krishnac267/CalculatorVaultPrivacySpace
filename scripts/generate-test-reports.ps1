# Generates consolidated test report pointers after Gradle test tasks.
param(
    [switch]$SkipInstrumented
)

$ErrorActionPreference = "Stop"
Set-Location (Split-Path $PSScriptRoot -Parent)

Write-Host "Running unit tests..."
& .\gradlew testDebugUnitTest --continue

if (-not $SkipInstrumented) {
    Write-Host "Running instrumented tests (requires device/emulator)..."
    & .\gradlew connectedDebugAndroidTest --continue
}

$unitReport = "app/build/reports/tests/testDebugUnitTest/index.html"
$androidReport = "app/build/reports/androidTests/connected/index.html"

Write-Host ""
Write-Host "=== Test Reports ==="
if (Test-Path $unitReport) {
    Write-Host "Unit tests:  $(Resolve-Path $unitReport)"
} else {
    Write-Warning "Unit test report not found."
}

if (-not $SkipInstrumented -and (Test-Path $androidReport)) {
    Write-Host "Android tests: $(Resolve-Path $androidReport)"
}

Write-Host ""
Write-Host "QA documentation: docs/qa/"
