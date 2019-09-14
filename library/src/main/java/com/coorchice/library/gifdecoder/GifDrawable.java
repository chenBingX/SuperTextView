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
 * Last modified 9/3/19 9:42 AM
 */

package com.coorchice.library.gifdecoder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * @author coorchice
 * @date 2019/09/03
 */
public class GifDrawable extends Drawable implements Gif {

    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
    private GifDecoder gifDecoder;
    private Bitmap frame;
    private GifDecoder.OnFrameListener onFrameListener;

    private GifDrawable(GifDecoder gifDecoder) {
        this.gifDecoder = gifDecoder;
        setBounds(0, 0, gifDecoder.getWidth(), gifDecoder.getHeight());
        gifDecoder.setOnFrameListener(new GifDecoder.OnFrameListener() {
            @Override
            public void onFrame(GifDecoder gd, Bitmap bitmap) {
                if (onFrameListener != null){
                    onFrameListener.onFrame(gd, bitmap);
                }
                frame = bitmap;
                invalidateSelf();
            }
        });
        play();
    }

    /**
     * 使用文件绝对路径创建一个 GifDrawable
     *
     * @param filePath 文件绝对路径
     * @return
     */
    public static GifDrawable createDrawable(String filePath) {
        return new GifDrawable(GifDecoder.openFile(filePath));
    }

    /**
     * 使用二进制数组创建一个 GifDrawable
     *
     * @param bytes 二进制数组
     * @return
     */
    public static GifDrawable createDrawable(byte[] bytes) {
        return new GifDrawable(GifDecoder.openBytes(bytes));
    }

    @Override
    public void draw(Canvas canvas) {
        if (gifDecoder == null || gifDecoder.isDestroy()) return;
        synchronized (gifDecoder.lock) {
            if (frame != null) {
                canvas.drawBitmap(frame, gifDecoder.getBounds(), getBounds(), paint);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        if (paint.getAlpha() < 255) {
            return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.OPAQUE;
    }

    private boolean isValid() {
        return gifDecoder != null && !gifDecoder.isDestroy();
    }

    /**
     * 获取 Gif 的宽
     * @return
     */
    @Override
    public int getWidth() {
        if (isValid()) {
            return gifDecoder.getWidth();
        }
        return 0;
    }

    @Override
    public int getIntrinsicWidth() {
        return getBounds() == null? getWidth() : getBounds().width();
    }

    /**
     * 获取 Gif 的高
     * @return
     */
    @Override
    public int getHeight() {
        if (isValid()) {
            return gifDecoder.getHeight();
        }
        return 0;
    }

    @Override
    public int getIntrinsicHeight() {
        return getBounds() == null ? getHeight() : getBounds().height();
    }

    /**
     * 获取 Gif 总帧数
     * @return 返回 Gif 总帧数
     */
    @Override
    public int getFrameCount() {
        if (isValid()) {
            return gifDecoder.getFrameCount();
        }
        return 0;
    }

    /**
     * 获取 Gif 当前帧间隔，单位毫秒（ms）
     * @return
     */
    @Override
    public int getFrameDuration() {
        if (isValid()) {
            return gifDecoder.getFrameDuration();
        }
        return 0;
    }

    /**
     * 设置 Gif 帧间隔
     *
     * @param duration 帧间隔，单位毫秒（ms）
     */
    @Override
    public void setFrameDuration(int duration) {
        if (isValid()) {
            gifDecoder.setFrameDuration(duration);
        }
    }

    /**
     * 获得 Gif 当前帧位置
     *
     * @return
     */
    @Override
    public int getCurrentFrame() {
        if (isValid()) {
            return gifDecoder.getCurrentFrame();
        }
        return 0;
    }

    /**
     * 跳转到 Gif 指定帧。
     *
     * 指定帧取值范围为 [0, 帧总数) 之间。
     *
     * @param frame 指定帧位置。
     */
    @Override
    public void gotoFrame(int frame) {
        if (isValid()) {
            gifDecoder.gotoFrame(frame);
        }
    }

    /**
     * 提取 Gif 指定帧图像。
     *
     * 指定帧取值范围为 [0, 帧总数) 之间。
     *
     * @param frame 指定帧位置。
     */
    @Override
    public Bitmap getFrame(int frame) {
        if (isValid()) {
            return gifDecoder.getFrame(frame);
        }
        return null;
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
        if (isValid()) {
            gifDecoder.setStrict(strict);
        }
    }

    /**
     * 是否启用了严格模式
     * @return
     */
    @Override
    public boolean isStrict() {
        if (isValid()) {
            return gifDecoder.isStrict();
        }
        return false;
    }

    @Override
    public int updateFrame() {
        if (isValid()) {
            return gifDecoder.updateFrame();
        }
        return 0;
    }

    /**
     * 获取 Gif 底层指针地址
     * @return
     */
    @Override
    public long getPtr() {
        if (isValid()) {
            return gifDecoder.getPtr();
        }
        return 0;
    }

    /**
     * 获取用于渲染 Gif 的 Bitmap 内存
     * @return
     */
    @Override
    public Bitmap getBitmap() {
        if (isValid()) {
            return gifDecoder.getBitmap();
        }
        return null;
    }

    /**
     * 播放
     */
    @Override
    public void play() {
        if (isValid()) {
            gifDecoder.play();
        }
    }

    /**
     * 是否在播放
     * @return
     */
    @Override
    public boolean isPlaying() {
        if (isValid()) {
            return gifDecoder.isPlaying();
        }
        return false;
    }

    /**
     * 暂停播放
     */
    @Override
    public void stop() {
        if (isValid()) {
            gifDecoder.stop();
        }
    }

    /**
     * 是否被销毁
     * @return
     */
    @Override
    public boolean isDestroy() {
        if (isValid()) {
            return gifDecoder.isDestroy();
        }
        return false;
    }

    /**
     * 销毁。
     *
     * 你不能使用一个以及被销毁的 Gif。
     */
    @Override
    public void destroy() {
        if (isValid()) {
            gifDecoder.destroy();
        }
    }

    /**
     * 设置帧绘制监听器。
     * 当一帧将要被绘制时，会进行回调。
     *
     * @param onFrameListener
     */
    @Override
    public void setOnFrameListener(GifDecoder.OnFrameListener onFrameListener) {
        this.onFrameListener = onFrameListener;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroy();
    }
}
