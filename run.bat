@echo off
echo Compiling Space Dodger...
cd src
javac *.java
if %errorlevel% neq 0 (
    echo Compilation failed! Make sure Java is installed and in your PATH.
    pause
    exit /b %errorlevel%
)
echo Compilation successful!
echo.
echo Starting Space Dodger...
java SpaceDodger
pause