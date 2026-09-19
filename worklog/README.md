# Protégé汉化实施工作日志

- [Protégé汉化实施工作日志](#protégé汉化实施工作日志)
  - [2026-09-19](#2026-09-19)
    - [Java 21 迁移与兼容性适配](#java-21-迁移与兼容性适配)
    - [依赖对齐与 OSGi 容器 Wiring 修复](#依赖对齐与-osgi-容器-wiring-修复)
    - [运行时配置与打包优化](#运行时配置与打包优化)
  - [2026-09-18](#2026-09-18)
    - [汉化路径与模块的定位分析](#汉化路径与模块的定位分析)
    - [Protégé的界面文本初步分析](#protégé的界面文本初步分析)
    - [汉化（国际化）的两种技术方案的比较考量](#汉化国际化的两种技术方案的比较考量)
      - [方法一：直接修改源码中的硬编码文本](#方法一直接修改源码中的硬编码文本)
      - [方法二：引入标准的 i18n 资源束机制](#方法二引入标准的-i18n-资源束机制)
    - [本地分支管理](#本地分支管理)

## 2026-09-19

### Java 21 迁移与兼容性适配
- **Toolchain & Compiler:** 将 Maven 编译插件 (`maven-compiler-plugin`) 的 `source` 和 `target` 正式提升并锁定至 **Java 21**。
- **JPMS 模块参数优化:** 在 `run.bat` 中通过 `--add-opens` 引入了必要的 Java 模块反射访问权限（如 `java.desktop`、`java.xml`、`java.base` 等），彻底解决了高版本 JDK 下的反射及访问限制问题。
- **Swing 渲染性能修复:** 增加了 JVM 编译指令排除参数 `-XX:CompileCommand=exclude,javax/swing/text/GlyphView,getBreakSpot`，确保在现代 JVM 运行时中 Swing 渲染组件的高效稳定。

### 依赖对齐与 OSGi 容器 Wiring 修复
- **依赖版本管理:** 在 `pom.xml` 中统一并锁定了核心 Eclipse Platform 组件版本：
  - `org.eclipse.osgi` (v3.18.0)
  - `org.eclipse.equinox.registry` (v3.11.0)
  - `org.eclipse.equinox.common` (v3.16.0)
- **解决 OSGi Bundle 依赖解析错误:** 修复了 Apache Felix 容器启动时由于缺少服务包（如 `org.eclipse.osgi.service.localization`）导致的 `BundleException` 及 `org.eclipse.equinox.registry` 解析失败问题。
- **类路径初始化优化:** 调整了启动类路径顺序，确保 `lib/org.eclipse.osgi.jar` 能够正确引导 OSGi 系统包的加载，避免类加载死锁。

### 运行时配置与打包优化
- **Felix 扩展包配置:** 完善了 `config.xml` 中的 `org.osgi.framework.system.packages.extra` 属性，显式导出了 Equinox 内部适配器、解析器以及本地化服务包。
- **启动脚本及构建链路完善:** 优化了 Windows 下的 `run.bat` 运行环境，适配了字符编码 (`-Dfile.encoding=utf-8`)、日志配置（`logback-win.xml`）以及插件动态扫描目录。

## 2026-09-18

### 汉化路径与模块的定位分析

Protégé的界面文本主要分布在不同的Maven模块中，考虑按照组件的依赖层次由底向上逐步推进汉化进程：

- `protege-common` / `protege-editor-core`：包含基础UI框架、菜单栏、通用对话框和插件管理器的文本
- `protege-editor-owl`：包含本体编辑的核心视图（如类层次结构、对象属性、数据属性、个体、SWRL规则等OWL的专用面板）
- `protege-desktop`：桌面端启动和组装逻辑
- 第三方/自定义插件：对这些标准插件的汉化语言包将单独考虑

### Protégé的界面文本初步分析

Protégé官方采用的是Java Swing架构，但并没有看到源码中全面实现了标准的Java国际化资源束机制（如 `.properties` 文件机制）来统一管理所有的UI文本。

相反，Protégé的大量UI组件、菜单项和标签文本等基本上都是在Java代码中直接使用硬编码（Hard-Coded）初始化的。

由于文本分散在各个子模块的Swing代码中，初步方法是在项目中通过全局搜索（`Ctrl+Shift+F` 或 `Cmd+Shift_F`）来定位和熟悉具体的UI文本。

### 汉化（国际化）的两种技术方案的比较考量

#### 方法一：直接修改源码中的硬编码文本

此方法最为直接，立竿见影，但如果未来有合并到上游的考虑则更新时出来可能的冲突会比较麻烦。

步骤举例：

1. 找到对应的Java UI类文件（通常继承自 `JPanel`、`AbstractAction` 或使用 `JFrame`）
2. 以`Add subclass`这个标签文本为例，通过全局搜索，可以定位到文件`protege-editor-owl\src\main\java\org\protege\editor\owl\ui\view\cls`里面的`ToldOWLClassHierarchyViewComponent.java`的第64行
   ```java
   new AbstractOWLTreeAction<OWLClass>("Add subclass",
   ```
   可以手工替换为`添加子类`。
3.这种方法对Swing界面最为有效，改为直接重新编译打包即可看到效果并发布。

#### 方法二：引入标准的 i18n 资源束机制

这种方式相对工程量肯定更大，但从当前的Java趋势上来看能够实现更加优雅与易于维护的目标，并为汉化后增添其他更多语言打下基础。

大致的步骤设想：

1. 在各个模块的 `src/main/resources` 下建立国际化目录（通常标准化命名为 `i18n`）
2. 创建 `Messages_zh_CN.properties` 文件，内容从如下示例开始：
   ```java
   action.add.subclass=添加子类
   menu.file=文件
   ...
   ```
3. 编写或重构部分UI代码，使其能够通过 `ResourceBundle` 的方式来动态加载文本。

### 本地分支管理

建立本地分支 `protege-cn` 来进行汉化测试。