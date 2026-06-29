@echo off
echo Starting EligiusConnector test server...
echo.

REM Check if Java 21 is installed
java -version 2>&1 | findstr /i "21" > nul
if errorlevel 1 (
    echo Warning: Java 21 not detected. Please install Java 21 LTS.
    echo.
)

REM Create run directory if it doesn't exist
if not exist "run" mkdir run

REM Download Paper if not present
if not exist "run/paper.jar" (
    echo Downloading Paper 1.21.1...
    curl -L -o run/paper.jar "https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/132/downloads/paper-1.21.1-132.jar"
    if errorlevel 1 (
        echo Failed to download Paper. Please download manually.
        pause
        exit /b 1
    )
)

REM Copy plugin to run directory
if exist "build/libs/EligiusConnector-*.jar" (
    copy "build\libs\EligiusConnector-*.jar" "run\plugins\" > nul
    echo Plugin copied to run/plugins/
) else (
    echo Plugin JAR not found. Run 'gradlew build' first.
    pause
    exit /b 1
)

REM Start server
echo Starting server...
cd run
java -Xms1G -Xmx2G -jar paper.jar --nogui
pause
