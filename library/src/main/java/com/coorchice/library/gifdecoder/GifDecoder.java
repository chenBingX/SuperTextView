/*
 * Copyright (C) 2019 CoorChice <icechen_@outlook.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * <p>
 * Last modified 9/2/19 7:53 PM
 */

package com.coorchice.library.gifdecoder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.coorchice.library.utils.LogUtils;
import com.coorchice.library.utils.STVUtils;
import com.coorchice.library.utils.ThreadPool;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Project Name:CoorChiceLibOne
 * Author:CoorChice
 * Date:2019/8/30
 * Notes:
 */
public class GifDecoder implements Gif {

    private long ptr;
    private Bitmap frame, buffer;
    private Canvas bufferCanvas;
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
    private Rect bounds;
    private boolean canPlay = false;
    private OnFrameListener onFrameListener;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ScheduledFuture<?> onceRenderSchedule;
    private ScheduledFuture<?> gotoFrameSchedule;
    protected final Object lock = new Object();
    private Runnable onFrameRunnable = new Runnable() {
        @Override
        public void run() {
            if (onFrameListener != null && !isDestroy() && buffer != null) {
                onFrameListener.onFrame(GifDecoder.this, buffer);
            }
        }
    };
    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            if (isDestroy() || !canPlay) {
                handler.removeCallbacksAndMessages(null);
                ThreadPool.globleExecutor().remove(playRunnable);
                if (onceRenderSchedule != null) {
                    onceRenderSchedule.cancel(false);
                }
                return;
            }
            int d = updateFrame();
            LogUtils.e("当前帧 = " + getCurrentFrame());
            LogUtils.e("当前帧间隔 = " + getFrameDuration());
            LogUtils.e("native本帧剩余时间 = " + d);
            handler.postAtTime(onFrameRunnable, SystemClock.uptimeMillis() + d);
            innerPlay(d);
        }
    };
    private Runnable gotoFrameRunnable;

    public static GifDecoder openFile(String filePtah) {
        return new GifDecoder(filePtah);
    }

    public static GifDecoder openBytes(byte[] bytes) {
        return new GifDecoder(bytes);
    }

    private GifDecoder(String filePtah) {
        if (!TextUtils.isEmpty(filePtah)) {
            ptr = JNI.openFile(filePtah);
        } else {
            throw new IllegalArgumentException("File path can not be null or empty!");
        }
        init();
    }

    private GifDecoder(byte[] bytes) {
        if (bytes != null) {
            ptr = JNI.openBytes(bytes);
        } else {
            throw new IllegalArgumentException("File path can not be null or empty!");
        }
        init();
    }

    private void init() {
        if (ptr == 0) {
            throw new NullPointerException("Init Failure！");
        }
        frame = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bufferCanvas = new Canvas(buffer = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888));
    }

    /**
     * 获取 Gif 的宽
     * @return
     */
    public int getWidth() {
        check();
        return JNI.getWidth(ptr);
    }

    /**
     * 获取 Gif 的高
     * @return
     */
    public int getHeight() {
        check();
        return JNI.getHeight(ptr);
    }

    /**
     * 获取 Gif 总帧数
     * @return 返回 Gif 总帧数
     */
    public int getFrameCount() {
        check();
        return JNI.getFrameCount(ptr);
    }

    /**
     * 获取 Gif 当前帧间隔，单位毫秒（ms）
     * @return
     */
    public int getFrameDuration() {
        check();
        return JNI.getFrameDuration(ptr);
    }

    /**
     * 设置 Gif 帧间隔
     *
     * @param duration 帧间隔，单位毫秒（ms）
     */
    public void setFrameDuration(int duration) {
        check();
        JNI.setFrameDuration(ptr, duration);
    }

    /**
     * 获得 Gif 当前帧位置
     *
     * @return
     */
    public int getCurrentFrame() {
        check();
        return JNI.getCurrentFrame(ptr);
    }

    /**
     * 跳转到 Gif 指定帧。
     *
     * 指定帧取值范围为 [0, 帧总数) 之间。
     *
     * @param frame 指定帧位置。
     */
    public void gotoFrame(final int frame) {
        check();
        if (canPlay) {
            synchronized (lock) {
                JNI.gotoFrame(ptr, frame, GifDecoder.this.frame);
            }
        } else {
            if (gotoFrameRunnable != null) {
                ThreadPool.globleExecutor().remove(gotoFrameRunnable);
            }
            if (gotoFrameSchedule != null) {
                gotoFrameSchedule.cancel(true);
            }
            gotoFrameSchedule = ThreadPool.globleExecutor().schedule(gotoFrameRunnable = new Runnable() {
                @Override
                public void run() {
                    JNI.gotoFrame(ptr, frame, GifDecoder.this.frame);
                    copyFrameToBuffer();
                    handler.postAtTime(onFrameRunnable, SystemClock.uptimeMillis());
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 提取 Gif 指定帧图像。
     *
     * 指定帧取值范围为 [0, 帧总数) 之间。
     *
     * @param frame 指定帧位置。
     */
    public Bitmap getFrame(int frame) {
        check();
        Bitmap tempFrame = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        JNI.getFrame(ptr, frame, tempFrame);
        return tempFrame;
    }

    /**
     * 设置是否启用严格模式。
     * 严格模式在支持局部更新的 Gif 图中十分有用，能够避免 {@link Gif#gotoFrame(int)} 和 {@link Gif#getFrame(int)} 产生错误的结果。
     * 启用严格模式会带来一些性能损耗！
     *
     * @param strict true - 启用严格模式；false - 关闭严格模式。
     */
    @Override
    public void setStrict(boolean strict) {
        check();
        JNI.setStrict(ptr, strict);
    }

    /**
     * 是否启用了严格模式
     * @return
     */
    @Override
    public boolean isStrict() {
        check();
        return JNI.getStrict(ptr);
    }

    /**
     * 渲染一帧
     * @return
     */
    public int updateFrame() {
        check();
        int r = 1;
        if (frame != null) {
            r = JNI.updateFrame(ptr, frame);
            copyFrameToBuffer();
        }
        return r;
    }

    /**
     * 获取 Gif 底层指针地址
     * @return
     */
    public long getPtr() {
        return ptr;
    }

    /**
     * 获取用于渲染 Gif 的 Bitmap 内存
     * @return
     */
    public Bitmap getBitmap() {
        return buffer;
    }

    private void check() {
        if (ptr == 0) {
            try {
                throw new IllegalStateException("GifDecoder has not been created or destroyed!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放
     */
    public void play() {
        if (isDestroy()) {
            canPlay = false;
            handler.removeCallbacksAndMessages(null);
            ThreadPool.globleExecutor().remove(playRunnable);
            if (onceRenderSchedule != null) {
                onceRenderSchedule.cancel(false);
            }
            return;
        }
        if (!canPlay) {
            canPlay = true;
            handler.removeCallbacksAndMessages(null);
            if (onceRenderSchedule != null) {
                onceRenderSchedule.cancel(false);
            }
            innerPlay(0);
        }
    }

    private void innerPlay(int delay) {
        ThreadPool.globleExecutor().remove(playRunnable);
        onceRenderSchedule = ThreadPool.globleExecutor().schedule(playRunnable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 是否在播放
     * @return
     */
    public boolean isPlaying() {
        return canPlay;
    }

    /**
     * 暂停播放
     */
    public void stop() {
        canPlay = false;
        handler.removeCallbacksAndMessages(null);
        ThreadPool.globleExecutor().remove(playRunnable);
        if (onceRenderSchedule != null) {
            onceRenderSchedule.cancel(false);
        }
    }

    /**
     * 是否被销毁
     * @return
     */
    public boolean isDestroy() {
        return ptr == 0;
    }

    /**
     * 销毁。
     *
     * 你不能使用一个以及被销毁的 Gif。
     */
    public void destroy() {
        canPlay = false;
        handler.removeCallbacksAndMessages(null);
        ThreadPool.globleExecutor().remove(playRunnable);
        if (onceRenderSchedule != null) {
            onceRenderSchedule.cancel(false);
        }
        check();
        JNI.destroy(ptr);
        ptr = 0;
        frame.recycle();
        frame = null;
        bufferCanvas = null;
        buffer.recycle();
        buffer = null;
    }

    /**
     * 设置帧绘制监听器。
     * 当一帧将要被绘制时，会进行回调。
     *
     * @param onFrameListener
     */
    public void setOnFrameListener(OnFrameListener onFrameListener) {
        this.onFrameListener = onFrameListener;
    }

    public Rect getBounds() {
        if (bounds == null || bounds.isEmpty()) {
            if (!isDestroy() && frame != null) {
                bounds = new Rect(0, 0, getWidth(), getHeight());
            } else {
                bounds = new Rect(0, 0, 1, 1);
            }
        }
        return bounds;
    }

    private void copyFrameToBuffer() {
        synchronized (lock) {
            if (buffer != null && bufferCanvas != null && frame != null) {
                bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                bufferCanvas.drawBitmap(frame, 0, 0, paint);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (!isDestroy()) {
            destroy();
        }
    }

    public static interface OnFrameListener {
        void onFrame(GifDecoder gd, Bitmap bitmap);
    }

    /**
     * 判断是否是 Gif
     *
     * @param o
     * @return
     */
    public static boolean isGif(Object o) {
        return STVUtils.isGif(o);
    }
}
