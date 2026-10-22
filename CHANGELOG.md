# Changelog

## v3.2.7 — 2026-10-22

Maven coordinate: `com.github.chenBingX:SuperTextView:v3.2.7`.
JitPack builds the production Maven artifact on demand from the release tag;
[check its build status](https://jitpack.io/#chenbingx/supertextview) if resolution
is waiting. A GitHub Release and local tests are distinct from external Maven
artifact availability.

### Java fixes — PR #125

- Initialize Java-constructed views with the same drawable defaults as XML,
  including both image slots.
- Correct drawable click ownership and cancellation: no click on `ACTION_CANCEL`,
  clear touch state before callbacks/re-entry and consume the owned `ACTION_UP`.
- Reject stale image-request callbacks, including same-URL A → B → A races;
  preserve manually assigned placeholders and refresh background shader state
  when the drawable or either view dimension changes.
- Draw outlined text without private TextView reflection; restore stateful text
  colors and Paint state without draw/layout invalidation loops.
- Gate GIF playback by owner visibility and attachment and preserve ownership
  across replacement/detachment.
- Detect static-image/GIF signatures without unnecessarily loading the native
  decoder; guard the demo's drawable-to-GIF cast and cached drawable2 routing.

Evidence: [PR #125](https://github.com/chenBingX/SuperTextView/pull/125),
[issue triage and original 43-test baseline](docs/issue-triage-2026-10-21.md), and
[regression contracts](regression-tests/README.md). The triage document is a
historical snapshot; its unmerged/native-not-rebuilt statements describe that
stage, not the subsequent native work.

### Native 16 KB compatibility and x86_64 — PR #126

- Restore the GIF decoder sources and reproducible native build, preserving the
  public JNI class and all 19 method signatures.
- Rebuild **armeabi-v7a, arm64-v8a, x86 and x86_64** with 16 KB LOAD alignment and
  offset/address congruence plus RELRO/writable-page separation. The previously
  missing x86_64 library is included; no extra `libc++_shared.so` is required.
- Correct the JNI byte-reader callback signature, validate decode completion and
  frame bounds, provide GIF87a no-GCB defaults, handle short signatures safely
  and preserve UTF-8 filenames including supplementary Unicode characters.
- Keep the Java library and 32-bit native minimum at **API 19**; 64-bit native
  libraries require **API 21**. The test APK's API 21 minimum is not a production
  minimum-SDK change. Native rebuilds use NDK r25 to retain API 19 support.

Evidence: [PR #126](https://github.com/chenBingX/SuperTextView/pull/126),
[native compatibility/build requirements](docs/native-compatibility.md),
[native source provenance and licenses](library/src/main/cpp/README.md), and
[native smoke suite](native-tests/README.md).

### Transparent drawing and AAR protection — PR #127

- Skip fully transparent border and solid-fill path submissions while preserving
  geometry, visible/translucent colors, gradients, pressed state and rounded
  image-background behavior. This is a confirmed no-op drawing fix, **not** a
  measured GPU-overdraw, frame-time, CPU or power improvement.
- Validate committed native binaries before source builds and the actual AAR
  after bundling. The gate checks the four-ABI set, duplicate entries, ZIP CRC,
  empty/truncated ELF payloads, architecture, LOAD bounds/alignment and RELRO,
  and reports sizes and SHA-256 hashes. A valid ZIP checksum alone cannot detect
  the historical empty native payload.
- Add regression/device fixtures for transparent drawing, inherited platform
  fonts and both image slots, without claiming reproduction of unspecified
  reporter environments.
- Record the exact historical `canvas.h` diagnostic and earlier removal; this is
  not presented as a newly discovered logging fix or a promise of silent logs.

Evidence: [PR #127](https://github.com/chenBingX/SuperTextView/pull/127),
[remaining-issue investigation](docs/remaining-issues-2026-10-22.md), and
[isolated Gradle gate results](docs/test-results/remaining-issues-2026-10-22/gradle-gate.json).

### Recorded verification and limits

| Evidence | Recorded result | Boundary |
| --- | --- | --- |
| Java host regression suite | **57/57 passed**; 14 new contracts include the confirmed transparent-drawing failures on the original baseline | Host command/typeface assertions are not device pixels or GPU measurements. |
| Python validation-tool suite | **35/35 passed**, including 9 new production-publication checks | Tool tests do not prove an external publication. |
| Actual debug/release regression AARs | Four native ABIs validated; byte-identical to shipped libraries | These test-build AARs are not the production publishing AAR. |
| Earlier API 35 ARM64 real-16-KB-kernel native suite | **47/47 in each of 10 runs = 470 executions**, empty crash buffer | Historical run at `d72aeef416673001c60dff55893c76431dd1d45a`; only the ARM64 library was loaded. |
| Current API 35 ARM64 framework/Skia UI suite | **15/15 passed**, empty crash buffer | Virtual device with app `sysconf=16384` and `smaps KernelPageSize=16 kB`; not physical OEM or API 31 coverage. |
| Initial x86_64 CI native matrix | 47 tests passed in each 4 KB / `ps16k` job | `ps16k` reports `sysconf=16384` on a **4 KB kernel**: simulated 16 KB userspace, not a real 16 KB kernel. |

Original evidence: [host/UI investigation and reproduction](docs/remaining-issues-2026-10-22.md),
[Java totals](docs/test-results/remaining-issues-2026-10-22/junit-summary.json),
[UI device identity/summary](docs/test-results/remaining-issues-2026-10-22/device-summary.json),
[raw UI run](docs/test-results/remaining-issues-2026-10-22/device-run.txt),
[ARM64 native device identity and raw log](docs/test-results/arm64-16k-2026-10-22/README.md),
[ten-run native totals](docs/test-results/arm64-16k-2026-10-22/summary.json), and
[the original x86_64 CI run](https://github.com/chenBingX/SuperTextView/actions/runs/39717058777).
These records retain their original revisions; they are not a fresh acceptance
run of the final v3.2.7 artifact. No new GPU/CPU performance figure is claimed.

Final production-AAR acceptance: [release run and raw evidence](docs/test-results/release-v3.2.7/README.md).
The real production AAR passed the publication gate and consumer build, then
**47/47** JNI tests on an ARM64 true-16-KB-kernel virtual device. This is separate
from the earlier regression AARs and the historical 470-run evidence.

### Unified coordinates and independent publishing build

Use the single v3.2.7 version for all four ABIs:

```groovy
implementation 'com.github.chenBingX:SuperTextView:v3.2.7'
```

The old `.64` / `.99` suffixes are **historical variants**, not ABI selectors for
v3.2.7. The independent `publishing/` build uses **JDK 11 or 17**, its **Gradle 7.5**
wrapper, an Android SDK and **Python 3**. It packages only the production library
sources/resources and shipped native binaries, with no regression-test code,
fixtures or test-only resources, and does not evaluate or migrate the old demo.
From the repository root:

```sh
./publishing/gradlew -p publishing publishToMavenLocal
```

This publishes to local Maven only; it does not publish to JitPack or a remote
repository. Python 3 is used by source-build native/AAR checks; **AAR consumers do
not need Python**. The v3.2.7 AAR embeds the JNI consumer keep rule for R8/ProGuard.
It also leaves the host application's label and backup policy untouched, avoiding
Manifest-merger conflicts. Older versions or custom shrinking pipelines that omit
consumer rules still need:

```proguard
-keep class com.coorchice.library.gifdecoder.JNI { *; }
```

ELF compatibility is not final APK ZIP alignment. Applications packaging
uncompressed native libraries should use AGP 8.5.1+ and verify their final APK;
older AGP needs a compatible extracted-library configuration. Other third-party
`.so` files must also be checked. See [application packaging guidance](docs/native-compatibility.md#application-packaging).

### Unresolved reports and non-goals

- **#91, #113, #77 and the originally reported 3.2.3 instance of #107 remain
  unconfirmed.** Passing inherited-font/resource/circle fixtures does not
  reproduce those missing original causes, layouts/assets or device environments.
- The empty 3.2.5 ARMv7 payload is a confirmed historical artifact defect; it does
  not establish the root cause of the reporter's 3.2.3 failure. No old tag or
  published artifact is rewritten.
- These suites are not full GIF conformance, fuzzing, long-running memory-leak or
  thread-safety validation. Copied GIF instances still require their source to
  outlive the copies, as required by the existing API.
- The legacy demo build is not migrated. Local gates and historical device/CI
  results do not certify a whole application's 16 KB compatibility. The Maven
  artifact is built separately by JitPack from the tag.
