@echo off
chcp 65001 >nul
set JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
set "PROTEGE_PATH=%~dp0"

if exist "%PROTEGE_PATH%Protege.exe" (
    start "Protege" "%PROTEGE_PATH%Protege.exe"
) else if exist "%PROTEGE_PATH%Protege_5.6.10\Protege.exe" (
    start "Protege" "%PROTEGE_PATH%Protege_5.6.10\Protege.exe"
) else if exist "%PROTEGE_PATH%Protege_5.6.11\Protege.exe" (
    start "Protege" "%PROTEGE_PATH%Protege_5.6.11\Protege.exe"
) else (
    echo 未找到 Protege.exe
    echo 请确保此脚本放置在 Protege 安装目录中
    pause
)
