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
 * Last modified 9/3/19 11:48 AM
 */

package com.coorchice.library.gifdecoder;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

/**
 * @author coorchice
 * @date 2019/09/03
 */
public interface Gif {

    /**
     * 获取 Gif 的宽
     * @return
     */
    int getWidth();

    /**
     * 获取 Gif 的高
     * @return
     */
    int getHeight();

    /**
     * 获取 Gif 总帧数
     * @return 返回 Gif 总帧数
     */
    int getFrameCount();

    /**
     * 获取 Gif 当前帧间隔，单位毫秒（ms）
     * @return
     */
    int getFrameDuration();

    /**
     * 设置 Gif 帧间隔
     *
     * @param duration 帧间隔，单位毫秒（ms）
     */
    void setFrameDuration(int duration);

    /**
     * 获得 Gif 当前帧位置
     *
     * @return
     */
    int getCurrentFrame();

    /**
     * 跳转到 Gif 指定帧。
     *
     * 指定帧取值范围为 [0, 帧总数) 之间。
     *
     * @param frame 指定帧位置。
     */
    void gotoFrame(int frame);

    /**
     * 提取 Gif 指定帧图像。
     *
     * 指定帧取值范围为 [0, 帧总数) 之间。
     *
     * @param frame 指定帧位置。
     */
    Bitmap getFrame(int frame);

    int updateFrame();

    /**
     * 获取 Gif 底层指针地址
     * @return
     */
    long getPtr();

    /**
     * 获取用于渲染 Gif 的 Bitmap 内存
     * @return
     */
    Bitmap getBitmap();

    /**
     * 播放
     */
    void play();

    /**
     * 是否在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 暂停播放
     */
    void stop();

    /**
     * 是否被销毁
     * @return
     */
    boolean isDestroy();

    /**
     * 销毁。
     *
     * 你不能使用一个以及被销毁的 Gif。
     */
    void destroy();

    /**
     * 设置帧绘制监听器。
     * 当一帧将要被绘制时，会进行回调。
     *
     * @param onFrameListener
     */
    void setOnFrameListener(GifDecoder.OnFrameListener onFrameListener);

    /**
     * 设置是否启用严格模式。
     * 严格模式在支持局部更新的 Gif 图中十分有用，能够避免 {@link Gif#gotoFrame(int)} 和 {@link Gif#getFrame(int)} 产生错误的结果。
     * 启用严格模式会带来一些性能损耗！
     *
     * @param strict true - 启用严格模式；false - 关闭严格模式。
     */
    void setStrict(boolean strict);

    /**
     * 是否启用了严格模式
     * @return
     */
    boolean isStrict();
}
