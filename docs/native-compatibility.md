# GIF native compatibility

The GIF decoder is built from source in [`library/src/main/cpp`](../library/src/main/cpp).
The JNI class and its 19 registered methods are unchanged.

Starting with **v3.2.7**, a single version includes all four ABIs below; the old
`.64` and `.99` suffixes describe historical release variants only. See the
[v3.2.7 changelog](../CHANGELOG.md) for the Java fixes and release scope.

## Supported ABIs and Android versions

| ABI | Native minimum API | ELF page alignment |
| --- | --- | --- |
| armeabi-v7a | 19 | 16 KB |
| arm64-v8a | 21 | 16 KB |
| x86 | 19 | 16 KB |
| x86_64 | 21 | 16 KB |

The library's Java minimum SDK remains **API 19**. The optional test APK uses API 21
for the platform page-size query; that does not change the production library.
There is no additional `libc++_shared.so` dependency.

## Why a rebuild was necessary

The previous release did not contain an x86_64 library. Its 32-bit ELF LOAD
segments were not congruent modulo 16 KB. The old arm64 binary had 64 KB LOAD
alignment, but its GNU_RELRO boundary shared a 16 KB page with writable data.
LOAD alignment alone therefore was not a sufficient safety check.

The rebuilt binaries use both `max-page-size=16384` and
`common-page-size=16384`, with RELRO isolated from writable data. This permits
loading on both 4 KB and 16 KB page-size systems.

The source restoration also fixes the JNI byte-reader callback signature,
validates GIF decode completion and frame bounds before rendering, supplies
valid no-GCB defaults for GIF87a, handles short signatures without reading past
the input, and preserves normal UTF-8 filenames including supplementary Unicode.
The public Java/native API remains the same.

## Rebuilding and checking

Use **NDK 25.0.8775105** (r25), which still supports API 19. Newer NDKs that drop
API 19 are rejected for the 32-bit build instead of silently raising the minimum.
CMake 3.10 or newer is sufficient:

```sh
python3 tools/build_native.py --ndk "$ANDROID_NDK_HOME" --cmake /path/to/cmake
python3 tools/check_native_binaries.py
python3 -m unittest discover -s tools/tests -v
```

The script builds and validates all requested ABIs before replacing the shipped
libraries, statically links the C++ runtime, hides unused archive symbols and
strips unneeded symbols. The pure-Python checker verifies architecture, LOAD
alignment/congruence and RELRO/writable-page separation, not just a linker flag.
The original giflib license is retained in `COPYING.giflib`.

## Building the production AAR

The independent `publishing/` build uses **JDK 11 or 17**, a **Gradle 7.5** wrapper,
an Android SDK and **Python 3**. From the repository root:

```sh
./publishing/gradlew -p publishing publishToMavenLocal
```

It packages only production library sources/resources and the four shipped native
libraries, excluding test code, fixtures and test-only resources. It does not
evaluate or migrate the legacy demo build. The source-build gate checks committed
native libraries before building and the actual AAR after packaging. Python is
needed for those checks, **not by applications consuming an AAR**. This command
publishes to local Maven only; it does not publish to JitPack or a remote repository.
The v3.2.7 AAR embeds the required JNI consumer keep rule for R8/ProGuard; older
AARs or custom pipelines that omit consumer rules still need the manual rule in
the [README](../README.md).

## Runtime verification

[`native-tests`](../native-tests/README.md) builds an independent APK against the
real JNI class and these exact libraries. Its 47 tests exercise all 19 native
methods, known frame pixels and transparency, local palettes/frame offsets,
frame controls, independent copy playback, GIF87a, malformed input and file paths.

On 2026-10-22 the rebuilt arm64 library passed all 47 tests on an API 36 emulator
with `sysconf` and `smaps` both reporting 4 KB pages. The 43 Java regression tests
also passed. This 4 KB result is not a substitute for 16 KB runtime verification.

The `Native compatibility` workflow rebuilds all four ABIs and runs the APK on
API 35 x86_64 images with both `google_apis` (4 KB) and `google_apis_ps16k` (16 KB).
It requires the expected runtime page size, a complete passing test summary and
`INSTRUMENTATION_CODE: -1`; an adb shell exit code alone is not accepted. See the
PR checks for the actual CI results.

The initial CI run passed both x86_64 jobs (47 tests each):
https://github.com/chenBingX/SuperTextView/actions/runs/39717058777 .
The official x86_64 `ps16k` image reports `sysconf` page size **16384** while
`smaps` reports a **4 KB kernel page size**. This exercises Android's simulated
16 KB environment, not a real ARM64 16 KB kernel. ELF LOAD/RELRO validation is
performed separately for all four shipped ABIs.

On 2026-10-22 the official API 35 **ARM64 `ps16k` emulator with a real 16 KB
kernel** also passed all 47 tests in **10 consecutive runs (470 executions)**.
Both the app's `sysconf` and `/proc/self/smaps` reported 16 KB, and the device
crash buffer was empty after the runs. This closes the native 16 KB-kernel
runtime gap; it is virtual-device testing, not physical OEM-device testing.
The APK contained byte-identical copies of the shipped native libraries.
See the [device evidence and raw test log](test-results/arm64-16k-2026-10-22/README.md)
for the image fingerprint, kernel, APK/library hashes, reproduction command and
per-run results. Use `--kernel-page-size 16384` to require this real-kernel check.

The suite is not a complete GIF conformance, fuzzing, lifetime/thread-safety or
memory-leak test. In particular, copied GIF instances borrow their source's
decoded data; the source must outlive its copies, as required by the existing API.

### Current Java/UI and packaging evidence

The historical native runs above retain their original revisions and test totals;
they are not new tests of a JitPack release. The subsequent
[remaining-issue investigation](remaining-issues-2026-10-22.md) records **57/57
Java tests**, **26/26 Python tests**, successful debug/release AAR validation and
**15/15 framework/Skia UI tests** on the API 35 ARM64 real-16-KB-kernel virtual
device. Its application reports both `sysconf=16384` and `smaps KernelPageSize=16 kB`.
See the [raw UI run](test-results/remaining-issues-2026-10-22/device-run.txt) and
[host totals](test-results/remaining-issues-2026-10-22/junit-summary.json).
These are not physical OEM/API-31 coverage or quantified CPU/GPU performance
results, and do not establish the original causes of #91, #113, #77 or #107's
reported 3.2.3 failure.

## Application packaging

ELF alignment and APK ZIP alignment are separate requirements. These libraries
address ELF loading. Applications packaging **uncompressed** native libraries
should use AGP **8.5.1+** and verify the final APK with:

```sh
zipalign -c -P 16 -v 4 app.apk
```

The standalone smoke APK intentionally extracts compressed libraries on install;
it verifies ELF loading and real rendering, not uncompressed APK ZIP alignment.
Applications using older AGP need a compatible extracted-library packaging
configuration. Replacing this library does not align other third-party `.so`
files in the same application or establish that the whole application is 16 KB
compatible.

Starting with **v3.2.7**, these rebuilt native libraries are included in the
unified four-ABI AAR; older published versions do not acquire this change
retroactively. The coordinate is `com.github.chenBingX:SuperTextView:v3.2.7`.
JitPack builds the Maven artifact on demand from the release tag; consult the
[build status](https://jitpack.io/#chenbingx/supertextview) if resolution is waiting.
Local AAR checks and the historical device records above are separate from an
external Maven publication.
