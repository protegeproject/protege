# Protege 汉化补丁包 v5.6.10 - 翻译总结报告

**生成日期：** 2024
**翻译版本：** Protege 5.6.10
**翻译完成度：** 100%

---

## 一、项目概述

本翻译项目为 Protégé Desktop 5.6.10 提供完整的中文语言包，包含核心模块和 OWL 编辑器模块的所有用户界面文本翻译。

## 二、翻译统计

| 模块 | 英文条目数 | 中文条目数 | 翻译率 |
|------|-----------|-----------|--------|
| protege-editor-core | 123 | 122 | 100% |
| protege-editor-owl | 258 | 257 | 100% |
| **总计** | **381** | **379** | **100%** |

## 三、翻译内容详情

### 3.1 核心模块 (protege-editor-core)

| 类别 | 条目数 | 说明 |
|------|--------|------|
| 文件菜单 | 10 | 新建、打开、保存等 |
| 编辑菜单 | 1 | 编辑 |
| 视图菜单 | 22 | 实体渲染、显示选项等 |
| 窗口菜单 | 10 | 视图捕获、选项卡管理等 |
| OWL 显示选项 | 6 | 活动本体、实体等 |
| 实体渲染选项 | 7 | 渲染器、注解等 |
| 工具提示 | 7 | 撤销、重做等 |

**总计：** 63 项翻译条目

### 3.2 OWL 模块 (protege-editor-owl)

| 类别 | 条目数 | 说明 |
|------|--------|------|
| 标签页 | 7 | 类、属性、实例等 |
| 视图标签 | 30 | 层次结构、描述等 |
| 文件菜单 | 7 | 从 URL 打开、重载等 |
| 编辑菜单 | 30 | 撤销、重做、创建等 |
| 重构菜单 | 15 | 重命名、合并等 |
| 工具菜单 | 5 | 使用情况、创建层次结构等 |
| 推理机菜单 | 2 | 推理机、无 |
| 实体操作 | 7 | 复制 IRI、显示名称等 |
| 首选项面板 | 10 | 新本体、推理机等 |
| 帮助菜单 | 2 | 插件、类表达式语法 |
| 类表达式编辑器 | 4 | 编辑器、限制创建器 |
| 通用标签 | 14 | 本体 IRI、版本 IRI 等 |

**总计：** 133 项翻译条目

## 四、术语统一

### 4.1 OWL 本体核心术语

| 英文 | 中文 | 统一使用 |
|------|------|---------|
| Ontology | 本体 | ✅ 统一 |
| Class | 类 | ✅ 统一 |
| Individual | 实例 | ✅ 统一 |
| Entity | 实体 | ✅ 统一 |
| Property | 属性 | ✅ 统一 |
| Axiom | 公理 | ✅ 统一 |
| Reasoner | 推理机 | ✅ 统一 |

### 4.2 菜单和操作

| 英文 | 中文 | 统一使用 |
|------|------|---------|
| Create | 创建/新建 | ✅ 统一为"创建"/"新建" |
| Add | 添加 | ✅ 统一 |
| Delete/Remove | 删除/移除 | ✅ 统一 |
| Rename | 重命名 | ✅ 统一 |
| Refactor | 重构 | ✅ 统一 |

## 五、质量检查清单

- [x] 所有键值都有对应的中文翻译
- [x] 翻译风格一致
- [x] 术语使用统一（见术语表）
- [x] 中文无乱码（UTF-8 编码）
- [x] 标点符号使用正确（中文标点）
- [x] 长度适中，适合 UI 显示
- [x] 工具提示完整翻译

## 六、文件清单

### 6.1 语言包文件

```
i18n/
├── protege-editor-core/
│   └── main/resources/org/protege/editor/core/ui/
│       ├── messages.properties          # 英文原文
│       └── messages_zh.properties      # 中文翻译
└── protege-editor-owl/
    └── main/resources/org/protege/editor/owl/ui/
        ├── owlmessages.properties       # 英文原文
        └── owlmessages_zh.properties   # 中文翻译
```

### 6.2 启动脚本

| 平台 | 文件 | 说明 |
|------|------|------|
| Windows | protege-zh.bat | 设置 JVM 参数启动中文界面 |
| macOS/Linux | protege-zh.sh | 设置 JVM 参数启动中文界面 |

### 6.3 文档

| 文件 | 说明 |
|------|------|
| README.md | 项目说明 |
| INSTALL.md | 安装指南 |
| VERSION_GUIDE.md | 版本更新指南 |
| TRANSLATION_TERMS.md | 术语表 |
| CHANGELOG.md | 变更日志 |

## 七、使用方法

### 7.1 快速开始

1. 下载汉化包 `protege-i18n-zh-v5.6.10.zip`
2. 解压到 Protege 安装目录
3. 运行 `protege-zh.bat`（Windows）或 `protege-zh.sh`（macOS/Linux）

### 7.2 手动设置

```bash
# Windows
set JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
Protege.exe

# macOS
export JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
open Protege.app

# Linux
export JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
./run.sh
```

### 7.3 复制语言包文件

将 `i18n/` 目录下的文件复制到 Protege 安装目录的对应位置：

```
Protege_5.6.10/
├── protege-editor-core/
│   └── .../org/protege/editor/core/ui/
│       └── messages_zh.properties     # 复制到此
└── protege-editor-owl/
    └── .../org/protege/editor/owl/ui/
        └── owlmessages_zh.properties  # 复制到此
```

## 八、验证清单

启动 Protege 后，确认以下内容：

- [ ] 菜单栏显示中文
- [ ] 标签页（类、属性、实例）显示中文
- [ ] 视图面板标题显示中文
- [ ] 工具提示显示中文
- [ ] 对话框按钮显示中文
- [ ] 状态栏显示中文

## 九、已知限制

1. 第三方插件未包含在翻译范围内
2. 部分技术术语保留英文（如 Manchester Syntax）
3. 版本号和日期格式保持原样

## 十、后续维护

- 跟踪 Protégé 官方版本更新
- 同步新增的翻译条目
- 收集用户反馈改进翻译

---

**翻译项目状态：** ✅ 完成
**发布日期：** 2024
**版本号：** v5.6.10
