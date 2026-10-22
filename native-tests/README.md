# GIF JNI native smoke APK

这是独立的 Android application 工程，用真实 Android Bitmap 检查 GIF JNI 的加载、
注册、解码、渲染与生命周期。工程不求值仓库根目录或 `library` 的旧 Gradle 配置，
也不依赖 `regression-tests`。唯一的 runner 是平台 `android.app.Instrumentation`，
没有 JUnit、AndroidX、外部 runner 或额外的 androidTest APK。

## 构建

需要 JDK 11、Gradle **7.5**、AGP **7.4.0**、Android SDK platform **28** 和
build-tools **30.0.3**。依赖已缓存时可离线构建。SDK 路径通过环境变量提供，不需要
创建或提交 `local.properties`，也不需要修改仓库原有 wrapper。

在仓库根目录执行：

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 11)  # macOS；其他系统选用 JDK 11
export ANDROID_HOME="$HOME/Library/Android/sdk" # 换成实际 SDK 目录
export GRADLE_75=/path/to/gradle-7.5/bin/gradle

"$GRADLE_75" -p native-tests --offline --no-daemon assembleDebug --console=plain
```

本机已缓存的 Gradle 可设置为：

```sh
export GRADLE_75="$HOME/.gradle/wrapper/dists/gradle-7.5-bin/9tzbf1ys39bmx06cf595mz8hp/gradle-7.5/bin/gradle"
```

产物是 `native-tests/build/outputs/apk/debug/SuperTextView-native-tests-debug.apk`。
`sourceSets` 直接编译 `library/src/main/java/com/coorchice/library/gifdecoder/JNI.java`，
仅增加此目录的 runner；native 库直接来自 `library/src/main/jniLibs`，不使用替身或副本源码。
因此，生产 `.so` 更新后应重新执行 `assembleDebug` 再安装 APK。

测试 APK 的 `minSdk=21` 仅用于调用平台 `android.system.Os`；不改变 library 的兼容范围。
`targetSdk=28`、`compileSdk=28`。所有现有 ABI 库都会打包，无需为每个 ABI 修改配置。

## 在已选定的设备上执行

由开发者选择并准备设备；下面的命令不创建、启动或关闭 emulator。
始终明确指定序列号，避免把结果归到错误的设备：

```sh
export ANDROID_SERIAL=emulator-5554 # 换成 adb devices -l 中确认的设备
ADB="$ANDROID_HOME/platform-tools/adb"
"$ADB" -s "$ANDROID_SERIAL" install -r native-tests/build/outputs/apk/debug/SuperTextView-native-tests-debug.apk
"$ADB" -s "$ANDROID_SERIAL" shell am instrument -w -r \
  com.coorchice.nativetests/com.coorchice.nativetests.NativeSmokeInstrumentation
```

验证 16 KB 运行环境时应加上硬性页大小检查：

```sh
"$ADB" -s "$ANDROID_SERIAL" shell am instrument -w -r -e requirePageSize 16384 \
  com.coorchice.nativetests/com.coorchice.nativetests.NativeSmokeInstrumentation
```

runner 输出 `Build.SUPPORTED_ABIS`、进程 `os.arch`、SDK、nativeLibraryDir、
`Os.sysconf(OsConstants._SC_PAGESIZE)` 的字节数，以及可读时的 `/proc/self/smaps`
`KernelPageSize`。sysconf 从 API 21 起可用；smaps 访问失败会明确标注，不掩盖 sysconf 错误。
`requirePageSize` 不匹配会让 environment 用例失败。4 KB 上通过的结果不能证明 16 KB 兼容。

完整执行包含 **47 个用例**。每例都有 `START` 和 `PASS` / `FAIL`，最后返回
`passed`、`failed`、`total` 与 `Native smoke: passed=... failed=... total=47`。
全部通过时 `INSTRUMENTATION_CODE: -1`、`failed=0`；Java 断言失败返回 code 0 并继续其余用例。
`adb` 的 shell 退出码本身不代表测试通过，必须检查最终统计、总数和 instrumentation code。

只复现一个用例时，使用精确名称：

```sh
"$ADB" -s "$ANDROID_SERIAL" shell am instrument -w -r -e test openBytesTruncatedScreen \
  com.coorchice.nativetests/com.coorchice.nativetests.NativeSmokeInstrumentation
"$ADB" -s "$ANDROID_SERIAL" logcat -d -s GifNativeSmoke AndroidRuntime DEBUG
```

例如 `sequentialPixels`、`strictFrameControls`、`nonStrictFrameControls`、`copyOwnership`、
`gif87BytesNoGcb`、`signatureBytesEmpty`、`signatureBytesShort5`、`openFileTruncatedFrame`。
非法名称返回 setup 失败，不会产生零用例的假通过。单例执行不包含 environment 用例，
验证页大小应先执行完整集合，或单独执行 `-e test environment -e requirePageSize 16384`。

## 已知像素 fixture 与断言

`src/main/assets/known-pixels.gif` 是 3 × 2 的两帧 GIF89a：

- 第一帧覆盖整幅图；第二帧只有 `(left=1, top=0, width=2, height=2)` 的矩形。
- 全局调色板为透明/黑、红、绿、蓝；第二帧使用不同次序的**局部调色板**。
- 两帧均使用 disposal 1（保留画布）、透明索引 0；延时分别是 **100 ms、200 ms**。
- 矩阵按从上到下、从左到右排列，`T=0x00000000`、`R=0xffff0000`、
  `G=0xff00ff00`、`B=0xff0000ff`：

```text
frame 0       frame 1（合成后）
R T B         R G B
G R T         G R B
```

预期是手工声明的图像，不从 JNI 或 native 实现计算。所有像素，包括透明像素 alpha，
均精确比较。`plain-87a.gif` 是没有 GCB 的 3 × 2 单帧 GIF87a；相同像素索引中的黑色
必须是不透明 `0xff000000`，帧时长为 0，`updateFrame` 返回 -1。

可以用标准库脚本重建或检查 fixture 字节，也可以用独立 Pillow 解码器验证图像：

```sh
python3 native-tests/tools/generate_fixtures.py --check
# 可选 host 工具，需要 Pillow；APK/Gradle 不依赖 Pillow。
python3 native-tests/tools/check_fixtures.py
```

fixture 的 SHA-256：

```text
a50b09b5b816977ed220a9a36638e04136c4e9485426d970dada21ed5e1244bb  known-pixels.gif
87918166e0f034a88336c928a7f0b2c116cf37462e0be4f2ee289c8428c327d3  plain-87a.gif
```

## 覆盖范围与边界

- 真实 JNI 类初始化触发 `System.loadLibrary("GifLib")` / `JNI_OnLoad`；声明名称和数量
  必须与 19 个方法一致。完整集合实际调用全部 19 个方法，不仅反射检查名称。
- `openBytes` / `openFile` 的宽高、帧数、平均帧时长与初始位置；文件路径也进行真实渲染。
  `unicodeFilePath` 使用含中文和 emoji 的 `图像-😀-….gif` 文件名验证 signature、打开与两帧像素，
  防止 JNI modified UTF-8 与文件系统 UTF-8 不一致导致路径打不开。
  此 API 暴露平均时长，所以 100 / 200 ms fixture 的 `getFrameDuration` 应为 150 ms，
  不是每帧延时。`updateFrame` 返回值减去实际绘制耗时，允许范围 `[0, frameDuration]`。
- 顺序绘制、回绕、时长修改、strict 开关、goto/get/set、负数和超界帧号裁剪；
  strict getFrame 与非 strict getFrame 均检查不改变播放位置和下一帧。
  非 strict 跳到局部帧前保留其前驱画布；strict 从透明画布重建合成。
- `copy` 的独立指针、位置、播放、strict 和时长；先 `copyDestroy`，再验证原实例仍能渲染，
  最后 `destroy` 原实例。不使用已释放指针、不重复释放、不测试原实例先释放后继续用 copy。
- bytes/file signature 对 null、空、1–5 字节、非 GIF、缺失文件严格返回 false。
  bytes/file open 对 null、空、短数据、非 GIF、仅 header、仅 screen、截断帧、
  缺 trailer、缺失文件严格返回 0；不会把异常、非零指针或 native 崩溃当成功。

老 native 的短 signature 可能抛越界异常；忽略 `DGifSlurp` 失败可能在无帧输入上除零或崩溃。
这些严格用例保持启用，没有 expect-crash、跳过或容错接受部分 GIF。Java 能统计断言/异常，
但 SIGSEGV/SIGFPE 会直接终止进程，无法承诺最终统计；没有 summary 就不算通过。
崩溃前的最后一个 `START` 可定位输入，再用单例命令和 logcat/tombstone 继续排查。

构建成功和 host fixture 核验只证明 APK 可生成、预期图像独立可信；JNI/native Bitmap
断言必须在设备上运行才有结论。每次报告设备 ABI、页大小、APK 来源和最终统计。
本 APK 使用压缩并提取的 native 库，验证实际 ELF 加载和渲染，不声称检验 APK 内未压缩
`.so` 的 16 KB ZIP 对齐。它也不是 GIF 格式全量、内存泄漏或线程安全测试；画布回绕前
显式清空，不额外声称覆盖所有 disposal 模式、跨循环合成、非 strict 全量重建或失效指针行为。
