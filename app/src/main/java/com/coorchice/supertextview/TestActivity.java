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
 * Last modified 1/21/19 3:31 PM
 */

package com.coorchice.supertextview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.coorchice.library.SuperTextView;
import com.coorchice.library.utils.STVUtils;

public class TestActivity extends ActionBarActivity {

    private SuperTextView stv_0;
    private SuperTextView stv_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setFinishOnTouchOutside(true);
        SuperTextView stv = (SuperTextView)findViewById(R.id.stv_1);
//        stv.setDrawable(new ColorDrawable(Color.parseColor("#4CBDD2")));
        init();

    }

    private void init() {
        findViews();
        stv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "点击右边可以关闭哦👉", Toast.LENGTH_SHORT).show();
//                PopupWindow popupWindow = new PopupWindow();
//                popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                SuperTextView contentView = new SuperTextView(TestActivity.this);
//                popupWindow.setContentView(contentView);
//                popupWindow.showAsDropDown(stv_0);
            }
        });
        stv_0.setOnDrawableClickedListener(new SuperTextView.OnDrawableClickedListener() {
            @Override
            public void onDrawable1Clicked(SuperTextView stv) {
                stv_0.setVisibility(View.GONE);
                stv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stv_0.setVisibility(View.VISIBLE);
                    }
                }, 2*1000);
            }

            @Override
            public void onDrawable2Clicked(SuperTextView stv) {

            }
        });
    }


    private void findViews() {
        stv_0 = (SuperTextView) findViewById(R.id.stv_0);
        stv_1 = (SuperTextView) findViewById(R.id.stv_1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
