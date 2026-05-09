# Protege 项目汉化工作指南

## 概述

本文档详细说明如何对 Protege 项目进行完整汉化。Protege 是一个基于 Eclipse/OSGi 架构的 Java Swing 桌面应用，用于编辑 OWL 本体。

本项目已建立国际化（i18n）框架，以下是继续完成汉化的详细步骤。

---

## 一、项目国际化架构

### 1.1 已创建的文件结构

```
/workspace/
├── protege-editor-core/
│   ├── src/main/java/org/protege/editor/core/ui/
│   │   └── Messages.java                    # 核心模块国际化工具类
│   └── src/main/resources/org/protege/editor/core/ui/
│       ├── messages.properties              # 英文默认（模板）
│       └── messages_zh.properties           # 中文翻译
│
├── protege-editor-owl/
│   ├── src/main/java/org/protege/editor/owl/ui/
│   │   └── OWLMessages.java                 # OWL 模块国际化工具类
│   └── src/main/resources/org/protege/editor/owl/ui/
│       ├── owlmessages.properties            # 英文默认（模板）
│       └── owlmessages_zh.properties         # 中文翻译
│
└── protege-editor-core/src/main/resources/
    └── plugin.xml                           # 核心菜单配置（待修改）
    └── protege-editor-owl/src/main/resources/plugin.xml  # OWL 菜单配置（待修改）
```

### 1.2 Messages 工具类说明

**Messages.java** - 核心模块工具类：
```java
// 获取翻译文本，如果找不到则返回键名
String text = Messages.getString("menu.file.new");

// 指定默认值
String text = Messages.getString("menu.file.new", "New...");
```

**OWLMessages.java** - OWL 模块工具类：
```java
// 获取翻译文本
String text = OWLMessages.getString("tab.classes");

// 指定默认值
String text = OWLMessages.getString("tab.classes", "Classes");
```

---

## 二、详细操作步骤

### 步骤 1：修改 plugin.xml 文件（核心模块）

**文件位置**：`protege-editor-core/src/main/resources/plugin.xml`

**修改方法**：将 `<name value="..."/>` 和 `<toolTip value="..."/>` 中的硬编码文本改为国际化键引用。

**示例**：

**修改前**：
```xml
<extension id="menu.file.new" point="org.protege.editor.core.application.EditorKitMenuAction">
    <name value="New..."/>
    <toolTip value="Create a new empty ontology"/>
    <class value="org.protege.editor.core.ui.action.NewAction"/>
    ...
</extension>
```

**修改后**：
```xml
<extension id="menu.file.new" point="org.protege.editor.core.application.EditorKitMenuAction">
    <name value="%menu.file.new"/>
    <toolTip value="%menu.file.new.tooltip"/>
    <class value="org.protege.editor.core.ui.action.NewAction"/>
    ...
</extension>
```

**注意**：Eclipse/OSGi 插件框架使用 `%` 前缀来引用外部化的字符串资源。

---

### 步骤 2：修改 plugin.xml 文件（OWL 模块）

**文件位置**：`protege-editor-owl/src/main/resources/plugin.xml`

**修改方法**：同样将 `<label value="..."/>` 改为 `<label value="%..."/>` 格式。

**示例**：

**修改前**：
```xml
<extension id="OWLClassesTab" point="org.protege.editor.core.application.WorkspaceTab">
    <label value="Classes"/>
    <class value="org.protege.editor.owl.ui.OWLWorkspaceViewsTab"/>
    ...
</extension>
```

**修改后**：
```xml
<extension id="OWLClassesTab" point="org.protege.editor.core.application.WorkspaceTab">
    <label value="%tab.classes"/>
    <class value="org.protege.editor.owl.ui.OWLWorkspaceViewsTab"/>
    ...
</extension>
```

---

### 步骤 3：修改 Java UI 代码

**涉及文件**：
- `protege-editor-core/src/main/java/**/ui/**/*.java`
- `protege-editor-owl/src/main/java/**/ui/**/*.java`

**修改方法**：将硬编码的 UI 文本替换为 Messages 调用。

**示例 1 - 核心模块**：

**修改前**：
```java
JLabel label = new JLabel("Ontology IRI");
```

**修改后**：
```java
import org.protege.editor.core.ui.Messages;

JLabel label = new JLabel(Messages.getString("label.ontologyiri", "Ontology IRI"));
```

**示例 2 - OWL 模块**：

**修改前**：
```java
JButton button = new JButton("Browse...");
```

**修改后**：
```java
import org.protege.editor.owl.ui.OWLMessages;

JButton button = new JButton(OWLMessages.getString("label.browse", "Browse..."));
```

---

### 步骤 4：更新属性文件

#### 4.1 核心模块 messages.properties

**文件位置**：`protege-editor-core/src/main/resources/org/protege/editor/core/ui/messages.properties`

**格式**：
```
键名 = 英文文本
键名.tooltip = 英文工具提示文本
```

#### 4.2 核心模块 messages_zh.properties

**文件位置**：`protege-editor-core/src/main/resources/org/protege/editor/core/ui/messages_zh.properties`

**格式**：
```
键名 = 中文文本
键名.tooltip = 中文工具提示文本
```

#### 4.3 OWL 模块 owlmessages.properties

**文件位置**：`protege-editor-owl/src/main/resources/org/protege/editor/owl/ui/owlmessages.properties`

#### 4.4 OWL 模块 owlmessages_zh.properties

**文件位置**：`protege-editor-owl/src/main/resources/org/protege/editor/owl/ui/owlmessages_zh.properties`

---

## 三、工作量估算与优先级

### 高优先级（必须完成）

| 序号 | 文件 | 文本项数 | 估计工时 |
|------|------|---------|---------|
| 1 | plugin.xml (core) | ~50 项 | 2-3 小时 |
| 2 | plugin.xml (owl) | ~150 项 | 6-8 小时 |
| 3 | 主要 Java UI (core) | ~100 项 | 4-5 小时 |
| 4 | 主要 Java UI (owl) | ~200 项 | 8-10 小时 |

### 中优先级（建议完成）

| 序号 | 文件 | 文本项数 | 估计工时 |
|------|------|---------|---------|
| 5 | 错误消息和对话框 | ~50 项 | 2-3 小时 |
| 6 | 首选项面板 | ~30 项 | 1-2 小时 |

### 低优先级（可选）

| 序号 | 文件 | 文本项数 | 估计工时 |
|------|------|---------|---------|
| 7 | 工具提示文本 | ~20 项 | 1 小时 |
| 8 | 状态栏消息 | ~10 项 | 0.5 小时 |

---

## 四、Git 分支管理策略

### 方案 A：创建独立中文分支（推荐）

```bash
# 1. 从主分支创建中文分支
git checkout -b localization/chinese

# 2. 提交国际化框架
git add .
git commit -m "feat(i18n): Add internationalization framework for Chinese localization"

# 3. 逐步提交汉化内容
git commit -m "chore(l10n): Translate core menu items to Chinese"
git commit -m "chore(l10n): Translate OWL menu items to Chinese"
# ... 持续提交

# 4. 定期与主分支同步
git fetch origin
git rebase origin/master

# 5. 完成后创建 Pull Request
```

### 方案 B：创建中英文分支

```bash
# 1. 创建中文分支
git checkout -b chinese

# 2. 汉化完成后，不合并回主分支，作为独立发行版维护
# 适合长期维护独立的汉化版本
```

### 方案 C：条件编译

```bash
# 创建构建配置文件
mvn package -Dlocale=zh-CN  # 构建中文版
mvn package                 # 构建英文版（默认）
```

---

## 五、构建与测试

### 5.1 Maven 构建

```bash
# 清理并编译
mvn clean compile

# 打包
mvn package

# 运行
java -jar protege-desktop/target/protege-*.jar
```

### 5.2 设置系统语言测试

```bash
# Linux
export LANG=zh_CN.UTF-8
java -jar protege-desktop/target/protege-*.jar

# Windows
set JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
java -jar protege-desktop/target/protege-*.jar

# macOS
java -Duser.language=zh -Duser.country=CN -jar protege-desktop/target/protege-*.jar
```

### 5.3 IDE 测试

在 Eclipse 或 IntelliJ IDEA 中：
1. 设置运行配置，添加 JVM 参数：`-Duser.language=zh -Duser.country=CN`
2. 运行项目验证中文显示

---

## 六、维护建议

### 6.1 保持与国际社区同步

1. **定期同步上游**：定期从主分支拉取更新
2. **使用有意义的键名**：确保键名描述清晰，便于定位
3. **记录翻译规则**：创建翻译风格指南保持一致性

### 6.2 翻译质量保证

1. **术语统一**：建立术语表，确保同一术语翻译一致
   - OWL → OWL（不翻译）
   - Class → 类
   - Property → 属性
   - Individual → 实例
   - Ontology → 本体
   - Axiom → 公理

2. **避免过长翻译**：中文翻译应尽量简短，避免界面溢出

3. **测试实际显示**：在多种分辨率下测试界面

### 6.3 建议的术语表

| 英文 | 中文 | 说明 |
|------|------|------|
| Class | 类 | OWL 类 |
| Object Property | 对象属性 | OWL 对象属性 |
| Data Property | 数据属性 | OWL 数据属性 |
| Annotation Property | 注解属性 | OWL 注解属性 |
| Individual | 实例 | OWL 个体 |
| Ontology | 本体 | OWL 本体 |
| SubClass | 子类 | 类的层次关系 |
| Equivalent Class | 等价类 | 等价类公理 |
| Disjoint | 互斥 | 互斥关系 |
| Reasoner | 推理机 | 推理引擎 |

---

## 七、常见问题

### Q1: 如何添加新的翻译键？

1. 在 `messages.properties` 中添加英文键值对
2. 在 `messages_zh.properties` 中添加中文翻译
3. 在 Java 代码中使用 `Messages.getString("键名")` 调用

### Q2: 如果找不到翻译，显示什么？

`Messages.getString()` 方法在找不到键时会返回键名本身，确保界面不会显示空白。

### Q3: 如何支持其他语言？

创建新的属性文件，如：
- `messages_fr.properties`（法语）
- `messages_de.properties`（德语）
- `messages_ja.properties`（日语）

### Q4: 第三方插件如何汉化？

第三方插件通常有自己的 `plugin.xml`，需要单独汉化。建议向插件作者提交翻译贡献。

---

## 八、参考资源

- [Java 国际化官方文档](https://docs.oracle.com/javase/tutorial/i18n/)
- [Eclipse 插件国际化](https://help.eclipse.org/latest/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/resInt_Externalize_Strings.htm)
- [Protege 项目主页](http://protege.stanford.edu)

---

## 九、下一步工作

1. ✅ 创建国际化框架
2. ⬜ 完整修改 plugin.xml（core + owl）
3. ⬜ 完整修改 Java UI 代码
4. ⬜ 添加缺失的翻译键值
5. ⬜ 测试验证
6. ⬜ 创建 Git 分支并维护

---

*文档版本：1.0*
*创建日期：2024*
*最后更新：完成国际化框架建立*
