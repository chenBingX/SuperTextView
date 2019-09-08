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
 * Last modified 9/2/19 7:46 PM
 */

package com.coorchice.library.gifdecoder;

import android.graphics.Bitmap;

/**
 * Project Name:CoorChiceLibOne
 * Author:CoorChice
 * Date:2019/8/29
 * Notes:
 */
public class JNI {

    static {
        System.loadLibrary("GifLib");
    }

    /**
     * 通过文件路径打开 gif 图
     *
     * @param path
     * @return
     */
    public static native long openFile(String path);

    /**
     * 通过byte数组打开 gif 图
     *
     * @param bytes
     * @return
     */
    public static native long openBytes(byte[] bytes);

    /**
     * 判断是否是gif
     * @param path
     * @return
     */
    public static native boolean fileIsGif(String path);

    /**
     * 判断是否是gif
     * @param bytes
     * @return
     */
    public static native boolean bytesIsGif(byte[] bytes);

    /**
     * 更新一帧
     *
     * @param ptr
     * @param bitmap
     * @return
     */
    public static native int updateFrame(long ptr, Bitmap bitmap);

    /**
     * 取得宽度
     *
     * @param ptr
     * @return
     */
    public static native int getWidth(long ptr);

    /**
     * 取得高度
     *
     * @param ptr
     * @return
     */
    public static native int getHeight(long ptr);

    /**
     * 获取帧数
     *
     * @param ptr
     * @return
     */
    public static native int getFrameCount(long ptr);

    /**
     * 获取当前帧间隔
     *
     * @param ptr
     * @return
     */
    public static native int getFrameDuration(long ptr);

    /**
     * 修改帧间隔
     *
     * @param ptr
     * @param duration
     */
    public static native void setFrameDuration(long ptr, int duration);


    /**
     * 获取当前帧位置
     *
     * @param ptr
     * @return
     */
    public static native int getCurrentFrame(long ptr);

    /**
     * 跳到指定帧
     *
     * @param ptr
     * @param frame
     */
    public static native void gotoFrame(long ptr, int frame, Bitmap bitmap);

    /**
     * 获得指定帧图像
     *
     * @param ptr
     * @param frame
     */
    public static native void getFrame(long ptr, int frame, Bitmap bitmap);

    public static native void setFrame(long ptr, int frame);

    /**
     * 启用/停止严格模式
     *
     * @param ptr
     * @param strict
     */
    public static native void setStrict(long ptr, boolean strict);


    /**
     * 是否启用严格模式
     *
     * @param ptr
     */
    public static native boolean getStrict(long ptr);

    /**
     * 销毁
     *
     * @param ptr
     * @return
     */
    public static native void destroy(long ptr);

}
