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
 * Last modified 18-5-13 上午10:51
 */

package com.coorchice.library.image_engine;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.coorchice.library.ImageEngine;


/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2018/5/13
 * Notes:
 */

/**
 * 用于下载图片的引擎，通过{@link ImageEngine#install(Engine)}安装到SuperTextView，开发者可以在
 * {@link Application#onCreate()}中进行配置（需要注意任何时候新安装的引擎总会替换掉原本的引擎）。
 */
public interface Engine {
  /**
   * 开发者需要实现这个方法，在其中处理下载图片的逻辑。
   * 最后，需要通过{@link ImageEngine.Callback#onCompleted(Drawable)}返回一个Drawable给SuperTextView
   *
   * @param url 网络图片地址
   * @param callback 回调对象，需要通过{@link ImageEngine.Callback#onCompleted(Drawable)}返回一个Drawable给SuperTextView
   */
  void load(String url, ImageEngine.Callback callback);
}
