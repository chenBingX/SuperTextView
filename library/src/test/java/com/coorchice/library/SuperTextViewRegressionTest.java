package com.coorchice.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.coorchice.library.image_engine.Engine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Behavioral contracts for issues #122, #114, #104 and #120.
 *
 * Views are attached and laid out before drawing. Reflection is limited to the #104 bitmap-shader
 * cache; text drawing is observed through a recording Canvas, not TextView's private fields or
 * SuperTextView's private sdkOnDraw method. The same tests run against HEAD (-Pbaseline=true) and
 * the working tree, so a fix cannot silently redefine the expected behavior.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@LooperMode(LooperMode.Mode.PAUSED)
public class SuperTextViewRegressionTest {
    private static final int WIDTH = 160;
    private static final int HEIGHT = 80;
    private ActivityController<Activity> controller;
    private Activity activity;
    private FrameLayout root;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Activity.class).setup().visible();
        activity = controller.get();
        root = new FrameLayout(activity);
        activity.setContentView(root);
        root.layout(0, 0, WIDTH, HEIGHT);
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
        ImageEngine.install(null);
    }

    @Test
    public void issue122_drawable1DownUpClicksOnceAndConsumesUp() {
        assertGesture(1, MotionEvent.ACTION_UP, false, 1);
    }

    @Test
    public void issue122_drawable2DownUpClicksOnceAndConsumesUp() {
        assertGesture(2, MotionEvent.ACTION_UP, false, 1);
    }

    @Test
    public void issue122_drawable1DownCancelDoesNotClick() {
        assertGesture(1, MotionEvent.ACTION_CANCEL, false, 0);
    }

    @Test
    public void issue122_drawable2DownCancelDoesNotClick() {
        assertGesture(2, MotionEvent.ACTION_CANCEL, false, 0);
    }

    @Test
    public void issue122_drawable1ReentrantCancelDoesNotClickAgain() {
        assertGesture(1, MotionEvent.ACTION_UP, true, 1);
    }

    @Test
    public void issue122_drawable2ReentrantCancelDoesNotClickAgain() {
        assertGesture(2, MotionEvent.ACTION_UP, true, 1);
    }

    private void assertGesture(final int slot, int terminalAction, final boolean reenter,
                               int expectedClicks) {
        // Attribute construction isolates touch regressions from the programmatic-defaults issue.
        SuperTextView view = withXmlDefaults();
        RecordingDrawable first = new RecordingDrawable(Color.RED);
        RecordingDrawable second = new RecordingDrawable(Color.BLUE);
        view.setDrawable(first).setDrawable2(second).setShowState(true).setShowState2(true);
        view.setStateDrawableMode(SuperTextView.DrawableMode.LEFT);
        view.setStateDrawable2Mode(SuperTextView.DrawableMode.RIGHT);
        view.setDrawableWidth(32).setDrawableHeight(32);
        view.setDrawable2Width(32).setDrawable2Height(32);
        attach(view, WIDTH, HEIGHT);
        draw(view);
        Rect bounds = slot == 1 ? first.getBounds() : second.getBounds();
        final float x = bounds.exactCenterX();
        final float y = bounds.exactCenterY();
        assertFalse("The drawable must have a real hit region", bounds.isEmpty());
        final int[] clicks = new int[2];
        view.setOnDrawableClickedListener(new SuperTextView.OnDrawableClickedListener() {
            private void clicked(SuperTextView target, int clickedSlot) {
                clicks[clickedSlot - 1]++;
                // Guard re-entry once so a broken implementation fails, rather than overflowing.
                if (reenter && clicks[clickedSlot - 1] == 1) {
                    send(target, MotionEvent.ACTION_CANCEL, x, y);
                }
            }

            @Override
            public void onDrawable1Clicked(SuperTextView target) {
                clicked(target, 1);
            }

            @Override
            public void onDrawable2Clicked(SuperTextView target) {
                clicked(target, 2);
            }
        });
        assertTrue("Drawable DOWN must be consumed", send(view, MotionEvent.ACTION_DOWN, x, y));
        assertEquals("DOWN alone is not a click", 0, clicks[0] + clicks[1]);
        boolean consumed = send(view, terminalAction, x, y);
        assertEquals("Only UP completes a drawable click, including synchronous re-entry",
                expectedClicks, clicks[slot - 1]);
        assertEquals("The other slot must not be clicked", 0, clicks[2 - slot]);
        if (terminalAction == MotionEvent.ACTION_UP) {
            assertTrue("A drawable-owned UP must remain consumed after clearing gesture state",
                    consumed);
        }
    }

    @Test
    public void issue114_programmaticDrawableDefaultsAreNonNullAndMatchInflatedXml() {
        SuperTextView programmatic = new SuperTextView(activity);
        SuperTextView xml = (SuperTextView) LayoutInflater.from(activity)
                .inflate(R.layout.regression_defaults, root, false);
        assertNotNull(programmatic.getStateDrawableLayer());
        assertNotNull(programmatic.getStateDrawable2Layer());
        assertNotNull(programmatic.getStateDrawableMode());
        assertNotNull(programmatic.getStateDrawable2Mode());
        assertEquals(xml.getStateDrawableLayer(), programmatic.getStateDrawableLayer());
        assertEquals(xml.getStateDrawable2Layer(), programmatic.getStateDrawable2Layer());
        assertEquals(xml.getStateDrawableMode(), programmatic.getStateDrawableMode());
        assertEquals(xml.getStateDrawable2Mode(), programmatic.getStateDrawable2Mode());
        assertFalse(programmatic.isShowState());
        assertFalse(programmatic.isShowState2());
        assertEquals(xml.isShowState(), programmatic.isShowState());
        assertEquals(xml.isShowState2(), programmatic.isShowState2());
    }

    @Test
    public void issue114_programmaticDrawable1CanBeShownAndDrawn() {
        assertProgrammaticDrawableDraws(1);
    }

    @Test
    public void issue114_programmaticDrawable2CanBeShownAndDrawn() {
        assertProgrammaticDrawableDraws(2);
    }

    private void assertProgrammaticDrawableDraws(int slot) {
        SuperTextView view = new SuperTextView(activity);
        RecordingDrawable drawable = new RecordingDrawable(Color.GREEN);
        setDrawable(view, slot, drawable);
        attach(view, WIDTH, HEIGHT);
        draw(view);
        assertEquals("A default-hidden drawable must not draw", 0, drawable.drawCount);
        if (slot == 1) {
            view.setShowState(true);
        } else {
            view.setShowState2(true);
        }
        draw(view);
        assertTrue("Showing a drawable on a Java-constructed view must draw it",
                drawable.drawCount > 0);
        assertFalse(drawable.lastBounds.isEmpty());
    }

    @Test
    public void issue104_replacingBackgroundDrawableInvalidatesAndRebuildsShader() throws Exception {
        SuperTextView view = backgroundView();
        BitmapShader oldShader = backgroundShader(view);
        RecordingDrawable replacement = new RecordingDrawable(Color.BLUE);
        view.setDrawable(replacement);
        assertNull("A replacement must invalidate the old pixels", backgroundShader(view));
        draw(view);
        assertNotNull(backgroundShader(view));
        assertNotSame(oldShader, backgroundShader(view));
        assertTrue("New drawable pixels must be copied", replacement.drawCount > 0);
    }

    @Test
    public void issue104_widthOnlyChangeRebuildsBackgroundShader() throws Exception {
        assertSingleDimensionResizeRebuildsShader(WIDTH + 20, HEIGHT);
    }

    @Test
    public void issue104_heightOnlyChangeRebuildsBackgroundShader() throws Exception {
        assertSingleDimensionResizeRebuildsShader(WIDTH, HEIGHT + 20);
    }

    private void assertSingleDimensionResizeRebuildsShader(int width, int height) throws Exception {
        SuperTextView view = backgroundView();
        BitmapShader oldShader = backgroundShader(view);
        layout(view, width, height);
        assertNull("Changing either dimension invalidates the shader", backgroundShader(view));
        draw(view);
        assertNotNull(backgroundShader(view));
        assertNotSame(oldShader, backgroundShader(view));
    }

    @Test
    public void issue104_switchingFromFitCenterToFitXyDropsTheTemporaryCanvas() throws Exception {
        SuperTextView view = backgroundView();
        view.setScaleType(SuperTextView.ScaleType.FIT_CENTER);
        draw(view);
        assertNotNull(cacheField(view, "tempDrawableBgCanvas"));
        // FIT_XY keeps the same backing-bitmap dimensions, so reallocation cannot mask a leak.
        view.setScaleType(SuperTextView.ScaleType.FIT_XY);
        draw(view);
        assertNull("FIT_XY must not retain FIT_CENTER's intermediate canvas",
                cacheField(view, "tempDrawableBgCanvas"));
    }

    @Test
    public void drawable1ReplacementReleasesOldCallback() {
        assertReplacementCallbacks(1);
    }

    @Test
    public void drawable2ReplacementReleasesOldCallback() {
        assertReplacementCallbacks(2);
    }

    private void assertReplacementCallbacks(int slot) {
        SuperTextView view = withXmlDefaults();
        RecordingDrawable old = new RecordingDrawable(Color.RED);
        RecordingDrawable replacement = new RecordingDrawable(Color.BLUE);
        setDrawable(view, slot, old);
        assertSame(view, old.getCallback());
        setDrawable(view, slot, replacement);
        assertNull("The previous drawable must not keep the view alive", old.getCallback());
        assertSame(view, replacement.getCallback());
    }

    @Test
    public void drawable1CanBeClearedWithoutCrashingAndReleasesCallback() {
        assertClearDrawable(1);
    }

    @Test
    public void drawable2CanBeClearedWithoutCrashingAndReleasesCallback() {
        assertClearDrawable(2);
    }

    private void assertClearDrawable(int slot) {
        SuperTextView view = withXmlDefaults();
        RecordingDrawable old = new RecordingDrawable(Color.RED);
        setDrawable(view, slot, old);
        setDrawable(view, slot, null);
        assertNull(old.getCallback());
        assertNull(slot == 1 ? view.getDrawable() : view.getDrawable2());
        attach(view, WIDTH, HEIGHT);
        draw(view);
    }

    @Test
    public void drawable1ReassigningSameInstanceKeepsItsCallback() {
        assertSameDrawableCallback(1);
    }

    @Test
    public void drawable2ReassigningSameInstanceKeepsItsCallback() {
        assertSameDrawableCallback(2);
    }

    private void assertSameDrawableCallback(int slot) {
        SuperTextView view = withXmlDefaults();
        RecordingDrawable drawable = new RecordingDrawable(Color.RED);
        setDrawable(view, slot, drawable);
        setDrawable(view, slot, drawable);
        assertSame("Assigning the current drawable must not detach its own callback",
                view, drawable.getCallback());
    }

    @Test
    public void drawable2ReplacementDoesNotInvalidateDrawable1BackgroundShader() throws Exception {
        // Only drawable1 supports background mode. Slot 2 must not clear its unrelated cache.
        SuperTextView view = backgroundView();
        BitmapShader shader = backgroundShader(view);
        RecordingDrawable replacement = new RecordingDrawable(Color.GREEN);
        view.setDrawable2(replacement).setShowState2(true);
        assertSame(shader, backgroundShader(view));
        draw(view);
        assertSame(shader, backgroundShader(view));
        assertTrue(replacement.drawCount > 0);
    }

    @Test
    public void issue104_drawable1EarlierSameUrlCannotOverwriteLatestRequest() {
        assertEarlierSameUrlCannotOverwriteLatestRequest(1);
    }

    @Test
    public void issue104_drawable2EarlierSameUrlCannotOverwriteLatestRequest() {
        assertEarlierSameUrlCannotOverwriteLatestRequest(2);
    }

    private void assertEarlierSameUrlCannotOverwriteLatestRequest(int slot) {
        CapturingEngine engine = new CapturingEngine();
        ImageEngine.install(engine);
        SuperTextView view = withXmlDefaults();
        setUrl(view, slot, "https://example.invalid/A.png");
        setUrl(view, slot, "https://example.invalid/B.png");
        setUrl(view, slot, "https://example.invalid/A.png");
        assertEquals(3, engine.callbacks.size());
        RecordingDrawable newest = new RecordingDrawable(Color.GREEN);
        engine.callbacks.get(2).onCompleted(newest);
        assertSame(newest, slot == 1 ? view.getDrawable() : view.getDrawable2());
        engine.callbacks.get(1).onCompleted(new RecordingDrawable(Color.BLUE));
        assertSame("An older different URL must not win", newest,
                slot == 1 ? view.getDrawable() : view.getDrawable2());
        RecordingDrawable staleA = new RecordingDrawable(Color.RED);
        engine.callbacks.get(0).onCompleted(staleA);
        assertSame("Request identity matters even when the URL returns to A", newest,
                slot == 1 ? view.getDrawable() : view.getDrawable2());
        assertNull("A stale result must never become owned by the view", staleA.getCallback());
    }

    @Test
    public void issue104_drawable1ManualReplacementSupersedesPendingRequest() {
        assertManualReplacementSupersedesPendingRequest(1);
    }

    @Test
    public void issue104_drawable2ManualReplacementSupersedesPendingRequest() {
        assertManualReplacementSupersedesPendingRequest(2);
    }

    private void assertManualReplacementSupersedesPendingRequest(int slot) {
        CapturingEngine engine = new CapturingEngine();
        ImageEngine.install(engine);
        SuperTextView view = withXmlDefaults();
        setUrl(view, slot, "https://example.invalid/A.png");
        assertEquals(1, engine.callbacks.size());
        RecordingDrawable placeholder = new RecordingDrawable(Color.YELLOW);
        setDrawable(view, slot, placeholder);
        RecordingDrawable stale = new RecordingDrawable(Color.RED);
        engine.callbacks.get(0).onCompleted(stale);
        assertSame("An explicit drawable assignment cancels the pending image's ownership",
                placeholder, slot == 1 ? view.getDrawable() : view.getDrawable2());
        assertSame(view, placeholder.getCallback());
        assertNull(stale.getCallback());
    }

    private static void setUrl(SuperTextView view, int slot, String url) {
        if (slot == 1) {
            view.setUrlImage(url);
        } else {
            view.setUrlImage2(url);
        }
    }

    @Test
    public void issue120_strokeAndFillUseConfiguredColorsOnEveryRealDraw() {
        RecordingCanvas textCanvas = new RecordingCanvas();
        CountingTextView view = strokedTextView();
        for (int frame = 0; frame < 2; frame++) {
            textCanvas.passes.clear();
            view.draw(textCanvas);
            assertEquals("Exactly one stroke and one fill pass", 2, textCanvas.passes.size());
            DrawPass stroke = textCanvas.passes.get(0);
            DrawPass fill = textCanvas.passes.get(1);
            assertEquals(Color.BLUE, stroke.color);
            assertTrue(stroke.style == Paint.Style.STROKE
                    || stroke.style == Paint.Style.FILL_AND_STROKE);
            assertEquals(4f, stroke.width, 0f);
            assertNull("The stroke is a solid color", stroke.shader);
            assertEquals(Color.RED, fill.color);
            assertEquals(Paint.Style.FILL, fill.style);
            assertNull(fill.shader);
        }
    }

    @Test
    public void issue120_restoresOriginalColorStateListAndPaintAfterEveryDraw() {
        RecordingCanvas textCanvas = new RecordingCanvas();
        CountingTextView view = strokedTextView();
        ColorStateList colors = originalTextColors();
        view.setTextColor(colors);
        Shader originalShader = new LinearGradient(0, 0, 20, 0,
                Color.CYAN, Color.YELLOW, Shader.TileMode.CLAMP);
        view.getPaint().setStyle(Paint.Style.STROKE);
        view.getPaint().setStrokeWidth(7.5f);
        view.getPaint().setShader(originalShader);
        int originalColor = view.getPaint().getColor();
        for (int frame = 0; frame < 2; frame++) {
            textCanvas.passes.clear();
            view.draw(textCanvas);
            assertEquals(2, textCanvas.passes.size());
            assertSame("The caller's ColorStateList must not be replaced", colors, view.getTextColors());
            assertEquals("The active original text color must also be restored",
                    Color.MAGENTA, view.getCurrentTextColor());
            assertEquals(Paint.Style.STROKE, view.getPaint().getStyle());
            assertEquals(7.5f, view.getPaint().getStrokeWidth(), 0f);
            assertEquals(originalColor, view.getPaint().getColor());
            assertSame(originalShader, view.getPaint().getShader());
        }
        view.setPressed(true);
        assertEquals("Stateful colors must still respond after drawing",
                Color.GREEN, view.getCurrentTextColor());
    }

    @Test
    public void issue120_drawingNeverRequestsAnotherFrameOrLayout() {
        RecordingCanvas textCanvas = new RecordingCanvas();
        CountingTextView view = strokedTextView();
        assertTrue(view.getIncludeFontPadding());
        // Observe invalidation that reaches platform View, not an overridden call that a
        // production implementation may legitimately suppress for temporary draw colors.
        ShadowView shadow = Shadow.extract(view);
        shadow.clearWasInvalidated();
        view.monitor = true;
        for (int frame = 0; frame < 3; frame++) {
            draw(view);
            assertFalse("Drawing must not reach platform invalidate()",
                    shadow.wasInvalidated());
        }
        view.monitor = false;
        assertEquals("Drawing must not request another layout", 0, view.layoutRequests);
        assertEquals("Drawing must not schedule itself again", 0, view.invalidations);
        assertTrue("Drawing must not mutate includeFontPadding", view.getIncludeFontPadding());
        assertFalse(view.isLayoutRequested());
    }

    @Test
    public void issue120_gradientIsAppliedOnlyToFillAndOriginalShaderIsRestored() {
        RecordingCanvas textCanvas = new RecordingCanvas();
        CountingTextView view = strokedTextView();
        view.setTextShaderStartColor(Color.RED).setTextShaderEndColor(Color.YELLOW);
        view.setTextShaderEnable(true);
        Shader originalShader = new LinearGradient(0, 0, 20, 0,
                Color.CYAN, Color.GREEN, Shader.TileMode.CLAMP);
        view.getPaint().setShader(originalShader);
        for (int frame = 0; frame < 2; frame++) {
            textCanvas.passes.clear();
            view.draw(textCanvas);
            assertEquals(2, textCanvas.passes.size());
            assertEquals(Color.BLUE, textCanvas.passes.get(0).color);
            assertNull("A caller's shader must not tint the solid stroke", textCanvas.passes.get(0).shader);
            assertEquals(Paint.Style.FILL, textCanvas.passes.get(1).style);
            assertNotNull(textCanvas.passes.get(1).shader);
            assertNotSame(originalShader, textCanvas.passes.get(1).shader);
            assertSame(originalShader, view.getPaint().getShader());
        }
    }

    private CountingTextView strokedTextView() {
        CountingTextView view = new CountingTextView(activity);
        // Plain, single-line text takes BoringLayout's direct Canvas.drawText path. Unlike
        // ReplacementSpan/TextLine, this does not depend on legacy ShadowPaint.set(Paint).
        view.setText("Regression");
        view.setTextSize(20);
        view.setTextColor(originalTextColors());
        view.setIncludeFontPadding(true);
        view.setTextStroke(true).setTextStrokeColor(Color.BLUE)
                .setTextFillColor(Color.RED).setTextStrokeWidth(4f);
        attach(view, WIDTH, HEIGHT);
        return view;
    }

    private ColorStateList originalTextColors() {
        return new ColorStateList(new int[][] {
                new int[] {android.R.attr.state_pressed}, new int[0]
        }, new int[] {Color.GREEN, Color.MAGENTA});
    }

    private SuperTextView backgroundView() throws Exception {
        SuperTextView view = withXmlDefaults();
        view.setDrawable(new RecordingDrawable(Color.RED));
        view.setDrawableAsBackground(true);
        attach(view, WIDTH, HEIGHT);
        draw(view);
        assertNotNull("Background drawing must initialize the bitmap shader", backgroundShader(view));
        return view;
    }

    private SuperTextView withXmlDefaults() {
        return new SuperTextView(activity, Robolectric.buildAttributeSet().build());
    }

    private void attach(SuperTextView view, int width, int height) {
        root.addView(view, new FrameLayout.LayoutParams(width, height));
        layout(view, width, height);
        assertTrue("A detached view would make onDraw a false positive", view.isAttachedToWindow());
        assertNotNull("The platform text layout must exist", view.getLayout());
    }

    private static void layout(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, width, height);
    }

    private static void draw(SuperTextView view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        bitmap.recycle();
    }

    private static boolean send(SuperTextView view, int action, float x, float y) {
        MotionEvent event = MotionEvent.obtain(0, action == MotionEvent.ACTION_DOWN ? 0 : 10,
                action, x, y, 0);
        try {
            return view.onTouchEvent(event);
        } finally {
            event.recycle();
        }
    }

    private static void setDrawable(SuperTextView view, int slot, Drawable drawable) {
        if (slot == 1) {
            view.setDrawable(drawable);
        } else {
            view.setDrawable2(drawable);
        }
    }

    private static BitmapShader backgroundShader(SuperTextView view) throws Exception {
        return (BitmapShader) cacheField(view, "drawableBackgroundShader");
    }

    private static Object cacheField(SuperTextView view, String name) throws Exception {
        Field field = SuperTextView.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(view);
    }

    private static final class CapturingEngine implements Engine {
        final List<ImageEngine.Callback> callbacks = new ArrayList<>();

        @Override
        public void load(String url, ImageEngine.Callback callback) {
            callbacks.add(callback);
        }
    }

    private static final class RecordingDrawable extends Drawable {
        private final Paint paint = new Paint();
        private final Rect lastBounds = new Rect();
        private int drawCount;

        RecordingDrawable(int color) {
            paint.setColor(color);
        }

        @Override
        public void draw(Canvas canvas) {
            drawCount++;
            lastBounds.set(getBounds());
            canvas.drawRect(getBounds(), paint);
        }

        @Override
        public int getIntrinsicWidth() {
            return 32;
        }

        @Override
        public int getIntrinsicHeight() {
            return 32;
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    private static final class DrawPass {
        final int color;
        final Paint.Style style;
        final float width;
        final Shader shader;

        DrawPass(Paint paint) {
            color = paint.getColor();
            style = paint.getStyle();
            width = paint.getStrokeWidth();
            shader = paint.getShader();
        }
    }

    private static final class RecordingCanvas extends Canvas {
        final List<DrawPass> passes = new ArrayList<>();

        RecordingCanvas() {
            super(Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888));
        }

        @Override
        public void drawText(String text, float x, float y, Paint paint) {
            passes.add(new DrawPass(paint));
            super.drawText(text, x, y, paint);
        }
    }

    private static final class CountingTextView extends SuperTextView {
        boolean monitor;
        int invalidations;
        int layoutRequests;

        CountingTextView(Context context) {
            super(context, Robolectric.buildAttributeSet().build());
        }

        @Override
        public void postInvalidate() {
            if (monitor) invalidations++;
            super.postInvalidate();
        }

        @Override
        public void postInvalidateDelayed(long delayMilliseconds) {
            if (monitor) invalidations++;
            super.postInvalidateDelayed(delayMilliseconds);
        }

        @Override
        public void postInvalidateOnAnimation() {
            if (monitor) invalidations++;
            super.postInvalidateOnAnimation();
        }

        @Override
        public void requestLayout() {
            if (monitor) layoutRequests++;
            super.requestLayout();
        }
    }
}
