@echo off
echo 🎬 Recomm - Simple Movie Discovery App
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Java is not installed or not in PATH
    echo Please install Java JDK 8 or higher
    pause
    exit /b 1
)

REM Clean up old compiled files
if exist "..\src\main\java\com\movieapp\*.class" (
    echo 🧹 Cleaning up old compiled files...
    del /q "..\src\main\java\com\movieapp\*.class"
)

REM Check if Gson library exists
if not exist "..\Libraries\gson-2.8.9.jar" (
    echo 📥 Downloading Gson library...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar' -OutFile '..\Libraries\gson-2.8.9.jar'"
    if errorlevel 1 (
        echo ❌ Failed to download Gson library
        pause
        exit /b 1
    )
)

REM Compile the Java file
echo 🔨 Compiling Recomm App...
javac -cp "..\Libraries\gson-2.8.9.jar" "..\src\main\java\com\movieapp\Recomm.java"

if errorlevel 1 (
    echo ❌ Compilation failed
    pause
    exit /b 1
)

echo 🚀 Running Recomm App...
echo.
echo Features:
echo • 🏠 Browse popular movies, TV shows, and anime
echo • 🔍 Search for any content by title
echo • 📱 Click cards to see detailed information
echo • 🎨 Netflix-style dark theme interface
echo • 📊 View ratings, plots, and genres
echo.

REM Run the app
java -cp "..\src\main\java;..\Libraries\gson-2.8.9.jar" com.movieapp.Recomm

REM Clean up compiled files after running
echo.
echo 🧹 Cleaning up compiled files...
del /q "..\src\main\java\com\movieapp\*.class"

echo �� App closed
pause
