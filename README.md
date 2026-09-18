# Protege Desktop (Chinese Localization Fork)

[Protege](https://protege.stanford.edu) is a free, open-source ontology editor that supports the latest [OWL 2.0 standard](http://www.w3.org/TR/owl2-overview/). Protege has a pluggable architecture, and many [plugins](https://protegewiki.stanford.edu/wiki/Protege_Plugin_Library) for different functionalities are available.

To read more about **Protege's features**, please visit the Protege [home page](https://protege.stanford.edu).

The latest version of Protege can be [downloaded](https://protege.stanford.edu/software.php#desktop-protege) from the Protege website, or from [github](https://github.com/protegeproject/protege-distribution/releases).

If you would like to contribute to the Protege Project please see our [contributing guide](https://github.com/protegeproject/protege/blob/master/CONTRIBUTING.md)

The [Developer Documentation](https://github.com/protegeproject/protege/wiki/Developer-Documentation) may be found on the wiki.

**Looking for support?** Please ask questions on the [protege-user](https://protege.stanford.edu/support.php) or [protege-dev](https://protege.stanford.edu/support.php) mailing lists. If you found a bug or would like to request a feature, you may also use [this issue tracker](https://github.com/protegeproject/protege/issues).

Protege is released under the [BSD 2-clause license](https://raw.githubusercontent.com/protegeproject/protege/master/license.txt).

Instructions for [building from source](https://github.com/protegeproject/protege/wiki/Building-from-Source) are available on the the wiki.

---

## 🇨🇳 中文汉化计划 (Localization Plan)

本项目分支正致力于开展 **Protege Desktop 的全面中文汉化** 工作，方便国内开发者和本体工程学习者使用。

### 📌 汉化路线图
1. **核心模块划分**：
   - `protege-common` & `protege-editor-core`：负责基础 UI 框架、顶部菜单栏及通用对话框的汉化。
   - `protege-editor-owl`：负责 OWL 本体核心编辑视图（类层次结构、对象属性、数据属性、个体及 SWRL 规则等面板）的汉化适配。
2. **实施方案**：
   - 采用标准 Java 国际化 (`ResourceBundle` / `.properties`) 属性文件与 UI 文本适配机制。
   - 确保在 Maven 编译打包（如各个平台的 zip/tar.gz 产物）及 Swing 运行时中完美支持中文字符。

欢迎关注本仓库的后续更新与汉化进展！

---

Below is the original README:

# Protege Desktop

[Protege](https://protege.stanford.edu) is a free, open-source ontology editor that supports the latest [OWL 2.0 standard](http://www.w3.org/TR/owl2-overview/). Protege has a pluggable architecture, and many [plugins](https://protegewiki.stanford.edu/wiki/Protege_Plugin_Library) for different functionalities are available.

To read more about **Protege's features**, please visit the Protege [home page](https://protege.stanford.edu).

The latest version of Protege can be [downloaded](https://protege.stanford.edu/software.php#desktop-protege) from the Protege website, or from [github](https://github.com/protegeproject/protege-distribution/releases).

If you would like to contribute to the Protege Project please see our [contributing guide](https://github.com/protegeproject/protege/blob/master/CONTRIBUTING.md)

The [Developer Documentation](https://github.com/protegeproject/protege/wiki/Developer-Documentation) may be found on the wiki.

**Looking for support?** Please ask questions on the [protege-user](https://protege.stanford.edu/support.php) or [protege-dev](https://protege.stanford.edu/support.php) mailing lists. If you found a bug or would like to request a feature, you may also use [this issue tracker](https://github.com/protegeproject/protege/issues).

Protege is released under the [BSD 2-clause license](https://raw.githubusercontent.com/protegeproject/protege/master/license.txt).

Instructions for [building from source](https://github.com/protegeproject/protege/wiki/Building-from-Source) are available on the the wiki.
