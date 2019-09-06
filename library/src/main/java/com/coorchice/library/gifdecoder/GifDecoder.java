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
//            LogUtils.e("当前帧间隔 = " + getFrameDuration());
//            LogUtils.e("native本帧剩余时间 = " + d);
            handler.postAtTime(onFrameRunnable, SystemClock.uptimeMillis() + d);
            innerPlay(d);
        }
    };

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

    public int getWidth() {
        check();
        return JNI.getWidth(ptr);
    }

    public int getHeight() {
        check();
        return JNI.getHeight(ptr);
    }

    public int getFrameCount() {
        check();
        return JNI.getFrameCount(ptr);
    }

    public int getFrameDuration() {
        check();
        return JNI.getFrameDuration(ptr);
    }

    public void setFrameDuration(int duration) {
        check();
        JNI.setFrameDuration(ptr, duration);
    }

    public int getCurrentFrame() {
        check();
        return JNI.getCurrentFrame(ptr);
    }


    public void gotoFrame(int frame) {
        check();
        JNI.gotoFrame(ptr, frame);
    }

    public Bitmap getFrame(int frame) {
        check();
        JNI.getFrame(ptr, frame, this.frame);
        copyFrameToBuffer();
        return buffer;
    }

    public int updateFrame() {
        check();
        int r = 1;
        if (frame != null) {
//            long startTime0 = System.currentTimeMillis();
            r = JNI.updateFrame(ptr, frame);
//            LogUtils.e("native绘制函数耗时 = " + (System.currentTimeMillis() - startTime0));
//            long startTime = System.currentTimeMillis();
            copyFrameToBuffer();
//            LogUtils.e("拷贝函数耗时 = " + (System.currentTimeMillis() - startTime));
        }
        return r;
    }

    public long getPtr() {
        return ptr;
    }

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

    public boolean isPlaying() {
        return canPlay;
    }

    public void stop() {
        canPlay = false;
        handler.removeCallbacksAndMessages(null);
        ThreadPool.globleExecutor().remove(playRunnable);
        if (onceRenderSchedule != null) {
            onceRenderSchedule.cancel(false);
        }
    }

    public boolean isDestroy() {
        return ptr == 0;
    }

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
//                long startTime = System.currentTimeMillis();
                bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                bufferCanvas.drawBitmap(frame, 0, 0, paint);
//                LogUtils.e("GifDecoder -> 拷贝视图 end = " + (System.currentTimeMillis() - startTime));
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

    public static boolean isGif(Object o) {
        return STVUtils.isGif(o);
    }
}
