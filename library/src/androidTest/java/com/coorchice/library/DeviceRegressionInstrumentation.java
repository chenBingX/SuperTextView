package com.coorchice.library;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.system.Os;
import android.system.OsConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.coorchice.library.gifdecoder.GifDrawable;
import com.coorchice.library.utils.LogUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/** Framework/Skia device tests; no Robolectric shadows, copied View or mocked native methods. */
public class DeviceRegressionInstrumentation extends Instrumentation {
    interface Check { void run() throws Exception; }
    static class Case {
        final String name;
        final Check check;
        Case(String name, Check check) { this.name = name; this.check = check; }
    }
    private final List<Case> cases = new ArrayList<>();
    private DeviceRegressionActivity activity;

    @Override public void onCreate(Bundle arguments) { super.onCreate(arguments); start(); }

    @Override public void onStart() {
        int passed = 0;
        try {
            Intent intent = new Intent(getTargetContext(), DeviceRegressionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity = (DeviceRegressionActivity) startActivitySync(intent);
            add("environment", () -> {
                String smaps = "unreadable";
                try (BufferedReader reader = new BufferedReader(new FileReader("/proc/self/smaps"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("KernelPageSize:")) { smaps = line; break; }
                    }
                }
                System.out.println("sdk=" + Build.VERSION.SDK_INT + " arch=" +
                        System.getProperty("os.arch") + " pageSizeBytes=" +
                        Os.sysconf(OsConstants._SC_PAGESIZE) + " smaps=" + smaps +
                        " pid=" + Process.myPid());
            });
            int[] layouts = {R.layout.remaining_resource_png, R.layout.remaining_resource_jpeg,
                    R.layout.remaining_resource_vector, R.layout.remaining_resource_shape,
                    R.layout.remaining_resource_selector, R.layout.remaining_resource_gif};
            String[] names = {"png", "jpeg", "vector", "shape", "selector", "gif"};
            for (int i = 0; i < layouts.length; i++) {
                final int layout = layouts[i];
                final boolean gif = i == layouts.length - 1;
                add("xmlBothDrawableSlots_" + names[i], () -> {
                    SuperTextView view = inflate(layout);
                    require(view.getDrawable() != null && view.getDrawable2() != null,
                            "Both XML image slots must load");
                    require((view.getDrawable() instanceof GifDrawable) == gif,
                            "GIF/static classification must agree with the resource");
                    require((view.getDrawable2() instanceof GifDrawable) == gif,
                            "Second slot GIF/static classification");
                    if (gif) {
                        GifDrawable first = (GifDrawable) view.getDrawable();
                        require(first.getWidth() > 0 && first.getHeight() > 0 && first.getFrameCount() > 0,
                                "Real JNI GIF metadata");
                    } else {
                        view.setStateDrawableMode(SuperTextView.DrawableMode.FILL)
                                .setStateDrawable2Mode(SuperTextView.DrawableMode.FILL);
                        Bitmap pixels = draw(view);
                        require(Color.alpha(pixels.getPixel(40, 40)) > 0,
                                "A loaded static image must actually draw pixels");
                    }
                    view.setDrawable((Drawable) null).setDrawable2((Drawable) null);
                });
            }
            add("roundedPngBackgroundPixels", () -> {
                SuperTextView view = inflate(R.layout.remaining_resource_png);
                view.setDrawable2((Drawable) null).setCorner(1000).setDrawableAsBackground(true);
                Bitmap pixels = draw(view);
                require(pixels.getPixel(40, 40) == Color.rgb(51, 102, 153), "Opaque image center");
                require(Color.alpha(pixels.getPixel(0, 0)) == 0, "Rounded image corner is transparent");
            });
            add("zeroGradientEndpointKeepsVisibleFallback", () -> {
                SuperTextView view = attach(new SuperTextView(activity));
                view.setSolid(Color.TRANSPARENT).setShaderStartColor(Color.TRANSPARENT)
                        .setShaderEndColor(Color.BLUE).setShaderEnable(true);
                require(draw(view).getPixel(40, 40) == Color.BLACK,
                        "Invalid gradient preserves the existing real Paint.reset fallback");
            });
            add("roundedThinLabelSubmitsNoInvisiblePaths", () -> {
                SuperTextView view = attach(new SuperTextView(activity));
                view.setCorner(1000).setStrokeWidth(1).setStrokeColor(0x00123456)
                        .setSolid(Color.TRANSPARENT);
                CountingCanvas canvas = new CountingCanvas();
                view.draw(canvas);
                require(canvas.paths == 0, "Transparent stroke and fill must not submit paths");
                view.setSolid(Color.GREEN).setStrokeColor(Color.BLUE);
                canvas = new CountingCanvas(); view.draw(canvas);
                require(canvas.paths == 2, "Visible stroke and fill must both remain");
            });
            for (int mode = 0; mode < 4; mode++) {
                final int variant = mode;
                add("xmlMonospaceTypeface_mode" + mode, () -> {
                    View fixture = LayoutInflater.from(activity).inflate(R.layout.remaining_font,
                            activity.root, false);
                    activity.root.removeAllViews();
                    activity.root.addView(fixture, new FrameLayout.LayoutParams(240, 240));
                    fixture.measure(View.MeasureSpec.makeMeasureSpec(240, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(240, View.MeasureSpec.EXACTLY));
                    fixture.layout(0, 0, 240, 240);
                    TextView platform = fixture.findViewById(R.id.remaining_platform_text);
                    SuperTextView view = fixture.findViewById(R.id.remaining_super_text);
                    Typeface expected = Typeface.create("monospace", Typeface.BOLD_ITALIC);
                    require(expected.equals(platform.getTypeface()), "Platform XML font control");
                    require(expected.equals(view.getTypeface()), "STV inherits fontFamily/textStyle");
                    float width = platform.getPaint().measureText("iiiiiiii");
                    require(Math.abs(width - view.getPaint().measureText("iiiiiiii")) < 0.01f,
                            "Framework glyph metrics agree with platform TextView");
                    view.setTextStroke((variant & 1) != 0).setTextStrokeWidth(2)
                            .setTextStrokeColor(Color.BLUE).setTextFillColor(Color.RED)
                            .setTextShaderEnable((variant & 2) != 0)
                            .setTextShaderStartColor(Color.RED).setTextShaderEndColor(Color.BLUE);
                    for (int frame = 0; frame < 3; frame++) {
                        view.draw(new Canvas(Bitmap.createBitmap(240, 240, Bitmap.Config.ARGB_8888)));
                        require(expected.equals(view.getTypeface()), "Draw must preserve XML font");
                    }
                });
            }
            add("repeatedDrawingHasNoPerFrameDiagnostics", () -> {
                SuperTextView view = attach(new SuperTextView(activity));
                view.setText("remaining issues");
                require(!LogUtils.DEBUG, "Library diagnostics default to off");
                for (int frame = 0; frame < 100; frame++) draw(view);
                // The host also captures logcat and rejects the historical canvas.h diagnostic.
            });
            for (int index = 0; index < cases.size(); index++) {
                Case test = cases.get(index);
                status(test.name, index + 1, 1, "START " + test.name);
                final Throwable[] error = new Throwable[1];
                runOnMainSync(() -> {
                    try { test.check.run(); } catch (Throwable failure) { error[0] = failure; }
                });
                if (error[0] != null) throw new AssertionError(test.name, error[0]);
                passed++;
                status(test.name, index + 1, 0, "PASS " + test.name);
            }
            Bundle receipt = new Bundle();
            receipt.putString("stream", "\nDEVICE_SUMMARY passed=" + passed + " failed=0 total=" + cases.size() + "\n");
            runOnMainSync(() -> activity.finish());
            finish(Activity.RESULT_OK, receipt);
        } catch (Throwable error) {
            Bundle failure = new Bundle();
            failure.putString("stream", "\nDEVICE_FAILURE passed=" + passed + " total=" + cases.size()
                    + "\n" + android.util.Log.getStackTraceString(error));
            if (activity != null) runOnMainSync(() -> activity.finish());
            finish(Activity.RESULT_CANCELED, failure);
        }
    }

    private void add(String name, Check check) { cases.add(new Case(name, check)); }
    private void status(String name, int current, int code, String stream) {
        Bundle result = new Bundle();
        result.putString("class", getClass().getName());
        result.putString("test", name);
        result.putInt("numtests", cases.size());
        result.putInt("current", current);
        result.putString("stream", "\n" + stream + "\n");
        sendStatus(code, result);
    }
    private SuperTextView inflate(int layout) {
        return attach((SuperTextView) LayoutInflater.from(activity).inflate(layout, activity.root, false));
    }
    private SuperTextView attach(SuperTextView view) {
        activity.root.removeAllViews();
        activity.root.addView(view, new FrameLayout.LayoutParams(80, 80));
        view.measure(View.MeasureSpec.makeMeasureSpec(80, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(80, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, 80, 80);
        require(view.isAttachedToWindow(), "View must really be attached");
        return view;
    }
    private Bitmap draw(SuperTextView view) {
        Bitmap pixels = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(pixels));
        return pixels;
    }
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static class CountingCanvas extends Canvas {
        int paths;
        CountingCanvas() { super(Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888)); }
        @Override public void drawPath(Path path, Paint paint) { paths++; super.drawPath(path, paint); }
    }
}
