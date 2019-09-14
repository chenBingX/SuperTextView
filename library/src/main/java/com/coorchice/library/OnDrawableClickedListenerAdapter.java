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
 * Last modified 6/10/19 11:02 AM
 */

package com.coorchice.library;

/**
 * OnDrawableClickedListener 适配类，你可以选择性的重写 drawable1 或者 drawable2 的点击回调，
 * 而不必每次都需要实现两个接口。
 *
 * @author coorchice
 * @date 2019/06/10
 */
public class OnDrawableClickedListenerAdapter implements SuperTextView.OnDrawableClickedListener {

    /**
     * 当 Drawable1 被点击时触发
     * @param stv
     */
    @Override
    public void onDrawable1Clicked(SuperTextView stv){

    }

    /**
     * 当 Drawable2 被点击时触发
     * @param stv
     */
    @Override
    public void onDrawable2Clicked(SuperTextView stv){

    }
}
