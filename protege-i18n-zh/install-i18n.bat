@echo off
chcp 65001 >nul
echo ================================================
echo   Protege 汉化包安装工具
echo ================================================
echo.

set "PROTEGE_PATH=%~dp0"
set "I18N_PATH=%PROTEGE_PATH%protege-i18n-zh-5.6.10\i18n"

echo 正在安装汉化包到: %PROTEGE_PATH%
echo.

REM 创建核心模块目录结构
echo 安装核心模块语言文件...
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.core\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.core\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\editor\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\editor\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\editor\core\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\editor\core\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\editor\core\ui\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\editor\core\ui\"

REM 复制核心模块语言文件
if exist "%I18N_PATH%\protege-editor-core\main\resources\org\protege\editor\core\ui\messages.properties" (
    copy "%I18N_PATH%\protege-editor-core\main\resources\org\protege\editor\core\ui\messages.properties" "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\editor\core\ui\" /Y >nul
    echo   - messages.properties
)
if exist "%I18N_PATH%\protege-editor-core\main\resources\org\protege\editor\core\ui\messages_zh.properties" (
    copy "%I18N_PATH%\protege-editor-core\main\resources\org\protege\editor\core\ui\messages_zh.properties" "%PROTEGE_PATH%plugins\org.protege.editor.core\resources\org\protege\editor\core\ui\" /Y >nul
    echo   - messages_zh.properties
)

REM 创建OWL模块目录结构
echo 安装OWL编辑器语言文件...
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.owl\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.owl\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\editor\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\editor\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\editor\owl\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\editor\owl\"
if not exist "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\editor\owl\ui\" mkdir "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\editor\owl\ui\"

REM 复制OWL模块语言文件
if exist "%I18N_PATH%\protege-editor-owl\main\resources\org\protege\editor\owl\ui\owlmessages.properties" (
    copy "%I18N_PATH%\protege-editor-owl\main\resources\org\protege\editor\owl\ui\owlmessages.properties" "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\editor\owl\ui\" /Y >nul
    echo   - owlmessages.properties
)
if exist "%I18N_PATH%\protege-editor-owl\main\resources\org\protege\editor\owl\ui\owlmessages_zh.properties" (
    copy "%I18N_PATH%\protege-editor-owl\main\resources\org\protege\editor\owl\ui\owlmessages_zh.properties" "%PROTEGE_PATH%plugins\org.protege.editor.owl\resources\org\protege\editor\owl\ui\" /Y >nul
    echo   - owlmessages_zh.properties
)

echo.
echo ================================================
echo   安装完成!
echo ================================================
echo.
echo 下一步: 双击运行 protege-zh.bat 启动中文版
echo.
pause
