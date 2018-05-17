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
 * Last modified 18-5-13 上午11:23
 */

package com.coorchice.supertextview.SuperTextView.ImageEngine;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.coorchice.library.ImageEngine;
import com.coorchice.library.image_engine.Engine;

import android.content.Context;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2018/5/13
 * Notes:
 */

public class GlideEngine implements Engine {

  private Context context;

  public GlideEngine(Context context) {
    this.context = context;
  }

  @Override
  public void load(String url, final ImageEngine.Callback callback) {
    Glide.with(context).load(url).into(new SimpleTarget<GlideDrawable>() {
      @Override
      public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
        callback.onCompleted(resource);
      }
    });
  }
}
