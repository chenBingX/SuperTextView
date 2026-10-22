# Remaining open-issue investigation — 2026-10-22

Base: `01bf71c28435bb3b81c4b033a78a8f6ae5bbb7f4` (merged PR #126).

## #89 — confirmed no-op background submissions

`drawStrokeLine` submitted a path whenever the stroke width was positive,
including fully transparent colors. `drawSolid` always submitted its default
transparent fill. The fix skips these invisible submissions but preserves path,
corner and FILL-paint preparation: rounded image backgrounds and PressAdjuster
reuse that state. Visible/translucent colors, gradients and the historical
zero-gradient-endpoint fallback must remain unchanged.

Regression tests record commands from attached, measured views, including alpha
0/1/128/255, zero-width strokes, repeated frames, pressed-state resize/corners,
image-background fills and transparent/visible setter transitions. Command counts
are **not** a measurement of GPU overdraw layers or frame-time improvement.

## #93 — screenshot matched to an existing historical fix

The screenshot shows `E/SuperTextView-onDraw(L:342): canvas.h = ...`.
`d55309a^` has exactly `LogUtils.e("canvas.h = " + canvas.getHeight())` at line 342,
and its LogUtils defaults DEBUG to true. Public JitPack `3.1.6.2` and `v3.1.6.2`
AAR bytecode corroborates the call and line number, rather than merely assuming
that today's default-off DEBUG makes the old report invalid.

`d55309a` commented out this log, `dbb843a` removed it, and `58ae627` separately
changed DEBUG to false. Downloaded `3.2.3` and `3.2.5` AARs have no `canvas.h`
class constant. Current device tests repeat drawing 100 times and the host checks
logcat for the exact historical diagnostic. This does not promise silence from
all Android/system logs or real exceptional/fatal conditions.

## #107 — bad release confirmed, original 3.2.3 root cause still unknown

| Artifact | ARMv7 `libGifLib.so` size | ZIP CRC |
| --- | ---: | --- |
| Public `3.2.3` / `v3.2.3` AAR | 124768 bytes | valid |
| Public `3.2.5` / `v3.2.5` AAR | **0 bytes** | valid |

The empty 3.2.5 library is also present in its Git tag. A valid ZIP checksum does
not establish a valid native payload. The current library has four nonempty,
architecture/16KB/RELRO-checked ABIs, already loaded in the separate native suite.

New protection is shared by the legacy library and standalone regression builds:
`gradle/native-validation.gradle` checks committed libraries before `preBuild`,
and validates the actual AAR after `bundle* AAR` tasks. The CLI's `--aar` mode
checks the ABI set, duplicate native entries, ZIP CRC, empty/truncated ELF,
architecture, LOAD bounds/alignment and RELRO, and prints sizes/SHA-256.
CI also covers version-tag pushes and a packaged four-ABI AAR.

Source builds now require Python 3 (`-PstvPython=/path/to/python3` overrides the
executable); consuming a published AAR does not execute this source-build guard.
The independent build exercises the same gate, but the legacy AGP 3.3/JCenter
project and a real JitPack publication are not migrated or published here.

The original report names 3.2.3. Today's served artifact is nonempty and its
Last-Modified is 2026-04-08: it cannot establish the reporter's historical cache.
Closing that original failure still needs the resolved dependency/AAR hash,
final APK or splits, device/API/ABI and the installed library size/hash, to find
the first mismatch along AAR → merged JNI → APK → installed file. No old tag or
published artifact is rewritten.

## #91 — preserve platform font inheritance, do not invent a font fix

SuperTextView delegates the supplied attributes to TextView and does not replace
Typeface. New XML fixtures compare TextView with SuperTextView inheriting
`monospace + bold|italic` through a style/textAppearance; normal, outlined,
gradient and combined drawing must preserve the typeface. The real-device suite
also compares actual framework font metrics.

Robolectric legacy Paint does not faithfully model reset/default style, alpha or
glyph metrics. Host tests therefore observe the original draw Paint's Typeface
and meaningful path properties; pixel/fallback checks belong to the device suite.
This is protection for a verified working configuration, not proof that the
reporter's unspecified font file/layout/system version has been reproduced.
System family names and pre-API-26 `@font` loading are different cases.

## #113 / #77 — reproducible resource/circle fixtures, missing original cause

Separate binary-XML fixtures load PNG, JPEG, vector, shape, selector and GIF
resources in both image slots; static images must produce pixels and GIFs must
return real JNI metadata. A rounded PNG background additionally checks its
opaque center and transparent corner. These exercise the production View and
resources, not a demo substitute or a mocked native implementation.

Both original reports contain only the outer InflateException; the innermost
cause and original layouts/images are still absent. Tests of current master do
not identify the original root cause, and passing API 35 does not substitute for
the reported API 31 environment. Do not call these original reports fixed merely
because a nearby native/signature issue was addressed by PR #125/#126.

## Reproduction commands

```sh
# JDK 11, Gradle 7.5 and Android platform 28; see regression-tests/README.md.
gradle -p regression-tests testDebugUnitTest assembleRelease assembleDebugAndroidTest
python3 -m unittest discover -s tools/tests -v
python3 tools/check_native_binaries.py --aar \
  regression-tests/build/outputs/aar/SuperTextView-regression-tests-release.aar

# The library's instrumentation APK is self-contained; select a test device explicitly.
adb -s <serial> install -r \
  regression-tests/build/outputs/apk/androidTest/debug/SuperTextView-regression-tests-debug-androidTest.apk
adb -s <serial> shell am instrument -w -r \
  com.coorchice.library.test/com.coorchice.library.DeviceRegressionInstrumentation
```

## Recorded results

- Original HEAD: 14 new host tests, **7 failures**, all the expected #89 no-op
  submissions; the font/control cases pass. Same expectations on the working
  tree: **57/57 total Java tests pass**, including all 14 new tests.
- Python tool tests: **26/26 pass**. Actual debug/release AARs pass four-ABI
  validation and contain byte-identical copies of the shipped libraries.
- The shared Gradle guard was also exercised in an isolated fixture: valid
  package exits 0; corrupt final AAR exits 1; empty committed SO exits 1 before
  the bundle task runs. No production SO was zeroed for this test.
- Official API 35 ARM64 ps16k virtual device: **15/15 framework/Skia tests pass**;
  application `sysconf=16384`, application `smaps KernelPageSize=16 kB`.
  Repeated drawing produces no historical `canvas.h`/`SuperTextView-onDraw`
  diagnostic; the device crash buffer is empty.

[Raw device log, device/APK identity and host test totals](test-results/remaining-issues-2026-10-22/).
These are native-kernel virtual-device results, not physical OEM/API-31 coverage
or a quantified GPU-performance measurement. The original root causes of #91,
#113, #77 and the declared 3.2.3 instance of #107 remain unconfirmed.
