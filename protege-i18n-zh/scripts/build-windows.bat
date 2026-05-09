@echo off
chcp 65001 >nul
echo ================================================
echo   Protege 汉化包构建工具 (Windows)
echo ================================================
echo.

set VERSION=5.6.10
set PACKAGE_NAME=protege-i18n-zh-%VERSION%

echo 正在构建汉化包 v%VERSION%...
echo.

if not exist "i18n" (
    echo 错误: 未找到 i18n 目录
    echo 请确保此脚本放置在 protege-i18n-zh 项目根目录
    pause
    exit /b 1
)

if exist "%PACKAGE_NAME%.zip" (
    echo 删除旧文件...
    del /q "%PACKAGE_NAME%.zip"
)

if exist "%PACKAGE_NAME%" (
    echo 删除旧目录...
    rmdir /s /q "%PACKAGE_NAME%"
)

echo 创建目录结构...
mkdir "%PACKAGE_NAME%"

echo 复制文件...
xcopy /e /i /y "i18n" "%PACKAGE_NAME%\i18n" >nul
xcopy /e /i /y "docs" "%PACKAGE_NAME%\docs" >nul 2>nul
copy "scripts\protege-zh.bat" "%PACKAGE_NAME%\" >nul
copy "scripts\protege-zh.sh" "%PACKAGE_NAME%\" >nul
copy "README.md" "%PACKAGE_NAME%\" >nul
copy "LICENSE" "%PACKAGE_NAME%\" >nul 2>nul

echo.
echo 正在压缩...
powershell -command "Compress-Archive -Path '%PACKAGE_NAME%' -DestinationPath '%PACKAGE_NAME%.zip' -Force"

if exist "%PACKAGE_NAME%.zip" (
    echo.
    echo ================================================
    echo   构建完成!
    echo ================================================
    echo.
    echo 生成文件: %~dp0%PACKAGE_NAME%.zip
    echo.
    echo 下一步:
    echo   1. 解压此文件到 Protégé 安装目录
    echo   2. 运行 protege-zh.bat 启动中文版
    echo.
    set /p OPEN=是否打开所在文件夹? (Y/N):
    if /i "!OPEN!"=="Y" (
        explorer "%~dp0"
    )
) else (
    echo.
    echo 构建失败!
    pause
)
