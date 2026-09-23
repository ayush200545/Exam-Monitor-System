# build.ps1
$ErrorActionPreference = "Stop"

Write-Host "Creating lib and bin directories..."
New-Item -ItemType Directory -Force -Path "lib" | Out-Null
New-Item -ItemType Directory -Force -Path "bin" | Out-Null

$postgresJar = "lib\postgresql-42.7.2.jar"
if (-not (Test-Path $postgresJar)) {
    Write-Host "Downloading PostgreSQL JDBC Driver..."
    Invoke-WebRequest -Uri "https://jdbc.postgresql.org/download/postgresql-42.7.2.jar" -OutFile $postgresJar
}

$jbcryptJar = "lib\jbcrypt-0.4.jar"
if (-not (Test-Path $jbcryptJar)) {
    Write-Host "Downloading BCrypt library..."
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar" -OutFile $jbcryptJar
}

Write-Host "Compiling Java sources..."
$javaFiles = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
javac -d bin -cp "lib/*" $javaFiles

Write-Host "Compilation complete."
Write-Host "--------------------------------"
Write-Host "To run the RMI Server:"
Write-Host "java -cp `"bin;lib/*`" com.distributedexam.server.RMIServer"
Write-Host ""
Write-Host "To run the Test Client (in another window):"
Write-Host "java -cp `"bin;lib/*`" client.TestRoomClient"
Write-Host "--------------------------------"
