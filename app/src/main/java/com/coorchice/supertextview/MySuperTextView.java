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
 * Last modified 3/19/19 9:18 PM
 */

package com.coorchice.supertextview;

import android.content.Context;
import android.util.AttributeSet;

import com.coorchice.library.ImageEngine;
import com.coorchice.library.SuperTextView;

/**
 * @author coorchice
 * @date 2019/03/19
 */
public class MySuperTextView extends SuperTextView {


    public MySuperTextView(Context context) {
        super(context);
    }

    public MySuperTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SuperTextView setUrlImage(String url, boolean asBackground) {
        return super.setUrlImage(url, asBackground);
    }
}
