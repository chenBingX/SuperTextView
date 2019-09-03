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

    private GifDrawable(GifDecoder gifDecoder) {
        this.gifDecoder = gifDecoder;
        setBounds(0, 0, gifDecoder.getWidth(), gifDecoder.getHeight());
        gifDecoder.setOnFrameListener(new GifDecoder.OnFrameListener() {
            @Override
            public void onFrame(GifDecoder gd, Bitmap bitmap) {
                invalidateSelf();
            }
        });
        play();
    }

    public static GifDrawable createDrawable(String filePath) {
        return new GifDrawable(GifDecoder.openFile(filePath));
    }

    public static GifDrawable createDrawable(byte[] bytes) {
        return new GifDrawable(GifDecoder.openBytes(bytes));
    }

    @Override
    public void draw(Canvas canvas) {
        if (gifDecoder == null || gifDecoder.isDestroy()) return;
        if (gifDecoder.getBitmap() != null) {
//            synchronized (gifDecoder.getBitmap()) {
                canvas.drawBitmap(gifDecoder.getBitmap(), gifDecoder.getBounds(), getBounds(), paint);
//            }
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

    @Override
    public int getWidth() {
        if (isValid()) {
            return gifDecoder.getWidth();
        }
        return 0;
    }

    @Override
    public int getHeight() {
        if (isValid()) {
            return gifDecoder.getHeight();
        }
        return 0;
    }

    @Override
    public int getFrameCount() {
        if (isValid()) {
            return gifDecoder.getFrameCount();
        }
        return 0;
    }

    @Override
    public int getFrameDuration() {
        if (isValid()) {
            return gifDecoder.getFrameDuration();
        }
        return 0;
    }

    @Override
    public void setFrameDuration(int duration) {
        if (isValid()) {
            gifDecoder.setFrameDuration(duration);
        }
    }

    @Override
    public int getCurrentFrame() {
        if (isValid()) {
            return gifDecoder.getCurrentFrame();
        }
        return 0;
    }

    @Override
    public void gotoFrame(int frame) {
        if (isValid()) {
            gifDecoder.gotoFrame(frame);
        }
    }

    @Override
    public Bitmap getFrame(int frame) {
        if (isValid()) {
            return gifDecoder.getFrame(frame);
        }
        return null;
    }

    @Override
    public int updateFrame() {
        if (isValid()) {
            return gifDecoder.updateFrame();
        }
        return 0;
    }

    @Override
    public long getPtr() {
        if (isValid()) {
            return gifDecoder.getPtr();
        }
        return 0;
    }

    @Override
    public Bitmap getBitmap() {
        if (isValid()) {
            return gifDecoder.getBitmap();
        }
        return null;
    }

    @Override
    public void play() {
        if (isValid()) {
            gifDecoder.play();
        }
    }

    @Override
    public boolean isPlaying() {
        if (isValid()) {
            return gifDecoder.isPlaying();
        }
        return false;
    }

    @Override
    public void stop() {
        if (isValid()) {
            gifDecoder.stop();
        }
    }

    @Override
    public boolean isDestroy() {
        if (isValid()) {
            return gifDecoder.isDestroy();
        }
        return false;
    }

    @Override
    public void destroy() {
        if (isValid()) {
            gifDecoder.destroy();
        }
    }

    @Override
    public void setOnFrameListener(GifDecoder.OnFrameListener onFrameListener) {
        if (isValid()) {
            gifDecoder.setOnFrameListener(onFrameListener);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroy();
    }
}
