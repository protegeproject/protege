# Protege 中文语言包 (Protege-i18n-zh)

为 [Protégé Desktop](https://protege.stanford.edu/) 提供的中文语言包补丁，让用户能够以中文界面使用 Protégé 本体编辑器。

## 项目特点

- **独立维护**：作为独立项目存在，无需修改 Protégé 官方代码
- **增量更新**：跟随 Protégé 官方版本同步更新
- **易于部署**：用户只需下载覆盖包，使用中文启动脚本即可
- **方案 A（JVM 参数启动）**：最简单的方式，对 IT 小白友好

## 支持版本

| Protégé 版本 | 汉化包版本 |
|--------------|-----------|
| 5.6.10 | 5.6.10 |
| 5.6.11+ | 同步更新中 |

## 快速开始

### 方式一：使用中文启动脚本（推荐）

1. 下载最新版本的语言包
2. 解压到您的 Protégé 安装目录
3. 使用 `protege-zh.bat`（Windows）或 `protege-zh.sh`（macOS/Linux）启动 Protégé

### 方式二：手动设置

在启动 Protégé 前设置 JVM 参数：

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

## 下载

- [GitHub Releases](https://github.com/protege-i18n-zh/releases)
- [Gitee 镜像](https://gitee.com/protege-i18n-zh/releases)（国内加速）

## 项目结构

```
protege-i18n-zh/
├── README.md
├── VERSION_GUIDE.md
├── CHANGELOG.md
├── docs/
│   ├── INSTALL.md          # 安装指南
│   └── TRANSLATION_TERMS.md # 术语表
├── scripts/
│   ├── protege-zh.bat      # Windows 启动脚本
│   ├── protege-zh.sh       # macOS/Linux 启动脚本
│   └── build.sh            # 构建脚本
└── i18n/                   # 语言包文件
    ├── protege-editor-core/
    └── protege-editor-owl/
```

## 翻译内容

- 菜单项（文件、编辑、视图、重构、工具等）
- 标签页（类、属性、实例等）
- 工具提示
- 对话框和消息
- 首选项面板

## 参与贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

本项目遵循 Protégé 相同的许可证。

## 致谢

- [Protégé Project](https://protege.stanford.edu/) - 强大的本体编辑器
- 所有翻译贡献者

---

*最后更新：2024*
