# 息 · STILL

一款以留白、月蚀光影和连续秒轨为核心的沉浸式 Android 时钟。没有闹钟、倒计时、通知、账号或网络权限，只保留时间本身。

## 特点

- 月蚀、晨雾、余烬三套单色光影
- 原生 Canvas 月面、呼吸光晕与连续椭圆秒轨
- 全屏沉浸模式与可一键进入、随时退出的纯净模式
- 12/24 小时、秒轨、常亮设置，使用 DataStore 本地保存
- 自适应横竖屏、窄屏、大字体与 TalkBack
- 前台秒轨约 30fps、静态光影 10fps，后台自动暂停动画

## 安装

支持 Android 8.0（API 26）及以上。正式签名 APK 可从 [GitHub Releases](https://github.com/boluobao101/still-clock/releases) 下载。

## 技术栈

- Kotlin 2.0.21
- Jetpack Compose / Material 3
- Preferences DataStore
- Android Gradle Plugin 8.13
- Gradle 9.5
- minSdk 26 / targetSdk 36 / compileSdk 36

## 本地构建

需要 JDK 17、Android SDK 36，以及兼容版本的 Android Studio。首次打开项目时，让 Android Studio 自动生成本机 `local.properties`，或设置标准 `ANDROID_HOME` 环境变量。

Windows：

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat assembleRelease
```

macOS / Linux：

```shell
./gradlew assembleDebug
./gradlew assembleRelease
```

未配置签名时，Release 任务生成未签名包。需要生成自己的签名发布包时：

1. 将 `keystore.properties.example` 复制为 `keystore.properties`。
2. 创建自己的 PKCS12 密钥库，并填写对应路径、别名和密码。
3. 重新执行 `assembleRelease`。

```shell
keytool -genkeypair -v -storetype PKCS12 -keystore still-release.keystore -alias still-clock -keyalg RSA -keysize 3072 -validity 10000
```

`keystore.properties`、`*.keystore`、`local.properties` 和构建产物均已加入 `.gitignore`。请妥善保管自己的发布私钥，不要提交到仓库。

## 测试与检查

```powershell
.\gradlew.bat testDebugUnitTest testReleaseUnitTest compileDebugAndroidTestKotlin lintRelease
```

单元测试覆盖午夜、中午、12/24 小时与中英文日期格式；仪器测试覆盖控制层、纯净模式和设置持久化。仪器测试需要连接设备或模拟器，可用 `connectedDebugAndroidTest` 执行。

## 隐私

应用不申请网络权限，不收集、上传或分享任何数据。所有设置仅保存在设备本地。

## 许可证

本项目采用 [MIT License](LICENSE)。你可以自由使用、修改和分发，但需保留版权与许可证声明。
