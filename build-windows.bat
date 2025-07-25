@echo off
echo Building Space Dodger for Windows...

REM Create build directory
if not exist "build" mkdir build
if not exist "dist\windows" mkdir dist\windows

REM Compile Java files
echo Compiling Java source...
cd src
javac -cp . *.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Create JAR file
echo Creating JAR file...
jar cfe ..\build\SpaceDodger.jar SpaceDodger *.class
if %errorlevel% neq 0 (
    echo JAR creation failed!
    pause
    exit /b 1
)

REM Copy assets
echo Copying assets...
xcopy /E /I ..\assets ..\build\assets

REM Go back to root
cd ..

REM Create executable with Launch4j (requires Launch4j to be installed)
echo Creating Windows executable...
echo Note: This requires Launch4j to be installed and in PATH
echo Download from: http://launch4j.sourceforge.net/
launch4jc build\launch4j-config.xml

REM Copy to distribution folder
if exist "build\SpaceDodger.exe" (
    copy "build\SpaceDodger.exe" "dist\windows\"
    xcopy /E /I "assets" "dist\windows\assets"
    echo.
    echo Windows build complete! Check dist\windows\ folder
) else (
    echo Warning: EXE file not created. Install Launch4j to create native executable.
    echo You can still distribute the JAR file from the build folder.
)

pause