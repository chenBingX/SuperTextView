# SuperTextView issue triage — 2026-10-21

## Delivery

- Reviewed all **38 open issues** and posted **one individually tailored comment per issue** using GitHub **chenBingX**, whose verified primary email is **icechenx@gmail.com**.
- All 38 comments were re-read from GitHub and verified to have that author and non-empty bodies. All issues remain **OPEN**; no feature request or unverified report was closed.
- Fix PR: https://github.com/chenBingX/SuperTextView/pull/125
- Code/test commit: https://github.com/chenBingX/SuperTextView/commit/61703b8524c54a43d7c9bf848628df26e16de119
- Commit author and committer: `CoorChice <icechenx@gmail.com>`.
- Branch: `fix/open-issues-20261021` (not merged; no release/tag overwritten).

## Verification

The independent build compiles the real library sources/resources using JDK 11, Gradle 7.5, AGP 7.4.0 and Robolectric 4.11.1. It does not migrate the legacy production build.

| Revision | Tests | Failures | Errors | Skipped |
| --- | ---: | ---: | ---: | ---: |
| Original master `3b22d57b8bbf367dcdaa9e6ba587286f3d67d171` | 43 | 34 | 0 | 0 |
| Fixed `61703b8524c54a43d7c9bf848628df26e16de119` | 43 | 0 | 0 | 0 |

```sh
gradle --no-daemon -p regression-tests testDebugUnitTest --console=plain
```

See [regression-tests/README.md](../regression-tests/README.md) for environment setup. Counts: 28 view/image/text tests, 11 mocked GIF owner-lifecycle tests and 4 native-free GIF signature tests. The original baseline was exported before committing the fixes; `-Pbaseline=true` always exports the current HEAD, so after committing it no longer means the old unfixed revision.

**Limits:** no physical Android device was connected; native GIF CPU/power usage and target-29+ device rendering were not measured. The legacy demo app was not fully built or device-tested; its `instanceof` cast guard was source-reviewed. The exact reporter-specific #104 adapter/engine example was not provided. Native binaries were not rebuilt. Remote CI status is separate from the local test result; consult the PR checks.

## Per-issue decisions and posted replies

“Patch submitted” means a confirmed code-path defect has been addressed in the PR, not that a new release is already available or that every reporter-specific device scenario has been reproduced.

| Issue | Decision | Reply |
| --- | --- | --- |
| #124 — 16KB alignment | Needs actual artifact/APK/device evidence. Current arm64 ELF LOAD alignment is 64KB; no native source/build recipe in this repository. | [comment](https://github.com/chenBingX/SuperTextView/issues/124#issuecomment-6315464661) |
| #123 — image/text centered together | Enhancement; suggest compound drawable or composition, preserve existing mode semantics. | [comment](https://github.com/chenBingX/SuperTextView/issues/123#issuecomment-6315661405) |
| #122 — repeated dialog drawable click | Patch submitted: no CANCEL click, clear state before callback/re-entry, consume owned UP. | [comment](https://github.com/chenBingX/SuperTextView/issues/122#issuecomment-6316073488) |
| #120 — text stroke ineffective | Patch submitted: remove private TextView reflection, restore colors/Paint, prevent draw/layout loops. Device validation remains. | [comment](https://github.com/chenBingX/SuperTextView/issues/120#issuecomment-6316074959) |
| #117 — selected/enabled colors | Enhancement; explain static int vs ColorStateList and business/system-selector alternatives. | [comment](https://github.com/chenBingX/SuperTextView/issues/117#issuecomment-6315661989) |
| #116 — GIF background CPU | Patch submitted: visibility/attach lifecycle gating and ownership. Native CPU reduction not measured. | [comment](https://github.com/chenBingX/SuperTextView/issues/116#issuecomment-6316074522) |
| #115 — pressed images | Enhancement; suggest StateListDrawable/system background rather than duplicate state API. | [comment](https://github.com/chenBingX/SuperTextView/issues/115#issuecomment-6315662476) |
| #114 — dynamically created drawable invisible | Patch submitted: Java-constructor defaults match XML for both slots. | [comment](https://github.com/chenBingX/SuperTextView/issues/114#issuecomment-6316073995) |
| #113 — local image InflateException | Need full inner Caused by, XML/resource, dependency version and ABI. Outer exception alone is insufficient. | [comment](https://github.com/chenBingX/SuperTextView/issues/113#issuecomment-6315424994) |
| #112 — partial text styles in XML | Enhancement; recommend system styled resources/Spannable instead of fixed character-index API. | [comment](https://github.com/chenBingX/SuperTextView/issues/112#issuecomment-6315662736) |
| #111 — local paths | Default engine is HTTP-only; explain Bitmap/Drawable, ContentResolver and custom engine alternatives, including separate GIF routing. | [comment](https://github.com/chenBingX/SuperTextView/issues/111#issuecomment-6315426210) |
| #110 — legacy armeabi | ABI compatibility work requires native rebuild; do not copy/rename incompatible so files. | [comment](https://github.com/chenBingX/SuperTextView/issues/110#issuecomment-6315464357) |
| #109 — shadow | Enhancement; distinguish text shadow from card/container elevation. | [comment](https://github.com/chenBingX/SuperTextView/issues/109#issuecomment-6315663000) |
| #108 — x86_64 | Missing native ABI confirmed; cannot produce a valid binary without decoder source/build. | [comment](https://github.com/chenBingX/SuperTextView/issues/108#issuecomment-6315463990) |
| #107 — zero-size so on 3.2.3 | Need resolved artifact and AAR/APK evidence; do not conflate with proven v3.2.5 empty binary. | [comment](https://github.com/chenBingX/SuperTextView/issues/107#issuecomment-6315463702) |
| #106 — pressed color | Usage: identical RGB compositing is visually unchanged; suggest translucent black or a distinct opaque color. | [comment](https://github.com/chenBingX/SuperTextView/issues/106#issuecomment-6315160904) |
| #105 — non-background image corners | Enhancement; explain background path vs direct drawable rendering, suggest image transformation/component. | [comment](https://github.com/chenBingX/SuperTextView/issues/105#issuecomment-6315663533) |
| #104 — list image overwrites | Patch submitted for confirmed same-URL request races, placeholder ownership and shader-size invalidation; exact adapter scenario still needs sample. | [comment](https://github.com/chenBingX/SuperTextView/issues/104#issuecomment-6316075381) |
| #101 — tint selector | Enhancement; explain fixed tint int and state-aware color resolution in business/subclass. | [comment](https://github.com/chenBingX/SuperTextView/issues/101#issuecomment-6315700412) |
| #99 — two text slots | Enhancement; recommend two composed text views instead of expanding one text layout into a container. | [comment](https://github.com/chenBingX/SuperTextView/issues/99#issuecomment-6315700741) |
| #98 — empty v3.2.5 ARM so | Historical binary defect confirmed at that tag; current master has non-empty ELF files. No historical tag/artifact rewritten. | [comment](https://github.com/chenBingX/SuperTextView/issues/98#issuecomment-6315463425) |
| #97 — selected/checked/enabled backgrounds | Enhancement; distinguish system selector, custom solid and absent checked model. | [comment](https://github.com/chenBingX/SuperTextView/issues/97#issuecomment-6315662213) |
| #96 — marquee without focus | Enhancement; suggest system single-line/marquee/selected combination, note semantic/lifecycle limits. | [comment](https://github.com/chenBingX/SuperTextView/issues/96#issuecomment-6315701032) |
| #95 — independent corner radii | Enhancement; explain one radius plus enable flags, suggest GradientDrawable/container. | [comment](https://github.com/chenBingX/SuperTextView/issues/95#issuecomment-6315701285) |
| #94 — image completion / GIF once | Enhancement; separate request lifecycle from reliable playback completion; frame callbacks are not a once-only guarantee. | [comment](https://github.com/chenBingX/SuperTextView/issues/94#issuecomment-6315701593) |
| #93 — excess logs | DEBUG defaults false; request copyable log/tag/version before suppressing diagnostics. | [comment](https://github.com/chenBingX/SuperTextView/issues/93#issuecomment-6315425900) |
| #92 — demo drawable click crash | Patch submitted: guard VectorDrawable-to-GifDrawable cast; fix drawable2 cached GIF fall-through and avoid native detection for static images. | [comment](https://github.com/chenBingX/SuperTextView/issues/92#issuecomment-6316075754) |
| #91 — fontFamily | Need minimal font/layout/system reproduction; current library does not override Typeface. | [comment](https://github.com/chenBingX/SuperTextView/issues/91#issuecomment-6315425610) |
| #89 — overdraw | Need equivalent layouts/screenshots/profiling; provided border colors differ, debug color alone is not a performance diagnosis. | [comment](https://github.com/chenBingX/SuperTextView/issues/89#issuecomment-6315426524) |
| #88 — gradient border | Enhancement; distinguish existing fill/text gradients and suggest Drawable/Adjuster extension. | [comment](https://github.com/chenBingX/SuperTextView/issues/88#issuecomment-6315701879) |
| #87 — AndroidX | Library has no Support dependency; distinguish library compatibility from unmigrated demo build. | [comment](https://github.com/chenBingX/SuperTextView/issues/87#issuecomment-6315349088) |
| #86 — CardView-like shadow | Enhancement; suggest outer card container, no implicit schedule from old comments. | [comment](https://github.com/chenBingX/SuperTextView/issues/86#issuecomment-6315663256) |
| #84 — engine lifecycle | Enhancement; host/cancellation protocol needs design, not just a global Activity/Context capture. | [comment](https://github.com/chenBingX/SuperTextView/issues/84#issuecomment-6315702127) |
| #77 — circle InflateException | Need full inner cause, XML/resource and version; do not guess corner algorithm from wrapper exception. | [comment](https://github.com/chenBingX/SuperTextView/issues/77#issuecomment-6315425308) |
| #61 — image/text whole centered | Enhancement; same family as #123; suggest composition/system compound drawable. | [comment](https://github.com/chenBingX/SuperTextView/issues/61#issuecomment-6315661689) |
| #39 — rounded stroke joins | Suggest existing TextPaint Join/Cap; no claim that every font alignment issue is resolved. | [comment](https://github.com/chenBingX/SuperTextView/issues/39#issuecomment-6315702403) |
| #25 — press fade durations | Enhancement; suggest system StateListDrawable transitions; avoid another incomplete state machine. | [comment](https://github.com/chenBingX/SuperTextView/issues/25#issuecomment-6315702665) |
| #8 — right/bottom drawable padding | Explain existing two-axis offset semantics/negative offsets and backward/RTL compatibility constraints. | [comment](https://github.com/chenBingX/SuperTextView/issues/8#issuecomment-6315702901) |

## Native artifact evidence

| Master ABI | Size | ELF PT_LOAD p_align |
| --- | ---: | --- |
| armeabi-v7a | 124792 bytes | 0x1000, 0x1000 |
| arm64-v8a | 256568 bytes | 0x10000, 0x10000 |
| x86 | 239532 bytes | 0x1000, 0x1000 |

The old `origin/v3.2.5` armeabi-v7a blob is exactly 0 bytes. Current master has no x86_64 or legacy armeabi binary and no C/C++/CMake/Android.mk decoder source. ELF alignment alone is not proof of final APK ZIP alignment or compatibility on every device; #124 requests the actual failing artifact and logs.
