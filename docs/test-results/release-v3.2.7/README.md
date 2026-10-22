# v3.2.7 production publication acceptance — 2026-10-22

This run uses the independent **production** `publishing/` AAR, not the regression
library's AAR. It was run before the release tag, on merged master `cf96da0813d7f9e67d4951d552611cd6ad981ae5`
plus the version/publishing/consumer-rule/manifest changes shipped with this entry.
Local build environment: Gradle 7.5, Corretto JDK 11, compile SDK 28 / build-tools 30.0.3.
The hosted publication job and JitPack use JDK 17 with the same Gradle version.

## Local acceptance

- Production `publishToMavenLocal`: passed. [`publication-local.json`](publication-local.json)
  records the actual AAR size/hash and each native library's size/hash.
- The publication checker verifies coordinates/POM, minSdk 19, the four native ABIs
  and their ELF/ZIP integrity, exact shipped native bytes, JNI consumer keep rules,
  Java classes/sources, giflib attribution and exclusion of regression fixtures.
- Production manifest does not set the host application's label or backup policy.
  The first consumer attempt exposed both conflicts; removing those library-level
  settings fixed the consumer build rather than masking them with `tools:replace`.
- The standalone native smoke APK was built with
  `-PstvAar=.../SuperTextView-v3.2.7.aar`, with source JNI and source `jniLibs` excluded.
- Installed that final-AAR consumer on API 35 ARM64 `google_apis_ps16k`:
  **47 passed, 0 failed**. Both app `sysconf=16384` and `smaps KernelPageSize=16 kB`
  are present in the [raw run](native-run.txt). This is a real 16 KB-kernel virtual
  device, not a physical OEM phone. Only ARM64 was loaded in this run.
- Fresh Java host regressions: **57 tests, 0 failures, 0 errors**.
- Fresh Python tool tests: **35 passed**, including 9 publication contracts;
  see [raw Python output](python-tests.txt).

The earlier [470 native executions](../arm64-16k-2026-10-22/README.md) and
[15 UI tests](../remaining-issues-2026-10-22/device-summary.json) remain separate
historical runs. This acceptance does not add physical-device, API 31, GPU/FPS,
full GIF conformance, fuzzing, leak or thread-safety coverage. It also does not
prove the availability of an externally hosted Maven artifact.

The GitHub Release workflow builds and checks its own production AAR, compiles a
consumer, then uploads the Maven files and `SHA256SUMS`. Its ZIP/container hash
may differ from the local record due to archive metadata/compiler differences;
the four shipped native byte hashes must remain identical.
