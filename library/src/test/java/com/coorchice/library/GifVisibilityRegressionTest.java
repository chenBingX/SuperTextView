package com.coorchice.library;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.coorchice.library.gifdecoder.GifDrawable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * GIF owner lifecycle tests. Mockito creates GifDrawable without invoking its
 * private constructor, so these tests never load GifLib or decode native frames.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@LooperMode(LooperMode.Mode.PAUSED)
public class GifVisibilityRegressionTest {

    private ActivityController<Activity> controller;
    private FrameLayout root;
    private LifecycleSuperTextView view;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Activity.class).setup();
        Activity activity = controller.get();
        root = new FrameLayout(activity);
        activity.setContentView(root);
        controller.visible();
        view = new LifecycleSuperTextView(activity);
        root.addView(view, new FrameLayout.LayoutParams(160, 80));
        assertTrue("The fixture must use real attach dispatch", view.isAttachedToWindow());
        assertTrue("The fixture must start visible", view.isShown());
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.pause().stop().destroy();
        }
    }

    @Test
    public void invisibleWindowPausesBothGifSlots() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);

        view.changeWindowVisibility(View.INVISIBLE);

        assertPaused(first, second);
        verify(first.drawable, never()).play();
        verify(second.drawable, never()).play();
    }

    @Test
    public void goneWindowPausesBothGifSlots() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);

        view.changeWindowVisibility(View.GONE);

        assertPaused(first, second);
    }

    @Test
    public void goneToInvisibleDoesNotResumeUntilVisible() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);

        view.changeWindowVisibility(View.GONE);
        view.changeWindowVisibility(View.INVISIBLE);

        assertPaused(first, second);
        verify(first.drawable, never()).play();
        verify(second.drawable, never()).play();

        view.changeWindowVisibility(View.VISIBLE);

        assertTrue(first.playing.get());
        assertTrue(second.playing.get());
        verify(first.drawable, times(1)).play();
        verify(second.drawable, times(1)).play();
    }

    @Test
    public void repeatedHiddenNotificationsPreserveResumeIntent() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);

        view.changeWindowVisibility(View.INVISIBLE);
        view.changeWindowVisibility(View.INVISIBLE);
        view.changeWindowVisibility(View.GONE);
        view.changeWindowVisibility(View.GONE);
        view.changeWindowVisibility(View.VISIBLE);
        view.changeWindowVisibility(View.VISIBLE);

        assertTrue(first.playing.get());
        assertTrue(second.playing.get());
        verify(first.drawable, times(1)).play();
        verify(second.drawable, times(1)).play();
    }

    @Test
    public void bindingWhileWindowIsHiddenImmediatelyPausesBothSlots() {
        view.changeWindowVisibility(View.GONE);

        Playback first = bindFirst(true);
        Playback second = bindSecond(true);

        assertPaused(first, second);
        view.changeWindowVisibility(View.VISIBLE);
        assertTrue(first.playing.get());
        assertTrue(second.playing.get());
        verify(first.drawable, times(1)).play();
        verify(second.drawable, times(1)).play();
    }

    @Test
    public void ownAndAncestorVisibilityPauseWithoutDetaching() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);

        view.setVisibility(View.INVISIBLE);
        assertTrue(view.isAttachedToWindow());
        assertPaused(first, second);
        view.setVisibility(View.VISIBLE);
        assertTrue(first.playing.get());
        assertTrue(second.playing.get());

        root.setVisibility(View.GONE);
        assertTrue(view.isAttachedToWindow());
        assertPaused(first, second);
        root.setVisibility(View.VISIBLE);
        assertTrue(first.playing.get());
        assertTrue(second.playing.get());
    }

    @Test
    public void detachReattachResumesBothSlotsWithoutDestroyingThem() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);

        root.removeView(view);

        assertFalse(view.isAttachedToWindow());
        assertPaused(first, second);
        verify(first.drawable, never()).destroy();
        verify(second.drawable, never()).destroy();
        assertSame(first.drawable, view.getDrawable());
        assertSame(second.drawable, view.getDrawable2());
        assertSame("A detached GIF must remain leased to its original owner",
                view, first.drawable.getCallback());
        assertSame("A detached GIF must remain leased to its original owner",
                view, second.drawable.getCallback());

        root.addView(view, new FrameLayout.LayoutParams(160, 80));

        assertTrue(view.isAttachedToWindow());
        assertTrue(first.playing.get());
        assertTrue(second.playing.get());
        assertSame(view, first.drawable.getCallback());
        assertSame(view, second.drawable.getCallback());
        verify(first.drawable, times(1)).play();
        verify(second.drawable, times(1)).play();
        verify(first.drawable, never()).destroy();
        verify(second.drawable, never()).destroy();
    }

    @Test
    public void manuallyStoppedGifIsNotRestartedByVisibilityOrReattach() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);
        first.drawable.stop();
        second.drawable.stop();

        view.changeWindowVisibility(View.INVISIBLE);
        view.changeWindowVisibility(View.GONE);
        view.changeWindowVisibility(View.VISIBLE);
        root.removeView(view);
        root.addView(view, new FrameLayout.LayoutParams(160, 80));

        assertFalse(first.playing.get());
        assertFalse(second.playing.get());
        verify(first.drawable, never()).play();
        verify(second.drawable, never()).play();
    }

    @Test
    public void hiddenReplacementDoesNotInheritPreviousGifResumeIntent() {
        Playback previous = bindFirst(true);
        view.changeWindowVisibility(View.GONE);
        Playback replacement = bindFirst(false);

        view.changeWindowVisibility(View.VISIBLE);

        assertFalse(replacement.playing.get());
        verify(replacement.drawable, never()).play();
        verify(previous.drawable, times(1)).destroy();
    }

    @Test
    public void settingSameGifAgainDoesNotDestroyOrUnbindIt() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);

        view.setDrawable(first.drawable);
        view.setDrawable2(second.drawable);

        verify(first.drawable, never()).destroy();
        verify(second.drawable, never()).destroy();
        assertSame(view, first.drawable.getCallback());
        assertSame(view, second.drawable.getCallback());
    }

    @Test
    public void animationAndGifResumeIndependentlyOnVisibleWindow() {
        Playback first = bindFirst(true);
        Playback second = bindSecond(true);
        ReflectionHelpers.setField(view, "runnable", true);
        ReflectionHelpers.setField(view, "needRun", true);

        view.changeWindowVisibility(View.GONE);
        view.changeWindowVisibility(View.GONE);
        view.changeWindowVisibility(View.VISIBLE);

        assertTrue("Adjuster animation must also resume", view.animationRestarts == 1);
        assertTrue(first.playing.get());
        assertTrue(second.playing.get());
        verify(first.drawable, times(1)).play();
        verify(second.drawable, times(1)).play();
    }

    private Playback bindFirst(boolean playing) {
        Playback result = new Playback(playing);
        view.setDrawable(result.drawable);
        return result;
    }

    private Playback bindSecond(boolean playing) {
        Playback result = new Playback(playing);
        view.setDrawable2(result.drawable);
        return result;
    }

    private static void assertPaused(Playback first, Playback second) {
        assertFalse(first.playing.get());
        assertFalse(second.playing.get());
        verify(first.drawable, atLeastOnce()).stop();
        verify(second.drawable, atLeastOnce()).stop();
    }

    private static final class Playback {
        final AtomicBoolean playing;
        final GifDrawable drawable = mock(GifDrawable.class);

        Playback(boolean initiallyPlaying) {
            playing = new AtomicBoolean(initiallyPlaying);
            // Robolectric instruments Drawable methods; Mockito otherwise returns null
            // for getCallback even after the owner has called setCallback.
            doCallRealMethod().when(drawable).setCallback(any());
            when(drawable.getCallback()).thenCallRealMethod();
            when(drawable.isPlaying()).thenAnswer(invocation -> playing.get());
            doAnswer(invocation -> {
                playing.set(false);
                return null;
            }).when(drawable).stop();
            doAnswer(invocation -> {
                playing.set(true);
                return null;
            }).when(drawable).play();
        }
    }

    /** Window visibility is controlled; real attach and ancestor dispatch remain intact. */
    private static final class LifecycleSuperTextView extends SuperTextView {
        private int windowVisibility = View.VISIBLE;
        int animationRestarts;

        LifecycleSuperTextView(Context context) {
            super(context, Robolectric.buildAttributeSet().build());
        }

        @Override
        public int getWindowVisibility() {
            return windowVisibility;
        }

        void changeWindowVisibility(int visibility) {
            windowVisibility = visibility;
            onWindowVisibilityChanged(visibility);
        }

        @Override
        public void startAnim() {
            // Observe lifecycle recovery without starting SuperTextView's animation thread.
            animationRestarts++;
        }
    }
}
