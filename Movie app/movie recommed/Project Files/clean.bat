@echo off
echo 🧹 Cleaning up compiled files...

if exist "..\src\main\java\com\movieapp\*.class" (
    del /q "..\src\main\java\com\movieapp\*.class"
    echo ✅ All .class files removed!
) else (
    echo ℹ️  No .class files found to clean.
)

echo.
echo Press any key to close...
pause >nul
