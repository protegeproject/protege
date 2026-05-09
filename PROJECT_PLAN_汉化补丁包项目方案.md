# Protege 汉化补丁包项目方案

## 项目概述

本项目为 Protege Desktop 提供中文语言包补丁，使终端用户能够以中文界面使用 Protege。

### 核心原则

- **独立维护**：作为独立项目存在，不修改 Protege 官方代码
- **增量更新**：跟随 Protege 官方版本同步更新
- **易于部署**：用户只需下载覆盖包，放置到指定目录即可

---

## 项目结构设计

### 目录结构

```
protege-i18n-zh/
├── README.md                          # 项目说明
├── VERSION_GUIDE.md                   # 版本更新指南
├── CHANGELOG.md                       # 变更日志
├── build.sh                           # 构建脚本（可选）
│
├── docs/                              # 文档
│   ├── INSTALL.md                     # 安装说明
│   └── TRANSLATION_TERMS.md          # 术语表
│
└── resources/                         # 汉化资源文件
    ├── protege-editor-core/           # 核心模块
    │   └── main/
    │       └── resources/
    │           └── org/protege/editor/core/ui/
    │               ├── messages.properties         # 英文原文（参考）
    │               └── messages_zh.properties     # 中文翻译
    │
    └── protege-editor-owl/            # OWL模块
        └── main/
            └── resources/
                └── org/protege/editor/owl/ui/
                    ├── owlmessages.properties     # 英文原文（参考）
                    └── owlmessages_zh.properties  # 中文翻译
```

### 部署包结构（用户下载）

```
protege-i18n-zh-v5.6.10.zip
│
├── i18n/                              # 放置到 Protege 安装目录
│   ├── protege-editor-core/
│   │   └── main/
│   │       └── resources/
│   │           └── org/protege/editor/core/ui/
│   │               └── messages_zh.properties
│   │
│   └── protege-editor-owl/
│       └── main/
│           └── resources/
│               └── org/protege/editor/owl/ui/
│                   └── owlmessages_zh.properties
│
├── plugins/                          # 可选：中文插件
│   └── ...
│
└── docs/
    ├── INSTALL.md                     # 安装说明
    ├── UNINSTALL.md                   # 卸载说明
    └── TRANSLATION_TERMS.md           # 术语表
```

---

## 工作流程设计

### 流程图

```
┌──────────────────────────────────────────────────────────────────────┐
│                        版本更新工作流程                                 │
└──────────────────────────────────────────────────────────────────────┘

1. 监听 Protege 官方发布
        │
        ▼
2. 下载新版 Protege 源代码
        │
        ▼
3. 对比新版 properties 文件与当前版本
        │
        ├── 有新增键值 ──────────┐
        │                       │
        │                       ▼
        │                4. 翻译新增内容
        │                       │
        │                       ▼
        │                5. 更新汉化文件
        │                       │
        ├── 无新增键值 ──────────┤
        │                       │
        ▼                       ▼
6. 更新 CHANGELOG.md    ◄─────────────────┘
        │
        ▼
7. 构建发布包
        │
        ▼
8. 发布到 GitHub / Gitee
```

### 详细步骤

#### 步骤 1：监听官方发布

- 关注 Protege GitHub releases 页面：https://github.com/protegeproject/protege/releases
- 每次新版本发布后，在 1 周内完成汉化更新

#### 步骤 2：下载新版代码

```bash
# 方式 A：克隆新版本标签
git clone --depth 1 --branch v5.6.11 https://github.com/protegeproject/protege.git

# 方式 B：下载源码包
wget https://github.com/protegeproject/protege/archive/refs/tags/v5.6.11.tar.gz
```

#### 步骤 3：对比差异

```bash
# 核心模块
diff protege-v5.6.11/protege-editor-core/src/main/resources/org/protege/editor/core/ui/messages.properties \
      protege-i18n-zh/resources/protege-editor-core/main/resources/org/protege/editor/core/ui/messages.properties

# OWL模块
diff protege-v5.6.11/protege-editor-owl/src/main/resources/org/protege/editor/owl/ui/owlmessages.properties \
      protege-i18n-zh/resources/protege-editor-owl/main/resources/org/protege/editor/owl/ui/owlmessages.properties
```

#### 步骤 4：翻译新增内容

- 识别新增的键值对
- 按照术语表进行翻译
- 确保翻译风格一致

#### 步骤 5：更新汉化文件

- 复制新版 properties 到项目
- 保持 messages_zh.properties 与 messages.properties 键同步
- 删除旧版本中已移除的键值

#### 步骤 6：更新变更日志

```markdown
## v5.6.11 (2024-xx-xx)

### 更新
- 同步 Protege v5.6.11
- 新增翻译：xxx, yyy, zzz
```

#### 步骤 7：构建发布包

```bash
# 使用提供的构建脚本
./build.sh v5.6.11

# 或手动打包
mkdir -p protegi18n-zh-v5.6.11/i18n
cp -r resources/* protegi18n-zh-v5.6.11/i18n/
zip -r protegi18n-zh-v5.6.11.zip protegi18n-zh-v5.6.11/
```

#### 步骤 8：发布

- GitHub Releases
- Gitee Releases（国内加速）
- 可选：Maven 中央仓库（高级）

---

## 部署说明（终端用户）

### 安装步骤

1. **下载汉化补丁包**
   - 从 GitHub/Gitee Releases 下载最新版本

2. **解压到 Protege 安装目录**
   ```
   Windows: C:\Program Files\Protege_5.6.10\
   macOS:   /Applications/Protege_5.6.10.app/Contents/
   Linux:   ~/protege-5.6.10/
   ```

3. **启动 Protege**
   ```bash
   # Windows
   Protege.exe

   # macOS
   open Protege.app

   # Linux
   ./run.sh
   ```

4. **设置中文界面**（通过代码修改实现，见下方说明）

### 注意事项

- ⚠️ 覆盖包需与 Protege 主版本匹配
- ⚠️ 升级 Protege 主版本后需下载对应版本的汉化包
- ⚠️ 卸载时删除覆盖文件即可恢复英文

---

## 技术实现方案

### 方案选择：运行时覆盖（推荐）

由于 Protege 使用 OSGi 框架和标准 Java 国际化机制，我们采用**运行时配置覆盖**方式：

#### 方案 A：JVM 参数启动（最简单）

创建启动脚本 `protege-zh.bat` / `protege-zh.sh`：

**Windows (protege-zh.bat)**
```batch
@echo off
set JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
start "Protege" "Protege.exe"
```

**macOS/Linux (protege-zh.sh)**
```bash
#!/bin/bash
export JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
open Protege.app
# 或
./run.sh
```

#### 方案 B：配置文件覆盖（可选）

如果 Protege 支持外部配置文件，可以创建：

```
protege-5.6.10/
├── Protege.exe
├── run.sh
└── i18n/                          # 汉化补丁放置目录
    ├── messages_zh.properties
    └── owlmessages_zh.properties
```

#### 方案 C：修改启动脚本（需要用户手动修改）

修改 `run.sh` 或创建新的启动配置：

```bash
# 在 run.sh 中添加
JAVA_OPTS="$JAVA_OPTS -Duser.language=zh -Duser.country=CN"
```

---

## 文件命名规范

### properties 文件命名

| 文件 | 说明 |
|------|------|
| `messages.properties` | 英文原文（参考文件） |
| `messages_zh.properties` | 简体中文翻译 |
| `messages_zh_CN.properties` | 中文简体（等效） |

### 发布包命名

```
protege-i18n-zh-{version}.zip
protege-i18n-zh-5.6.10.zip      # 对应 Protege 5.6.10
protege-i18n-zh-5.6.11.zip      # 对应 Protege 5.6.11
```

---

## 版本号管理

### 版本号对应规则

汉化包版本号与 Protege 主版本号对应：

| Protege 版本 | 汉化包版本 |
|--------------|-----------|
| 5.6.10 | 5.6.10 |
| 5.6.11 | 5.6.11 |
| ... | ... |

### 版本状态标识

- `v5.6.10` - 正式版
- `v5.6.10-beta` - 测试版
- `v5.6.10-rc1` - 候选版

---

## 质量保证

### 翻译质量检查清单

- [ ] 所有键值都有对应的中文翻译
- [ ] 翻译风格与 Protege 中文社区一致
- [ ] 术语使用统一（见术语表）
- [ ] 中文无乱码（UTF-8 编码）
- [ ] 标点符号使用正确
- [ ] 长度适中，不超出 UI 显示范围

### 测试检查清单

- [ ] 在 Windows 上测试
- [ ] 在 macOS 上测试
- [ ] 在 Linux 上测试
- [ ] 测试所有主要菜单
- [ ] 测试对话框和消息框
- [ ] 测试工具提示
- [ ] 测试首选项面板

---

## 术语表（初稿）

| 英文 | 中文 | 说明 |
|------|------|------|
| Ontology | 本体 | OWL Ontology |
| Class | 类 | OWL Class |
| Property | 属性 | OWL Property |
| Individual | 实例 | OWL Individual |
| SubClass | 子类 | 类的层次关系 |
| Object Property | 对象属性 | OWL Object Property |
| Data Property | 数据属性 | OWL Data Property |
| Annotation Property | 注解属性 | OWL Annotation Property |
| Equivalent Class | 等价类 | 等价类公理 |
| Disjoint | 互斥 | 互斥关系 |
| Reasoner | 推理机 | 推理引擎 |
| Axiom | 公理 | OWL Axiom |
| Entity | 实体 | OWL Entity |
| Expression | 表达式 | Manchester Syntax 等 |
| Triple | 三元组 | RDF 三元组 |
| Namespace | 命名空间 | Ontology Namespace |
|IRI | IRI | Internationalized Resource Identifier |

---

## 维护计划

### 短期维护

- [ ] 完成 v5.6.10 版本的完整翻译
- [ ] 发布正式版汉化包
- [ ] 建立发布流程

### 中期维护

- [ ] 建立术语表文档
- [ ] 开发半自动化翻译辅助工具
- [ ] 建立翻译质量检查流程

### 长期维护

- [ ] 争取成为 Protege 官方支持的语言
- [ ] 建立翻译志愿者社区
- [ ] 支持更多语言（中英文以外）

---

## 风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|----------|
| Protege 版本更新频繁 | 翻译工作量增加 | 建立自动化对比工具 |
| UI 界面变动 | 原有翻译失效 | 及时关注版本更新 |
| 术语不统一 | 用户困惑 | 建立并维护术语表 |
| 第三方插件未汉化 | 体验不一致 | 提供插件汉化指南 |

---

## 成本估算

### 初始翻译（v5.6.10）

| 项目 | 估计工时 |
|------|---------|
| 文本提取和整理 | 4 小时 |
| 核心模块翻译 | 8 小时 |
| OWL 模块翻译 | 16 小时 |
| 校对和润色 | 8 小时 |
| 测试和修复 | 8 小时 |
| **总计** | **44 小时** |

### 后续版本更新

| 项目 | 估计工时 |
|------|---------|
| 版本对比 | 1 小时 |
| 新增内容翻译 | 2-4 小时 |
| 测试验证 | 2 小时 |
| **总计** | **5-7 小时/版本** |

---

## 审核清单

在开始翻译工作前，请确认以下事项：

### 项目方案审核

- [ ] 项目结构设计是否合理？
- [ ] 工作流程是否清晰可行？
- [ ] 部署方式是否便于用户使用？
- [ ] 术语表是否准确完整？

### 技术方案审核

- [ ] JVM 参数方案是否可行？
- [ ] 是否需要考虑其他启动方式？
- [ ] properties 文件编码是否正确（UTF-8）？
- [ ] 文件放置路径是否正确？

### 质量保证审核

- [ ] 翻译检查清单是否完整？
- [ ] 测试平台覆盖是否足够？
- [ ] 术语表是否需要补充？

---

*文档版本：1.0*
*创建日期：2024*
*最后更新：初始方案制定*
