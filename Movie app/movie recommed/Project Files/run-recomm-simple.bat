@echo off
title Recomm - Simple Runner
color 0A

echo.
echo 🎬 Starting Recomm App...
echo.

REM Change to the correct directory
cd /d "%~dp0.."

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java not found! Please install Java JDK 8+
    pause
    exit /b 1
)

REM Clean old files
if exist "src\main\java\com\movieapp\*.class" (
    del /q "src\main\java\com\movieapp\*.class"
)

REM Compile
echo 🔨 Compiling...
javac -cp "Libraries\gson-2.8.9.jar" "src\main\java\com\movieapp\Recomm.java"
if errorlevel 1 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

REM Run with proper classpath - using forward slashes for better compatibility
echo 🚀 Running...
java -cp "src/main/java;Libraries/gson-2.8.9.jar" com.movieapp.Recomm

REM Cleanup
if exist "src\main\java\com\movieapp\*.class" (
    del /q "src\main\java\com\movieapp\*.class"
)

echo.
echo ✅ App closed. Thank you!
pause
