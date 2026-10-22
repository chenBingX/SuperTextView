# GifLib native sources

The JNI wrapper and bundled giflib 5.1.4 sources originate from
[chenBingX/CoorChiceLibOne](https://github.com/chenBingX/CoorChiceLibOne/tree/9880e2973e17aa6b3ac2fe3380a574ccadc2787d/app/src/main/cpp)
(commit `9880e2973e17aa6b3ac2fe3380a574ccadc2787d`). The JNI class and all 19 method
signatures match `com.coorchice.library.gifdecoder.JNI`.

The decoder is now built here rather than keeping unexplained prebuilt binaries.
The original JNI API is preserved. The wrapper checks failed/truncated input,
initializes missing graphics-control extensions, releases failed-open resources,
and uses giflib's exact `InputFunc` signature. Reading a frame does not change the
current playback position. This is not a giflib upgrade or a general codec rewrite.

## Rebuild

Use Python 3, CMake 3.10+ and Android NDK r25 (for example `25.0.8775105`). Set
`ANDROID_NDK_HOME`, or pass the tool paths explicitly:

```sh
python3 tools/build_native.py --ndk /path/to/android-ndk-r25 --cmake /path/to/cmake
python3 tools/check_native_binaries.py
```

Run from the repository root. The build uses API 19 for armeabi-v7a/x86 and API 21
for arm64-v8a/x86_64. NDKs that no longer support API 19 are rejected for the 32-bit
build instead of silently raising the library's documented minimum SDK. The C++
runtime is linked statically; no additional `libc++_shared.so` must be packaged.
Old ARMv5 `armeabi`, removed from NDK r17, is deliberately not restored.

Both `max-page-size=16384` and `common-page-size=16384` are set. Merely observing a
64 KB LOAD alignment is insufficient: a 4 KB-aligned RELRO end can still protect
writable data on the same 16 KB page. The checker validates ELF architecture, LOAD
offset/address congruence and RELRO page sharing for every ABI.

The library's AAR does not decide final APK ZIP alignment. Applications packaging
uncompressed native libraries need AGP 8.5.1+ (and `zipalign -c -P 16 -v 4 app.apk`
from build-tools 35+ to verify the result), or the compressed-native-library
packaging alternative described in the
[Android 16 KB guide](https://developer.android.com/guide/practices/page-sizes).
Do not treat an ELF-only check as full device acceptance. See `native-tests/` for
an Android JNI/real bitmap smoke test; a 4 KB kernel is not a 16 KB test environment.

## Notices

The authored wrapper follows SuperTextView's Apache-2.0 license. giflib's MIT
license is retained in `COPYING.giflib`. `reallocarray.c` retains Otto Moerbeek's
ISC license notice. Upstream filenames and author notices are preserved.
