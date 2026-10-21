# Standalone Android regression tests

This directory is a separate Gradle root. It compiles the real `library/src/main/java`,
`library/src/main/res` and `library/src/main/AndroidManifest.xml`, and runs tests from
`library/src/test/java`. It does **not** evaluate or migrate the repository's legacy
AGP 3.3 / JCenter build files. The only additional main resource is an XML inflation
fixture local to this test build.

## Requirements

- JDK 11 (AGP 7.4.0's runtime requirement).
- Gradle **7.5**; the existing repository wrapper remains unchanged.
- An Android SDK with platform **28** and build-tools 30.0.3.
- Public Google Maven and Maven Central access on the first run. Robolectric 4.11.1
  also downloads its API 28 runtime from Maven Central. No emulator/device is needed.

Set `ANDROID_HOME` (or an **untracked** `regression-tests/local.properties`) for your
SDK installation. Never commit a machine-specific SDK path. `GRADLE_75` below is the
path to the Gradle 7.5 executable, such as one from an already cached distribution.

From the repository root:

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 11) # macOS; select JDK 11 normally elsewhere
export ANDROID_HOME=/path/to/your/android-sdk
export GRADLE_75=/path/to/gradle-7.5/bin/gradle

"$GRADLE_75" -p regression-tests testDebugUnitTest --console=plain
```

Run only the four issue regressions:

```sh
"$GRADLE_75" -p regression-tests testDebugUnitTest \
  --tests com.coorchice.library.SuperTextViewRegressionTest --console=plain
```

## Reproduce the unfixed baseline without changing shared source files

```sh
"$GRADLE_75" -p regression-tests -Pbaseline=true testDebugUnitTest \
  --tests com.coorchice.library.SuperTextViewRegressionTest --console=plain
```

`-Pbaseline=true` exports Java sources from **the current Git HEAD** with `git archive`
into ignored `build/baseline/baseline-sources`, then runs exactly the same tests.
It never checks out/reverts files or writes into `library/src/main`. This is useful
while fixes are uncommitted; after committing fixes, HEAD is no longer an unfixed
baseline. Normal reports are in `build/reports/tests/testDebugUnitTest/`; baseline
reports are separately retained in `build/baseline/reports/tests/testDebugUnitTest/`.

## What is observed

- **#122:** each drawable independently owns DOWN–UP, clicks exactly once, consumes
  UP, never clicks on CANCEL, and tolerates synchronous CANCEL from its own callback.
- **#114:** Java-constructor defaults match an actual inflated XML view, are non-null,
  initially hide both drawables, and can subsequently show/draw either drawable.
- **#104:** replacing drawable1's background or changing *either* dimension rebuilds
  its shader; leaving FIT_CENTER releases the intermediate canvas. Cache reflection
  is confined to these assertions. Drawable2 has no background mode: replacing it
  must draw new pixels without invalidating drawable1's unrelated background shader.
  Both slots cover null assignment, releasing old callbacks and self-assignment.
  A fake `Engine` captures callbacks to reproduce A→B→A out-of-order completion and
  a manual placeholder assignment superseding a pending image request; no network
  request or production request-token reflection is needed.
- **#120:** a recording `Canvas` observes the actual `TextPaint` passed by platform
  `BoringLayout` for plain single-line text during repeated real `View.draw()` calls.
  This avoids both incomplete legacy `ShadowPaint.set(Paint)` copying in the
  span/TextLine path and native-renderer API 28 incompatibilities. It checks one correctly
  colored stroke pass and one fill pass (including gradient fill), restores the
  original stateful `ColorStateList` and paint style/width/color/shader, and checks
  that drawing neither invalidates itself nor requests layout/mutates font padding.
  No private TextView reflection, private draw-method calls or production hook
  visibility changes are used; views are attached to an Activity and measured.

The standalone build also supports other tests in `library/src/test/java`, including
Mockito-based tests; GIF native binaries are not loaded by the regular-drawable tests.
