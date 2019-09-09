/*
 * Copyright (C) 2018 CoorChice <icechen_@outlook.com>
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
 * Last modified 18-5-13 上午9:39
 */

package com.coorchice.library.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Looper;

import com.coorchice.library.gifdecoder.JNI;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2018/5/13
 * Notes:
 */

/**
 * @hide
 */
public class STVUtils {

    /**
     * 把Drawable转换成一个Bitmap
     *
     * @param drawable      drawable对象
     * @param defaultWidth  默认width，如果没有固有width
     * @param defaultHeight 默认Height，如果没有固有Height
     * @return
     */
    public static final Bitmap drawableToBitmap(Drawable drawable, int defaultWidth, int defaultHeight) {
        int intrinsicWidth = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : defaultWidth;
        int intrinsicHeight = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : defaultHeight;
        Bitmap bitmap = Bitmap.createBitmap(
                intrinsicWidth,
                intrinsicHeight,
                drawable.getOpacity() != PixelFormat.OPAQUE
                        ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 判断是否是 Gif。支持判断文件url路径字符串，文件byte数组数据，其它情况返回false。
     *
     * @param o
     * @return
     */
    public static boolean isGif(Object o) {
        boolean r = false;
        try {
            if (o instanceof String) {
                r = ((String) o).toUpperCase().endsWith(".gif".toUpperCase());
            } else if (o instanceof byte[]) {
                r = JNI.bytesIsGif((byte[]) o);
            }
        } catch (Exception ignored) {

        }
        return r;
    }


    /**
     * 字符串转 MD5
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] hash = md.digest();
            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }

    /**
     * 获取指定 Drawable 资源的 byte 数组
     *
     * @param context
     * @param drawableRes
     * @return
     */
    public static byte[] getResBytes(Context context, int drawableRes) {
        if (context == null || drawableRes == 0) return null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(drawableRes);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
