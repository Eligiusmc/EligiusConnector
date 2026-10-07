@echo off
setlocal
echo Starting EligiusConnector test server (Purpur 26.2)...
echo.

set JAVA_CMD=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin\java.exe
if not exist "%JAVA_CMD%" set JAVA_CMD=java

if not exist "server\purpur-26.2.jar" (
    echo Downloading Purpur 26.2...
    curl -L -o server\purpur-26.2.jar "https://api.purpurmc.org/v2/purpur/26.2/latest/download"
    if errorlevel 1 (
        echo Failed to download Purpur.
        pause
        exit /b 1
    )
)

if exist "build\libs\EligiusConnector-*.jar" (
    del /q "server\plugins\EligiusConnector-*.jar" 2>nul
    copy "build\libs\EligiusConnector-*.jar" "server\plugins\" > nul
    echo Plugin deployed to server/plugins/
) else (
    echo Plugin JAR not found. Run 'gradlew build' first.
    pause
    exit /b 1
)

echo Starting server (DISCORD_BOT_TOKEN must be set in env)...
cd server
"%JAVA_CMD%" -Xms1G -Xmx2G -jar purpur-26.2.jar --nogui
pause
