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

    int getWidth();

    int getHeight();

    int getFrameCount();

    int getFrameDuration();

    void setFrameDuration(int duration);

    int getCurrentFrame();

    void gotoFrame(int frame);

    Bitmap getFrame(int frame);

    int updateFrame();

    long getPtr();

    Bitmap getBitmap();

    void play();

    boolean isPlaying();

    void stop();

    boolean isDestroy();

    void destroy();

    void setOnFrameListener(GifDecoder.OnFrameListener onFrameListener);

    void setStrict(boolean strict);

    boolean isStrict();
}
