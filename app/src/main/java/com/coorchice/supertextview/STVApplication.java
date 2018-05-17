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
 * Last modified 18-5-13 上午11:26
 */

package com.coorchice.supertextview;

import android.app.Application;

import com.coorchice.library.ImageEngine;
import com.coorchice.supertextview.SuperTextView.ImageEngine.GlideEngine;
import com.coorchice.supertextview.SuperTextView.ImageEngine.PicassoEngine;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2018/5/13
 * Notes:
 */

public class STVApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
//    ImageEngine.install(new GlideEngine(this));
//    ImageEngine.install(new PicassoEngine(this));
  }
}
