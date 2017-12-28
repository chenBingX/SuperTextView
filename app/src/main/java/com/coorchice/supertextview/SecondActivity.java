/*
 * Copyright (C) 2017 CoorChice <icechen_@outlook.com>
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
 * Last modified 17-8-2 上午11:08
 */

package com.coorchice.supertextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.coorchice.library.SuperTextView;
import com.coorchice.supertextview.SuperTextView.Adjuster.MoveEffectAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.Ripple2Adjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.RippleAdjuster;
import com.coorchice.supertextview.Utils.LogUtils;

public class SecondActivity extends AppCompatActivity {

  private SuperTextView btn;
  private SuperTextView stv_2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_second);
    btn = (SuperTextView) findViewById(R.id.btn);

//    btn.setAutoAdjust(true);
    btn.setAdjuster(new RippleAdjuster(getResources().getColor(R.color.opacity_5_a58fed)));

    btn.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        LogUtils.e("onTouch");
        return false;
      }
    });

    btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        LogUtils.e("onClick");
      }
    });

    stv_2 = (SuperTextView) findViewById(R.id.stv_2);
    stv_2.addAdjuster(new MoveEffectAdjuster().setOpportunity(SuperTextView.Adjuster.Opportunity.BEFORE_TEXT));
    stv_2.addAdjuster(new Ripple2Adjuster(getResources().getColor(R.color.opacity_9_a58fed)));
    stv_2.setAutoAdjust(true).startAnim();
  }
}
