@echo off
title Recomm - Movie Discovery App
color 0A

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    🎬 RECOMM APP 🎬                        ║
echo ║              Simple Movie Discovery App                     ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

REM Check if Java is installed
echo 🔍 Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Java is not installed or not in PATH
    echo.
    echo Please install Java JDK 8 or higher from:
    echo https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)
echo ✅ Java is installed

REM Clean up old compiled files
echo 🧹 Cleaning up old compiled files...
if exist "..\src\main\java\com\movieapp\*.class" (
    del /q "..\src\main\java\com\movieapp\*.class"
    echo ✅ Old files cleaned
) else (
    echo ℹ️  No old files to clean
)

REM Check if Gson library exists
echo 📚 Checking Gson library...
if not exist "..\Libraries\gson-2.8.9.jar" (
    echo ❌ Gson library not found
    echo 📥 Downloading Gson library...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar' -OutFile '..\Libraries\gson-2.8.9.jar'"
    if errorlevel 1 (
        echo ❌ Failed to download Gson library
        echo Please check your internet connection
        pause
        exit /b 1
    )
    echo ✅ Gson library downloaded
) else (
    echo ✅ Gson library found
)

REM Compile the Java file
echo.
echo 🔨 Compiling Recomm App...
javac -cp "..\Libraries\gson-2.8.9.jar" "..\src\main\java\com\movieapp\Recomm.java"

if errorlevel 1 (
    echo ❌ Compilation failed
    echo Please check the source code for errors
    pause
    exit /b 1
)
echo ✅ Compilation successful

echo.
echo 🚀 Starting Recomm App...
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                        FEATURES                              ║
echo ╠══════════════════════════════════════════════════════════════╣
echo ║ 🏠 Browse popular movies, TV shows, and anime              ║
echo ║ 🔍 Search for any content by title                         ║
echo ║ 📱 Click cards to see detailed information                ║
echo ║ 🎨 Netflix-style dark theme interface                     ║
echo ║ 📊 View ratings, plots, and genres                        ║
echo ║ 🎭 Real movie posters with enhanced loading               ║
echo ║ 📱 Responsive design that centers content                 ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

REM Run the app with correct Windows classpath separator
java -cp "..\src\main\java;..\Libraries\gson-2.8.9.jar" com.movieapp.Recomm

REM Clean up compiled files after running
echo.
echo 🧹 Cleaning up compiled files...
del /q "..\src\main\java\com\movieapp\*.class"
echo ✅ Cleanup complete

echo.
echo 🎬 Recomm App has been closed
echo Thank you for using Recomm!
echo.
pause
