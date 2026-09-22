# build.ps1
$ErrorActionPreference = "Stop"

Write-Host "Creating lib and bin directories..."
New-Item -ItemType Directory -Force -Path "lib" | Out-Null
New-Item -ItemType Directory -Force -Path "bin" | Out-Null

$jarPath = "lib\postgresql-42.7.2.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "Downloading PostgreSQL JDBC Driver..."
    Invoke-WebRequest -Uri "https://jdbc.postgresql.org/download/postgresql-42.7.2.jar" -OutFile $jarPath
}

Write-Host "Compiling Java sources..."
$javaFiles = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
javac -d bin -cp $jarPath $javaFiles

Write-Host "Compilation complete."
Write-Host "--------------------------------"
Write-Host "To run the RMI Server:"
Write-Host "java -cp `"bin;lib/*`" com.distributedexam.server.RMIServer"
Write-Host ""
Write-Host "To run the Test Client (in another window):"
Write-Host "java -cp `"bin;lib/*`" client.TestRoomClient"
Write-Host "--------------------------------"
