package com.coorchice.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowTypeface;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Contracts for #89 (no-op background path submissions) and #91 (platform font inheritance).
 *
 * All observations come from attached, measured views' real draw(Canvas) entry point. Legacy
 * Robolectric can establish submitted commands and font attributes, not GPU overdraw, font glyphs
 * or pixel-perfect rounded contours. In particular, neither text width nor a copied Paint is used
 * as evidence of a typeface. Path/Paint values are captured immediately, before shared state changes.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@LooperMode(LooperMode.Mode.PAUSED)
public class FontAndOverdrawRegressionTest {
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
        root.layout(0, 0, WIDTH, HEIGHT * 2);
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }

    @Test
    public void issue89_defaultTransparentSolidSubmitsNoPathButStillDrawsText() {
        SuperTextView view = backgroundView();
        assertNoPathsForTwoFrames(view);
    }

    @Test
    public void issue89_transparentRoundedStrokeAndSolidIncludeNonzeroRgbAlphaZero() {
        SuperTextView view = backgroundView();
        // A radius larger than the height matches the original issue's rounded thin label.
        view.setCorner(100f).setStrokeWidth(1f);
        for (int transparent : new int[] {Color.TRANSPARENT, 0x00abcdef}) {
            view.setSolid(transparent).setStrokeColor(transparent);
            assertNoPathsForTwoFrames(view);
        }
    }

    @Test
    public void issue89_strokeAlphaOneHalfAndOpaqueAreAllVisible() {
        SuperTextView view = backgroundView();
        view.setSolid(Color.TRANSPARENT).setStrokeWidth(3f);
        for (int alpha : new int[] {1, 128, 255}) {
            int color = Color.argb(alpha, 0x12, 0x34, 0x56);
            view.setStrokeColor(color);
            for (int frame = 0; frame < 2; frame++) {
                RecordingCanvas canvas = record(view);
                assertEquals("Only the visible stroke is submitted; alpha=" + alpha,
                        1, canvas.paths.size());
                assertPath(canvas.paths.get(0), Paint.Style.STROKE, color, 3f);
            }
        }
    }

    @Test
    public void issue89_solidAlphaOneHalfAndOpaqueAreAllVisible() {
        SuperTextView view = backgroundView();
        // A transparent nonzero-width stroke must not accompany the visible fill.
        view.setStrokeWidth(2f).setStrokeColor(0x00abcdef);
        for (int alpha : new int[] {1, 128, 255}) {
            int color = Color.argb(alpha, 0x65, 0x43, 0x21);
            view.setSolid(color);
            for (int frame = 0; frame < 2; frame++) {
                RecordingCanvas canvas = record(view);
                assertEquals("Only the visible fill is submitted; alpha=" + alpha,
                        1, canvas.paths.size());
                assertPath(canvas.paths.get(0), Paint.Style.FILL, color, 0f);
            }
        }
    }

    @Test
    public void issue89_zeroWidthOpaqueStrokeDoesNotBecomeAHairline() {
        SuperTextView view = backgroundView();
        view.setSolid(Color.GREEN).setStrokeColor(Color.BLUE).setStrokeWidth(0f);
        for (int frame = 0; frame < 2; frame++) {
            RecordingCanvas canvas = record(view);
            assertEquals(1, canvas.paths.size());
            assertPath(canvas.paths.get(0), Paint.Style.FILL, Color.GREEN, 0f);
        }
    }

    @Test
    public void issue89_visibleStrokeAndFillKeepOrderColorsAndWidths() {
        SuperTextView view = backgroundView();
        view.setSolid(Color.GREEN).setStrokeColor(Color.BLUE).setStrokeWidth(4f);
        for (int frame = 0; frame < 2; frame++) {
            RecordingCanvas canvas = record(view);
            assertEquals(2, canvas.paths.size());
            assertPath(canvas.paths.get(0), Paint.Style.STROKE, Color.BLUE, 4f);
            assertPath(canvas.paths.get(1), Paint.Style.FILL, Color.GREEN, 0f);
        }
    }

    @Test
    public void issue89_visibleGradientSurvivesTransparentSolid() {
        SuperTextView view = backgroundView();
        view.setSolid(Color.TRANSPARENT).setShaderStartColor(Color.RED)
                .setShaderEndColor(Color.BLUE).setShaderEnable(true);
        for (int frame = 0; frame < 2; frame++) {
            RecordingCanvas canvas = record(view);
            assertEquals(1, canvas.paths.size());
            PathPass fill = canvas.paths.get(0);
            assertEquals(Paint.Style.FILL, fill.style);
            assertTrue("The transparent solid must not suppress its visible gradient",
                    fill.shader instanceof LinearGradient);
            assertNonemptyBounds(fill, 0f, 0f, WIDTH, HEIGHT);
        }
    }

    @Test
    public void issue89_drawableBackgroundRetainsFilledGeometryAfterVisibleStroke() {
        SuperTextView view = backgroundView();
        Bitmap pixels = Bitmap.createBitmap(12, 12, Bitmap.Config.ARGB_8888);
        pixels.eraseColor(Color.GREEN);
        view.setSolid(Color.TRANSPARENT).setStrokeColor(Color.BLUE).setStrokeWidth(4f)
                .setCorner(20f).setDrawable(new BitmapDrawable(activity.getResources(), pixels))
                .setDrawableAsBackground(true);
        // Do not prime the drawable cache: the first frame must already have a valid solidPath.
        for (int frame = 0; frame < 2; frame++) {
            RecordingCanvas canvas = record(view);
            assertEquals("Visible stroke + drawable fill, with no transparent solid submission",
                    2, canvas.paths.size());
            assertPath(canvas.paths.get(0), Paint.Style.STROKE, Color.BLUE, 4f);
            PathPass background = canvas.paths.get(1);
            assertTrue(background.shader instanceof BitmapShader);
            assertEquals("Skipping solid submission must still reset the shared paint to FILL",
                    Paint.Style.FILL, background.style);
            assertNonemptyBounds(background, 4f, 4f, WIDTH - 4f, HEIGHT - 4f);
        }
    }

    @Test
    public void issue89_pressedBackgroundKeepsGeometryAcrossFramesUpAndCancel() {
        SuperTextView view = backgroundView();
        view.setSolid(Color.TRANSPARENT).setStrokeColor(Color.TRANSPARENT).setStrokeWidth(4f)
                .setCorner(20f).setPressBgColor(Color.MAGENTA);
        // The real first draw installs PressAdjuster, just as it does before a user's gesture.
        record(view);
        for (int terminal : new int[] {MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL}) {
            assertTrue(send(view, MotionEvent.ACTION_DOWN));
            for (int frame = 0; frame < 2; frame++) {
                RecordingCanvas canvas = record(view);
                assertEquals("Only the visible pressed background is submitted",
                        1, canvas.paths.size());
                PathPass pressed = canvas.paths.get(0);
                assertPath(pressed, Paint.Style.FILL, Color.MAGENTA, 0f);
                assertNonemptyBounds(pressed, 4f, 4f, view.getWidth() - 4f,
                        view.getHeight() - 4f);
                for (float radius : view.getCorners()) {
                    assertEquals("Transparent solid must still prepare press corner geometry",
                            view.getCorner() - 2f, radius, 0f);
                }
                // The next pressed frame must rebuild geometry, not reuse the old solid state.
                if (frame == 0) {
                    view.setCorner(view.getCorner() + 6f);
                    layout(view, view.getWidth() + 8, view.getHeight() + 4);
                }
            }
            send(view, terminal);
            assertNoPathsForTwoFrames(view);
        }
    }

    @Test
    public void issue89_transparentVisibleTransparentSettersTakeEffectEveryFrame() {
        SuperTextView view = backgroundView();
        view.setStrokeWidth(2f).setSolid(0x00abcdef).setStrokeColor(0x00123456);
        assertNoPathsForTwoFrames(view);
        view.setSolid(Color.GREEN).setStrokeColor(Color.BLUE);
        for (int frame = 0; frame < 2; frame++) {
            RecordingCanvas canvas = record(view);
            assertEquals(2, canvas.paths.size());
            assertPath(canvas.paths.get(0), Paint.Style.STROKE, Color.BLUE, 2f);
            assertPath(canvas.paths.get(1), Paint.Style.FILL, Color.GREEN, 0f);
        }
        view.setSolid(0x00abcdef).setStrokeColor(0x00123456);
        assertNoPathsForTwoFrames(view);
    }

    @Test
    public void issue91_xmlInheritedTypefaceSurvivesPlainDrawing() {
        assertXmlTypefaceDuringDrawing(false, false);
    }

    @Test
    public void issue91_xmlInheritedTypefaceSurvivesTextStrokeDrawing() {
        assertXmlTypefaceDuringDrawing(true, false);
    }

    @Test
    public void issue91_xmlInheritedTypefaceSurvivesTextGradientDrawing() {
        assertXmlTypefaceDuringDrawing(false, true);
    }

    @Test
    public void issue91_xmlInheritedTypefaceSurvivesCombinedStrokeAndGradient() {
        assertXmlTypefaceDuringDrawing(true, true);
    }

    private void assertXmlTypefaceDuringDrawing(boolean stroke, boolean gradient) {
        View fixture = LayoutInflater.from(activity).inflate(R.layout.remaining_font, root, false);
        attach(fixture, WIDTH, HEIGHT * 2);
        TextView platform = fixture.findViewById(R.id.remaining_platform_text);
        SuperTextView view = fixture.findViewById(R.id.remaining_super_text);
        layout(platform, WIDTH, HEIGHT);
        layout(view, WIDTH, HEIGHT);
        assertTrue(platform.isAttachedToWindow());
        assertTrue(view.isAttachedToWindow());
        assertNotNull(platform.getLayout());
        assertNotNull(view.getLayout());
        Typeface platformTypeface = platform.getTypeface();
        Typeface originalTypeface = view.getTypeface();
        assertXmlFont(platformTypeface);
        assertXmlFont(originalTypeface);
        assertEquals(platform.getText().toString(), view.getText().toString());
        assertEquals(platformTypeface.getStyle(), originalTypeface.getStyle());
        assertEquals(fontFamily(platformTypeface), fontFamily(originalTypeface));
        view.setTextStroke(stroke).setTextStrokeColor(Color.BLUE).setTextFillColor(Color.RED)
                .setTextStrokeWidth(3f).setTextShaderStartColor(Color.RED)
                .setTextShaderEndColor(Color.YELLOW).setTextShaderEnable(gradient);
        for (int frame = 0; frame < 2; frame++) {
            RecordingCanvas platformCanvas = record(platform);
            assertEquals("The platform control must actually draw text", 1,
                    platformCanvas.text.size());
            assertTextTypeface(platformCanvas.text.get(0), platformTypeface);
            RecordingCanvas canvas = record(view);
            assertEquals("Observe every real text pass, including the stroke pass",
                    stroke ? 2 : 1, canvas.text.size());
            for (TextPass pass : canvas.text) {
                assertTextTypeface(pass, originalTypeface);
                assertEquals(fontFamily(platformTypeface), pass.family);
            }
            if (stroke) {
                TextPass outline = canvas.text.get(0);
                assertTrue(outline.style == Paint.Style.STROKE
                        || outline.style == Paint.Style.FILL_AND_STROKE);
                assertNull("The outline must not inherit the fill gradient", outline.shader);
            }
            TextPass fill = canvas.text.get(canvas.text.size() - 1);
            // Legacy ShadowPaint leaves the platform's default style null; font inheritance,
            // not that shadow default, is the contract here. Device tests exercise real Skia.
            if (gradient) {
                assertNotNull("The gradient branch must really execute", fill.shader);
            } else {
                assertNull(fill.shader);
            }
            assertSame("Drawing must not replace the XML-inherited typeface",
                    originalTypeface, view.getTypeface());
            assertSame(platformTypeface, platform.getTypeface());
            assertXmlFont(view.getTypeface());
        }
    }

    private static void assertXmlFont(Typeface typeface) {
        assertNotNull(typeface);
        assertEquals("monospace", fontFamily(typeface));
        assertEquals(Typeface.BOLD_ITALIC, typeface.getStyle());
    }

    private static String fontFamily(Typeface typeface) {
        return ((ShadowTypeface) Shadow.extract(typeface)).getFontDescription().getFamilyName();
    }

    private static void assertTextTypeface(TextPass pass, Typeface original) {
        assertSame("Read the original drawText Paint's typeface, not a Paint copy", original,
                pass.typeface);
        assertEquals("monospace", pass.family);
        assertEquals(Typeface.BOLD_ITALIC, pass.typefaceStyle);
    }

    private SuperTextView backgroundView() {
        // Isolate these tests from the unrelated programmatic drawable-defaults contract (#114).
        SuperTextView view = new SuperTextView(activity, Robolectric.buildAttributeSet().build());
        view.setText("iiiiWW");
        view.setTextSize(20f);
        attach(view, WIDTH, HEIGHT);
        assertNotNull(view.getLayout());
        return view;
    }

    private void attach(View view, int width, int height) {
        root.addView(view, new FrameLayout.LayoutParams(width, height));
        layout(view, width, height);
        assertTrue("A detached view would make SuperTextView.onDraw a false positive",
                view.isAttachedToWindow());
    }

    private static void layout(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, width, height);
    }

    private static RecordingCanvas record(TextView view) {
        RecordingCanvas canvas = new RecordingCanvas(view.getWidth(), view.getHeight());
        view.draw(canvas);
        assertFalse("A real attached draw must reach the platform text layout", canvas.text.isEmpty());
        return canvas;
    }

    private static void assertNoPathsForTwoFrames(SuperTextView view) {
        for (int frame = 0; frame < 2; frame++) {
            assertEquals("Fully transparent backgrounds must not submit drawPath; frame=" + frame,
                    0, record(view).paths.size());
        }
    }

    private static void assertPath(PathPass pass, Paint.Style style, int color, float width) {
        assertEquals(style, pass.style);
        assertEquals(color, pass.color);
        assertEquals(Color.alpha(color), pass.alpha);
        if (style == Paint.Style.STROKE) assertEquals(width, pass.width, 0f);
        // Stroke width is ignored by FILL. Legacy ShadowPaint.reset does not reset this field.
        assertNull("This pass is a solid color, not a shader", pass.shader);
        assertFalse("Visible submissions need real geometry", pass.empty);
        assertFalse(pass.bounds.isEmpty());
    }

    private static void assertNonemptyBounds(PathPass pass, float left, float top,
                                            float right, float bottom) {
        assertFalse("The first and subsequent frames must have a nonempty path", pass.empty);
        assertEquals(left, pass.bounds.left, 0.01f);
        assertEquals(top, pass.bounds.top, 0.01f);
        assertEquals(right, pass.bounds.right, 0.01f);
        assertEquals(bottom, pass.bounds.bottom, 0.01f);
    }

    private static boolean send(SuperTextView view, int action) {
        MotionEvent event = MotionEvent.obtain(0, action == MotionEvent.ACTION_DOWN ? 0 : 10,
                action, view.getWidth() / 2f, view.getHeight() / 2f, 0);
        try {
            return view.onTouchEvent(event);
        } finally {
            event.recycle();
        }
    }

    private static final class PathPass {
        final Paint.Style style;
        final int color;
        final int alpha;
        final float width;
        final Shader shader;
        final boolean empty;
        final RectF bounds = new RectF();

        PathPass(Path path, Paint paint) {
            style = paint.getStyle();
            color = paint.getColor();
            alpha = Color.alpha(color);
            width = paint.getStrokeWidth();
            shader = paint.getShader();
            empty = path.isEmpty();
            path.computeBounds(bounds, true);
        }
    }

    private static final class TextPass {
        final Typeface typeface;
        final String family;
        final int typefaceStyle;
        final Paint.Style style;
        final Shader shader;

        TextPass(Paint originalPaint) {
            // Legacy ShadowPaint's copy/set does not reliably preserve typefaces. Read in-place.
            typeface = originalPaint.getTypeface();
            family = typeface == null ? null : fontFamily(typeface);
            typefaceStyle = typeface == null ? -1 : typeface.getStyle();
            style = originalPaint.getStyle();
            shader = originalPaint.getShader();
        }
    }

    private static final class RecordingCanvas extends Canvas {
        final List<PathPass> paths = new ArrayList<>();
        final List<TextPass> text = new ArrayList<>();

        RecordingCanvas(int width, int height) {
            super(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
        }

        @Override
        public void drawPath(Path path, Paint paint) {
            paths.add(new PathPass(path, paint));
            super.drawPath(path, paint);
        }

        @Override
        public void drawText(String value, float x, float y, Paint paint) {
            text.add(new TextPass(paint));
            super.drawText(value, x, y, paint);
        }
    }
}
