package com.coorchice.nativetests;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;

import com.coorchice.library.gifdecoder.JNI;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Platform-only, self-targeted instrumentation. No external test framework. */
public final class NativeSmokeInstrumentation extends Instrumentation {
    private static final String TAG = "GifNativeSmoke";
    private static final String CLASS = NativeSmokeInstrumentation.class.getName();
    private static final int RED = 0xffff0000;
    private static final int GREEN = 0xff00ff00;
    private static final int BLUE = 0xff0000ff;
    private static final int BLACK = 0xff000000;
    // Hand-authored image oracle, not values read back from the decoder under test.
    private static final int[] FIRST = {RED, 0, BLUE, GREEN, RED, 0};
    private static final int[] SECOND = {RED, GREEN, BLUE, GREEN, RED, BLUE};
    private static final int[] PLAIN = {RED, BLACK, BLUE, GREEN, RED, BLACK};
    private static final String[] NATIVE_METHODS = {
            "openFile", "openBytes", "fileIsGif", "bytesIsGif", "updateFrame",
            "getWidth", "getHeight", "getFrameCount", "getFrameDuration",
            "setFrameDuration", "getCurrentFrame", "gotoFrame", "getFrame",
            "setFrame", "setStrict", "getStrict", "destroy", "copy", "copyDestroy"
    };

    private Bundle arguments;
    private byte[] animated;
    private byte[] plain;
    private int passed;
    private int failed;
    private String environment = "not queried";

    private interface Check {
        void run() throws Exception;
    }

    private interface FileCheck {
        void run(File file) throws Exception;
    }

    private static final class TestCase {
        final String name;
        final Check check;

        TestCase(String name, Check check) {
            this.name = name;
            this.check = check;
        }
    }

    private static final class Input {
        final String name;
        final byte[] bytes;

        Input(String name, byte[] bytes) {
            this.name = name;
            this.bytes = bytes;
        }
    }

    @Override
    public void onCreate(Bundle args) {
        super.onCreate(args);
        arguments = args == null ? new Bundle() : new Bundle(args);
        start();
    }

    @Override
    public void onStart() {
        super.onStart();
        List<TestCase> tests = new ArrayList<>();
        try {
            animated = readAsset("known-pixels.gif");
            plain = readAsset("plain-87a.gif");
            addTests(tests);
            String only = arguments.getString("test");
            if (only != null) {
                List<TestCase> selected = new ArrayList<>();
                for (TestCase test : tests) {
                    if (test.name.equals(only)) {
                        selected.add(test);
                    }
                }
                if (selected.isEmpty()) {
                    throw new IllegalArgumentException("Unknown test: " + only + "; available="
                            + names(tests));
                }
                tests = selected;
            }
        } catch (Throwable failure) {
            failed++;
            report("setup", 1, 1, -2, failure);
            finishRun(1);
            return;
        }

        int current = 0;
        for (TestCase test : tests) {
            current++;
            // Visible before each native call: a SIGSEGV/SIGFPE cannot be caught by Java.
            report(test.name, current, tests.size(), 1, null);
            try {
                test.check.run();
                passed++;
                report(test.name, current, tests.size(), 0, null);
            } catch (Throwable failure) {
                failed++;
                report(test.name, current, tests.size(), -2, failure);
            }
        }
        finishRun(tests.size());
    }

    private void addTests(List<TestCase> tests) {
        add(tests, "environment", this::checkEnvironment);
        add(tests, "jniRegistration", this::checkRegistration);
        add(tests, "positiveSignatures", () -> {
            truth(JNI.bytesIsGif(animated), "GIF89a byte signature");
            truth(JNI.bytesIsGif(plain), "GIF87a byte signature");
            withFile(animated, f -> truth(JNI.fileIsGif(f.getAbsolutePath()), "GIF89a file signature"));
            withFile(plain, f -> truth(JNI.fileIsGif(f.getAbsolutePath()), "GIF87a file signature"));
        });
        add(tests, "openBytesMetadata", () -> checkMetadata(false));
        add(tests, "openFileMetadata", () -> checkMetadata(true));
        add(tests, "unicodeFilePath", () -> withFile(animated, "图像-😀-", file -> {
            truth(JNI.fileIsGif(file.getAbsolutePath()), "Chinese + emoji file signature");
            long ptr = opened(JNI.openFile(file.getAbsolutePath()));
            try {
                metadata(ptr, 2, 150);
                renderTwo(ptr);
            } finally {
                JNI.destroy(ptr);
            }
        }));
        add(tests, "sequentialPixels", this::checkSequentialPixels);
        add(tests, "strictFrameControls", this::checkStrictFrameControls);
        add(tests, "nonStrictFrameControls", this::checkNonStrictFrameControls);
        add(tests, "copyOwnership", this::checkCopyOwnership);
        add(tests, "gif87BytesNoGcb", () -> checkPlain(false));
        add(tests, "gif87FileNoGcb", () -> checkPlain(true));
        add(tests, "zeroHandleDestroy", () -> {
            JNI.destroy(0);
            JNI.copyDestroy(0);
        });

        List<Input> signatures = new ArrayList<>();
        signatures.add(new Input("Null", null));
        signatures.add(new Input("Empty", new byte[0]));
        for (int length = 1; length < 6; length++) {
            signatures.add(new Input("Short" + length, Arrays.copyOf(animated, length)));
        }
        signatures.add(new Input("NonGif", "NOTGIF data".getBytes(StandardCharsets.US_ASCII)));
        for (Input input : signatures) {
            add(tests, "signatureBytes" + input.name,
                    () -> truth(!JNI.bytesIsGif(input.bytes), input.name + " bytes must not be GIF"));
            add(tests, "signatureFile" + input.name, () -> {
                if (input.bytes == null) {
                    truth(!JNI.fileIsGif(null), "null file must not be GIF");
                } else {
                    withFile(input.bytes, f -> truth(!JNI.fileIsGif(f.getAbsolutePath()),
                            input.name + " file must not be GIF"));
                }
            });
        }
        add(tests, "signatureFileMissing", () -> {
            File missing = missingFile();
            truth(!JNI.fileIsGif(missing.getAbsolutePath()), "missing file must not be GIF");
        });

        List<Input> invalid = Arrays.asList(
                new Input("Null", null),
                new Input("Empty", new byte[0]),
                new Input("Short", Arrays.copyOf(animated, 5)),
                new Input("NonGif", "NOTGIF data".getBytes(StandardCharsets.US_ASCII)),
                new Input("TruncatedHeader", Arrays.copyOf(animated, 6)),
                new Input("TruncatedScreen", Arrays.copyOf(animated, 25)),
                new Input("TruncatedFrame", Arrays.copyOf(animated, animated.length - 4)),
                new Input("MissingTrailer", Arrays.copyOf(animated, animated.length - 1)));
        for (Input input : invalid) {
            add(tests, "openBytes" + input.name,
                    () -> expectRejected(JNI.openBytes(input.bytes), input.name + " bytes"));
            add(tests, "openFile" + input.name, () -> {
                if (input.bytes == null) {
                    expectRejected(JNI.openFile(null), "null file");
                } else {
                    withFile(input.bytes, f -> expectRejected(JNI.openFile(f.getAbsolutePath()),
                            input.name + " file"));
                }
            });
        }
        add(tests, "openFileMissing",
                () -> expectRejected(JNI.openFile(missingFile().getAbsolutePath()), "missing file"));
    }

    private void checkEnvironment() throws Exception {
        long pageSize = Os.sysconf(OsConstants._SC_PAGESIZE);
        truth(pageSize > 0, "positive sysconf page size");
        String smapsPage = "unavailable";
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/self/smaps"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("KernelPageSize:")) {
                    smapsPage = line.trim();
                    break;
                }
            }
        } catch (IOException unavailable) {
            // sysconf is authoritative; smaps is additional, potentially restricted evidence.
            smapsPage = "unavailable: " + unavailable.getMessage();
        }
        environment = "sdk=" + Build.VERSION.SDK_INT + " supportedAbis="
                + Arrays.toString(Build.SUPPORTED_ABIS) + " processArch="
                + System.getProperty("os.arch") + " pageSizeBytes=" + pageSize
                + " smaps=" + smapsPage + " nativeLibraryDir="
                + getTargetContext().getApplicationInfo().nativeLibraryDir;
        info(environment);
        String required = arguments.getString("requirePageSize");
        if (required != null) {
            equal(Long.parseLong(required), pageSize, "required runtime page size");
        }
    }

    private void checkRegistration() throws Exception {
        // Initialize the REAL JNI class; System.loadLibrary triggers JNI_OnLoad/RegisterNatives.
        Class<?> jniClass = Class.forName("com.coorchice.library.gifdecoder.JNI", true,
                getTargetContext().getClassLoader());
        Set<String> actual = new HashSet<>();
        int count = 0;
        for (Method method : jniClass.getDeclaredMethods()) {
            if (Modifier.isNative(method.getModifiers())) {
                actual.add(method.getName());
                count++;
            }
        }
        equal(19, count, "native declaration count");
        truth(actual.equals(new HashSet<>(Arrays.asList(NATIVE_METHODS))),
                "native declarations differ: " + actual);
        // Successful full-suite execution resolves and calls every one of these 19 methods.
        info("JNI_OnLoad loaded; 19 native declarations=" + actual);
    }

    private void checkMetadata(boolean fromFile) throws Exception {
        if (fromFile) {
            withFile(animated, file -> {
                long ptr = opened(JNI.openFile(file.getAbsolutePath()));
                try {
                    metadata(ptr, 2, 150);
                    renderTwo(ptr);
                } finally {
                    JNI.destroy(ptr);
                }
            });
        } else {
            long ptr = opened(JNI.openBytes(animated));
            try {
                metadata(ptr, 2, 150);
            } finally {
                JNI.destroy(ptr);
            }
        }
    }

    private void checkSequentialPixels() {
        long ptr = opened(JNI.openBytes(animated));
        Bitmap bitmap = canvas();
        try {
            metadata(ptr, 2, 150);
            truth(!JNI.getStrict(ptr), "default strict=false");
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, FIRST, "first frame: transparent pixels stay transparent");
            equal(0, JNI.getCurrentFrame(ptr), "first displayed frame");
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, SECOND, "second: partial rectangle, transparency and local palette");
            equal(1, JNI.getCurrentFrame(ptr), "second displayed frame");
            JNI.setFrameDuration(ptr, 777);
            equal(777, JNI.getFrameDuration(ptr), "duration override");
            bitmap.eraseColor(0);
            delay(JNI.updateFrame(ptr, bitmap), 777);
            pixels(bitmap, FIRST, "playback wraps to first (on a cleared canvas)");
            equal(0, JNI.getCurrentFrame(ptr), "wrapped frame");
        } finally {
            bitmap.recycle();
            JNI.destroy(ptr);
        }
    }

    private void checkStrictFrameControls() {
        long ptr = opened(JNI.openBytes(animated));
        Bitmap bitmap = canvas();
        try {
            JNI.setStrict(ptr, true);
            truth(JNI.getStrict(ptr), "strict enabled");
            JNI.gotoFrame(ptr, 1, bitmap);
            pixels(bitmap, SECOND, "strict goto reconstructs preceding full frame");
            equal(1, JNI.getCurrentFrame(ptr), "goto current frame");
            bitmap.eraseColor(0);
            JNI.gotoFrame(ptr, -9, bitmap);
            pixels(bitmap, FIRST, "negative goto clamps to zero");
            equal(0, JNI.getCurrentFrame(ptr), "negative goto current frame");
            bitmap.eraseColor(0);
            JNI.gotoFrame(ptr, 99, bitmap);
            pixels(bitmap, SECOND, "upper goto clamps to last");
            equal(1, JNI.getCurrentFrame(ptr), "upper goto current frame");
            JNI.setFrame(ptr, -1);
            equal(0, JNI.getCurrentFrame(ptr), "negative setFrame clamps to zero");
            JNI.setFrame(ptr, 99);
            equal(1, JNI.getCurrentFrame(ptr), "upper setFrame clamps to last");
            JNI.setFrame(ptr, 0);
            bitmap.eraseColor(0);
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, FIRST, "setFrame changes next render");
            bitmap.eraseColor(0);
            JNI.getFrame(ptr, 1, bitmap);
            pixels(bitmap, SECOND, "strict getFrame composites on fresh canvas");
            equal(0, JNI.getCurrentFrame(ptr), "strict getFrame preserves displayed position");
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, SECOND, "strict getFrame leaves pending frame 1 unchanged");
            equal(1, JNI.getCurrentFrame(ptr), "strict getFrame preserves next playback position");
            bitmap.eraseColor(0);
            JNI.getFrame(ptr, -1, bitmap);
            pixels(bitmap, FIRST, "negative getFrame clamps to zero");
            bitmap.eraseColor(0);
            JNI.getFrame(ptr, 99, bitmap);
            pixels(bitmap, SECOND, "upper getFrame clamps to last");
            JNI.setStrict(ptr, false);
            truth(!JNI.getStrict(ptr), "strict disabled");
        } finally {
            bitmap.recycle();
            JNI.destroy(ptr);
        }
    }

    private void checkNonStrictFrameControls() {
        long ptr = opened(JNI.openBytes(animated));
        Bitmap bitmap = canvas();
        try {
            JNI.setStrict(ptr, false);
            JNI.gotoFrame(ptr, 0, bitmap);
            pixels(bitmap, FIRST, "non-strict goto first");
            JNI.gotoFrame(ptr, 1, bitmap);
            pixels(bitmap, SECOND, "non-strict goto partial frame on predecessor");
            equal(1, JNI.getCurrentFrame(ptr), "non-strict goto current");
            bitmap.eraseColor(0);
            JNI.getFrame(ptr, 0, bitmap);
            pixels(bitmap, FIRST, "non-strict get first");
            equal(1, JNI.getCurrentFrame(ptr), "non-strict getFrame preserves displayed position");
            JNI.getFrame(ptr, 1, bitmap);
            pixels(bitmap, SECOND, "non-strict get partial frame on predecessor");
            equal(1, JNI.getCurrentFrame(ptr), "second non-strict getFrame preserves position");
            bitmap.eraseColor(0);
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, FIRST, "non-strict getFrame preserves pending frame zero");
            equal(0, JNI.getCurrentFrame(ptr), "non-strict getFrame preserves next position");
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, SECOND, "non-strict playback continues after getFrame");
            JNI.setFrame(ptr, 0);
            bitmap.eraseColor(0);
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, FIRST, "non-strict set first");
            JNI.setFrame(ptr, 1);
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, SECOND, "non-strict set second");
        } finally {
            bitmap.recycle();
            JNI.destroy(ptr);
        }
    }

    private void checkCopyOwnership() {
        long original = opened(JNI.openBytes(animated));
        long copy = 0;
        Bitmap originalBitmap = canvas();
        Bitmap copyBitmap = canvas();
        try {
            JNI.setStrict(original, true);
            delay(JNI.updateFrame(original, originalBitmap), 150);
            pixels(originalBitmap, FIRST, "original first frame");
            copy = opened(JNI.copy(original));
            truth(copy != original, "copy must be a distinct instance");
            metadata(copy, 2, 150);
            truth(!JNI.getStrict(copy), "copy starts in non-strict mode");
            JNI.setFrameDuration(copy, 90);
            equal(90, JNI.getFrameDuration(copy), "copy duration override");
            equal(150, JNI.getFrameDuration(original), "copy does not change original duration");
            JNI.setStrict(copy, true);
            JNI.setStrict(original, false);
            truth(JNI.getStrict(copy), "original strict change leaves copy strict");
            truth(!JNI.getStrict(original), "original owns its strict flag");
            delay(JNI.updateFrame(copy, copyBitmap), 90);
            pixels(copyBitmap, FIRST, "copy restarts at frame zero");
            delay(JNI.updateFrame(copy, copyBitmap), 90);
            pixels(copyBitmap, SECOND, "copy independently advances");
            equal(0, JNI.getCurrentFrame(original), "copy playback does not advance original");
            delay(JNI.updateFrame(original, originalBitmap), 150);
            pixels(originalBitmap, SECOND, "original keeps its own pending frame");
            JNI.setFrame(copy, 0);
            equal(1, JNI.getCurrentFrame(original), "copy seeking does not seek original");
            JNI.copyDestroy(copy);
            copy = 0;
            // copyDestroy must not free the original's shared decoded data.
            originalBitmap.eraseColor(0);
            JNI.setStrict(original, true);
            JNI.gotoFrame(original, 1, originalBitmap);
            pixels(originalBitmap, SECOND, "original remains renderable after copyDestroy");
        } finally {
            if (copy != 0) {
                JNI.copyDestroy(copy);
            }
            JNI.destroy(original);
            originalBitmap.recycle();
            copyBitmap.recycle();
        }
    }

    private void checkPlain(boolean fromFile) throws Exception {
        if (fromFile) {
            withFile(plain, file -> renderPlain(opened(JNI.openFile(file.getAbsolutePath()))));
        } else {
            renderPlain(opened(JNI.openBytes(plain)));
        }
    }

    private void renderPlain(long ptr) {
        Bitmap bitmap = canvas();
        try {
            metadata(ptr, 1, 0);
            equal(-1, JNI.updateFrame(ptr, bitmap), "no GCB means zero duration / no timer");
            pixels(bitmap, PLAIN, "GIF87a without GCB uses opaque colors, including black");
            equal(0, JNI.getCurrentFrame(ptr), "single frame index");
        } finally {
            bitmap.recycle();
            JNI.destroy(ptr);
        }
    }

    private static void metadata(long ptr, int frames, int duration) {
        equal(3, JNI.getWidth(ptr), "width");
        equal(2, JNI.getHeight(ptr), "height");
        equal(frames, JNI.getFrameCount(ptr), "frame count");
        // JNI's public contract exposes the average duration, not per-frame GCE delays.
        equal(duration, JNI.getFrameDuration(ptr), "frame duration ms");
        equal(0, JNI.getCurrentFrame(ptr), "initial displayed frame");
    }

    private static void renderTwo(long ptr) {
        Bitmap bitmap = canvas();
        try {
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, FIRST, "file first frame");
            equal(0, JNI.getCurrentFrame(ptr), "file first index");
            delay(JNI.updateFrame(ptr, bitmap), 150);
            pixels(bitmap, SECOND, "file second frame");
            equal(1, JNI.getCurrentFrame(ptr), "file second index");
        } finally {
            bitmap.recycle();
        }
    }

    private static void expectRejected(long ptr, String input) {
        if (ptr != 0) {
            // Clean up an incorrectly accepted input, but never call this a pass.
            try {
                throw new AssertionError(input + " must return 0; got native handle " + ptr);
            } finally {
                JNI.destroy(ptr);
            }
        }
    }

    private static long opened(long ptr) {
        truth(ptr != 0, "valid GIF must open with a nonzero handle");
        return ptr;
    }

    private static Bitmap canvas() {
        Bitmap bitmap = Bitmap.createBitmap(3, 2, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(0);
        return bitmap;
    }

    private static void pixels(Bitmap bitmap, int[] expected, String message) {
        int[] actual = new int[6];
        bitmap.getPixels(actual, 0, 3, 0, 0, 3, 2);
        for (int i = 0; i < expected.length; i++) {
            if (actual[i] != expected[i]) {
                throw new AssertionError(message + " at (" + i % 3 + "," + i / 3
                        + "): expected=0x" + Integer.toHexString(expected[i])
                        + " actual=0x" + Integer.toHexString(actual[i]));
            }
        }
    }

    private static void delay(int actual, int maximum) {
        // Native subtracts rendering time. Slow emulators may legitimately return zero.
        truth(actual >= 0 && actual <= maximum,
                "render delay must be in [0," + maximum + "], got " + actual);
    }

    private byte[] readAsset(String name) throws IOException {
        try (InputStream in = getTargetContext().getAssets().open(name);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            return out.toByteArray();
        }
    }

    private void withFile(byte[] bytes, FileCheck check) throws Exception {
        withFile(bytes, "native-gif-", check);
    }

    private void withFile(byte[] bytes, String prefix, FileCheck check) throws Exception {
        File file = File.createTempFile(prefix, ".gif", getTargetContext().getCacheDir());
        try {
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(bytes);
            }
            check.run(file);
        } finally {
            if (!file.delete()) {
                Log.w(TAG, "Could not delete temporary fixture " + file);
            }
        }
    }

    private File missingFile() throws IOException {
        File file = File.createTempFile("native-missing-", ".gif", getTargetContext().getCacheDir());
        truth(file.delete(), "prepare missing path");
        return file;
    }

    private static void add(List<TestCase> tests, String name, Check check) {
        tests.add(new TestCase(name, check));
    }

    private static String names(List<TestCase> tests) {
        List<String> names = new ArrayList<>();
        for (TestCase test : tests) {
            names.add(test.name);
        }
        return names.toString();
    }

    private static void truth(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void equal(long expected, long actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected=" + expected + " actual=" + actual);
        }
    }

    private void info(String message) {
        Log.i(TAG, message);
        Bundle status = new Bundle();
        status.putString("stream", "\n" + message + "\n");
        sendStatus(2, status);
    }

    private void report(String name, int current, int total, int code, Throwable failure) {
        Bundle status = new Bundle();
        status.putString("class", CLASS);
        status.putString("test", name);
        status.putInt("current", current);
        status.putInt("numtests", total);
        String outcome = code == 1 ? "START" : code == 0 ? "PASS" : "FAIL";
        String message = outcome + " " + name + " (" + current + "/" + total + ")";
        if (failure != null) {
            String stack = Log.getStackTraceString(failure);
            status.putString("stack", stack);
            message += "\n" + stack;
            Log.e(TAG, message);
        } else {
            Log.i(TAG, message);
        }
        status.putString("stream", "\n" + message + "\n");
        sendStatus(code, status);
    }

    private void finishRun(int total) {
        Bundle result = new Bundle();
        result.putInt("passed", passed);
        result.putInt("failed", failed);
        result.putInt("total", total);
        result.putString("environment", environment);
        result.putString("stream", "\nNative smoke: passed=" + passed + " failed=" + failed
                + " total=" + total + "\n" + environment + "\n"
                + (failed == 0 ? "OK" : "FAILURES") + "\n");
        // am instrument's shell exit status is not a sufficient success signal.
        finish(failed == 0 ? Activity.RESULT_OK : Activity.RESULT_CANCELED, result);
    }
}
