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
 * Last modified 18-5-13 上午11:14
 */

package com.coorchice.library;

import com.coorchice.library.image_engine.DefaultEngine;
import com.coorchice.library.image_engine.Engine;

import android.app.Application;
import android.graphics.drawable.Drawable;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2018/5/13
 * Notes:
 */

/**
 * SuperTextView的图片加载引擎
 */
public class ImageEngine {

  private Engine engine;

  private ImageEngine() {

  }

  private static final class Holder {
    private static final ImageEngine instance = new ImageEngine();
  }

  /**
   * 必须先安装一个引擎。后安装的Engine总是会替换前面的。
   * 默认情况下，SuperTextView有一个十分简易的Engine，建议开发者根据项目所使用的图片框架实现一个{@link Engine}。
   * 建议开发者在{@link Application#onCreate()}中进行配置。
   * @param engine 图片加载引擎
   */
  public static void install(Engine engine) {
    synchronized (Holder.instance) {
      Holder.instance.engine = engine;
    }
  }

  /**
   * @hide
   * @param url
   * @param callback
   */
  static void load(String url, Callback callback) {
    if (Holder.instance.engine == null)
      throw new IllegalStateException("You must first install one engine!");
    Holder.instance.engine.load(url, callback);
  }

  /**
   * @hide
   */
  static void checkEngine() {
    if (Holder.instance.engine == null) {
      Holder.instance.engine = new DefaultEngine();
    }
  }

  public static interface Callback {
    void onCompleted(Drawable drawable);
  }

}
