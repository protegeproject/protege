#!/bin/bash

VERSION=${1:-"5.6.10"}
PACKAGE_NAME="protege-i18n-zh-${VERSION}"

echo "构建 Protege 汉化包 v${VERSION}..."

if [ ! -d "i18n" ]; then
    echo "错误: 未找到 i18n 目录"
    exit 1
fi

rm -rf "${PACKAGE_NAME}"
mkdir -p "${PACKAGE_NAME}"

cp -r i18n "${PACKAGE_NAME}/"
cp -r docs "${PACKAGE_NAME}/"
cp scripts/protege-zh.bat "${PACKAGE_NAME}/"
cp scripts/protege-zh.sh "${PACKAGE_NAME}/"
cp README.md "${PACKAGE_NAME}/"
cp LICENSE "${PACKAGE_NAME}/" 2>/dev/null || true

chmod +x "${PACKAGE_NAME}/protege-zh.sh"

cd "${PACKAGE_NAME}" || exit 1
zip -r "../${PACKAGE_NAME}.zip" .
cd ..

echo "构建完成: ${PACKAGE_NAME}.zip"
