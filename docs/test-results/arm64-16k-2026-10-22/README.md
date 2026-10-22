# ARM64 real 16 KB-kernel device verification — 2026-10-22

## Result

**47/47 native smoke tests passed in each of 10 consecutive runs: 470 passed
executions, no failures. The device crash buffer was empty after the runs.**
The application process itself reported both `sysconf=16384` and
`smaps KernelPageSize=16 kB` on every run. This is an Android virtual device
with a real 16 KB ARM64 kernel, not the x86_64 userspace page-size simulation.

| Item | Observed value |
| --- | --- |
| Tested checkout | `d72aeef416673001c60dff55893c76431dd1d45a` |
| Time | 2026-10-22 13:15:02–13:15:09 +08:00 |
| Android / SDK | Android 15 / API 35 |
| Guest ABI / process architecture | `arm64-v8a` / `aarch64` |
| Emulator | 36.6.11.0, isolated AVD `stv-arm64-real16k-20261022` |
| Serial | `emulator-5582` |
| Kernel | `6.6.30-android15-8-gdd9c02ccfe27-ab11987101` |
| App `Os.sysconf(_SC_PAGESIZE)` | 16384 bytes |
| App `/proc/self/smaps` `KernelPageSize` | 16 kB |
| Shell `getconf PAGESIZE` / `smaps` | 16384 bytes / 16 kB |

Official image: `system-images;android-35;google_apis_ps16k;arm64-v8a`, revision 5.
The complete 1,778,788,352-byte archive was verified against the official SHA-1
`9d834fe640beff22d25025a8028b98ab4366be4d` before installation:
https://dl.google.com/android/repository/sys-img/google_apis/arm64-v8a-ps16k-35_r05.zip

Image fingerprint:

```text
google/sdk_gphone16k_arm64/emu64a16k:15/AE3A.240806.043/12960925:userdebug/dev-keys
```

The kernel's ARM64 Image header flags were `0x0c`, page-size field `2` (16 KB).
The emulator booted cold with `-no-snapshot`; no 4 KB snapshot, replacement
kernel, mocked JNI implementation or altered userspace page-size query was used.

## Exact tested artifacts

The APK contained byte-identical copies of all four shipped `libGifLib.so`
files; **only the ARM64 library was loaded on this device**. The hashes and full
device identity are retained in [device.json](device.json).

- APK SHA-256: `ba910af8c7f04709ddd24d9c73a687449146c9f5ff947b336e5cdd783defcb5e`
- ARM64 library SHA-256: `59f77f68ac6f0dbc8fe8825e35ad6f1dc4b30a8190eb6b6bb7bbd4d4903dd48d`

## Evidence

- [First complete raw instrumentation run](run-01.txt): 47 passed, 0 failed,
  total 47, `INSTRUMENTATION_CODE: -1`; includes the app's page-size values and
  successful `JNI_OnLoad` registration of all 19 native methods.
- [All ten run totals](summary.json): 47 passed in every run, 470 executions.
- [SHA-256 of each original run log](run-sha256.json).
- [Crash buffer after the ten runs](crash-buffer.txt): empty.

All ten original logs are also retained locally at
`native-build/arm64-16k-device/run-01.txt` through `run-10.txt`; the repository
keeps one complete raw run instead of ten copies of the repetitive protocol.

The suite checks actual JNI loading and calls, exact frame pixels,
transparency, palettes/frame offsets, frame control, independent copy playback,
GIF87a defaults, malformed/empty inputs, zero-handle destruction and Unicode filenames.

## Reproduce on a selected ARM64 16 KB device

Prepare the official ARM64 `ps16k` image above and boot without snapshots.
Confirm the correct serial and image before installing anything, then run:

```sh
python3 tools/run_native_smoke.py \
  --adb "$ANDROID_HOME/platform-tools/adb" --serial emulator-5582 \
  --page-size 16384 --kernel-page-size 16384 \
  --log native-build/arm64-16k-smoke.txt
```

`--page-size` requires the application's `sysconf` value; `--kernel-page-size`
additionally requires the application's readable `smaps` value to agree. Missing
kernel evidence, a 4 KB kernel, incomplete execution, any test failure or an
incorrect instrumentation completion code is rejected.

## Scope

This is virtual-device testing, not physical OEM-device coverage. Repeating the
suite does not establish GIF conformance, long-running memory-leak freedom,
thread safety or full application UI/lifecycle correctness. The test APK extracts
compressed native libraries and does not prove uncompressed APK ZIP alignment.
The x86_64 CI simulation remains a separate result. No PR merge or release was
performed as part of this verification.
