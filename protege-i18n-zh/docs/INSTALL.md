# 安装说明

本指南详细说明如何安装 Protege 汉化补丁包。

## 前置要求

- 已安装 Protégé Desktop 5.6.10 或更高版本
- 下载最新的汉化包（protege-i18n-zh-{version}.zip）

## 安装步骤

### 第一步：下载汉化包

从以下地址下载最新版本：

- GitHub: https://github.com/protege-i18n-zh/releases
- Gitee: https://gitee.com/protege-i18n-zh/releases

### 第二步：解压汉化包

将下载的 `protege-i18n-zh-{version}.zip` 解压到任意目录。

解压后应包含：
```
protege-i18n-zh-{version}/
├── i18n/                    # 语言包文件
│   ├── protege-editor-core/
│   └── protege-editor-owl/
├── docs/
│   ├── INSTALL.md
│   └── TRANSLATION_TERMS.md
├── protege-zh.bat          # Windows 启动脚本
└── protege-zh.sh           # macOS/Linux 启动脚本
```

### 第三步：复制语言包文件

将 `i18n` 文件夹中的内容复制到 Protégé 安装目录：

**Windows:**
```batch
# 假设 Protégé 安装在 C:\Program Files\Protege_5.6.10
# 将 i18n/ 下的文件复制到对应位置
```

**macOS:**
```bash
# 假设 Protégé 安装在 /Applications/Protege_5.6.10.app
# 使用右键"显示包内容"进入 Contents/Resources
```

**Linux:**
```bash
# 假设 Protégé 安装在 ~/protege-5.6.10
```

### 第四步：使用中文启动脚本

#### Windows 用户

1. 双击运行 `protege-zh.bat`
2. Protégé 将以中文界面启动

#### macOS 用户

1. 打开终端
2. 进入汉化包目录
3. 运行 `./protege-zh.sh`
4. Protégé 将以中文界面启动

#### Linux 用户

1. 打开终端
2. 进入汉化包目录
3. 运行 `./protege-zh.sh`
4. Protégé 将以中文界面启动

## 手动设置（备选方案）

如果不使用启动脚本，您可以手动设置 JVM 参数：

### Windows

在命令提示符中：
```batch
set JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
"C:\Program Files\Protege_5.6.10\Protege.exe"
```

### macOS

在终端中：
```bash
export JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
open /Applications/Protege_5.6.10.app
```

### Linux

在终端中：
```bash
export JAVA_TOOL_OPTIONS="-Duser.language=zh -Duser.country=CN"
cd ~/protege-5.6.10
./run.sh
```

## 验证安装

启动 Protégé 后，检查以下内容：

- [ ] 菜单栏显示中文
- [ ] 标签页显示中文
- [ ] 工具提示显示中文
- [ ] 对话框显示中文

## 卸载

如需恢复英文界面：

1. 删除复制到 Protégé 安装目录的语言包文件
2. 不再使用中文启动脚本
3. 恢复正常启动方式

## 常见问题

### Q: 汉化没有生效？

确保：
1. 已正确复制语言包文件
2. 使用了中文启动脚本或设置了 JVM 参数
3. Protégé 版本与汉化包版本匹配

### Q: 部分内容仍显示英文？

这是正常的，某些第三方插件可能未包含在汉化包中。

### Q: 如何检查 Protégé 版本？

启动 Protégé，点击菜单"帮助 > 关于 Protégé"

## 获取帮助

- GitHub Issues: https://github.com/protege-i18n-zh/issues
- 邮箱: support@protege-i18n-zh.example.com
