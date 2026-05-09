# 版本更新指南

本文档描述如何将汉化包同步到新版 Protégé。

## 更新流程

### 1. 准备工作

```bash
# 克隆汉化包仓库
git clone https://github.com/protege-i18n-zh/protege-i18n-zh.git
cd protege-i18n-zh

# 克隆 Protégé 官方仓库（指定新版本标签）
git clone --depth 1 --branch v5.6.11 https://github.com/protegeproject/protege.git protege-new
```

### 2. 对比差异

```bash
# 对比 core 模块
diff protege-new/protege-editor-core/src/main/resources/org/protege/editor/core/ui/messages.properties \
      i18n/protege-editor-core/main/resources/org/protege/editor/core/ui/messages.properties

# 对比 OWL 模块
diff protege-new/protege-editor-owl/src/main/resources/org/protege/editor/owl/ui/owlmessages.properties \
      i18n/protege-editor-owl/main/resources/org/protege/editor/owl/ui/owlmessages.properties
```

### 3. 识别新增内容

使用以下命令查找新增的键值对：

```bash
# 提取英文原文件中的所有键
grep -E '^[a-zA-Z0-9._]+ =' protege-new/.../messages.properties | cut -d' ' -f1 > new_keys.txt
grep -E '^[a-zA-Z0-9._]+ =' i18n/.../messages.properties | cut -d' ' -f1 > old_keys.txt

# 找出新增的键
comm -13 old_keys.txt new_keys.txt
```

### 4. 翻译新增内容

1. 复制新版 `messages.properties` 到 `resources/` 目录
2. 在 `messages_zh.properties` 中添加新增键的中文翻译
3. 删除已移除键的中文翻译

### 5. 更新 CHANGELOG.md

```markdown
## v5.6.11 (2024-xx-xx)

### 更新
- 同步 Protégé v5.6.11
- 新增翻译：xxx, yyy, zzz

### 修复
- 修复翻译问题：...
```

### 6. 构建发布包

```bash
# 使用构建脚本
./scripts/build.sh v5.6.11

# 或手动打包
mkdir -p protege-i18n-zh-v5.6.11/i18n
cp -r i18n/* protege-i18n-zh-v5.6.11/i18n/
cp scripts/protege-zh.bat protege-i18n-zh-v5.6.11/
cp scripts/protege-zh.sh protege-i18n-zh-v5.6.11/
cp docs/*.md protege-i18n-zh-v5.6.11/
zip -r protege-i18n-zh-v5.6.11.zip protege-i18n-zh-v5.6.11/
```

### 7. 发布

1. 创建 GitHub Release
2. 上传发布包
3. 更新 Gitee 镜像

## 注意事项

- 每次只同步一个 Protégé 版本
- 确保所有新增键都有中文翻译
- 检查翻译风格一致性
- 使用 UTF-8 编码保存文件

## 自动化工具（未来计划）

- 自动对比脚本
- 翻译辅助工具
- 持续集成测试
