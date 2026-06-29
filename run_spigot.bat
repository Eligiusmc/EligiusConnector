@echo off
echo Starting EligiusConnector test server with Spigot...
echo.

REM Check if Java 21 is installed
java -version 2>&1 | findstr /i "21" > nul
if errorlevel 1 (
    echo Warning: Java 21 not detected. Please install Java 21 LTS.
    echo.
)

REM Create run directory if it doesn't exist
if not exist "run" mkdir run

REM Download Spigot if not present
if not exist "run/spigot.jar" (
    echo Please download Spigot 1.21.1 manually from https://www.spigotmc.org/
    echo and place it as run/spigot.jar
    pause
    exit /b 1
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

REM Accept EULA
echo eula=true > run/eula.txt

REM Start server
echo Starting server...
cd run
java -Xms1G -Xmx2G -jar spigot.jar --nogui
pause
