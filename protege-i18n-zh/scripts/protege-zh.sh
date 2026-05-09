#!/bin/bash
export JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -d "$SCRIPT_DIR/Protege.app" ]; then
    open "$SCRIPT_DIR/Protege.app"
elif [ -d "$SCRIPT_DIR/Protege_5.6.10.app" ]; then
    open "$SCRIPT_DIR/Protege_5.6.10.app"
elif [ -d "$SCRIPT_DIR/Protege_5.6.11.app" ]; then
    open "$SCRIPT_DIR/Protege_5.6.11.app"
elif [ -f "$SCRIPT_DIR/run.sh" ]; then
    "$SCRIPT_DIR/run.sh"
elif [ -f "$SCRIPT_DIR/protege" ]; then
    "$SCRIPT_DIR/protege"
else
    echo "未找到 Protege 应用程序"
    echo "请确保此脚本放置在 Protege 安装目录中"
    exit 1
fi
