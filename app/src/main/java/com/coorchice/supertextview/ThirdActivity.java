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
 * Last modified 18-5-13 上午10:05
 */

package com.coorchice.supertextview;

import com.coorchice.library.SuperTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ThirdActivity extends Activity {

  private SuperTextView stv_1;
  private SuperTextView stv_2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_third);
    stv_1 = (SuperTextView) findViewById(R.id.stv_1);
    stv_2 = (SuperTextView) findViewById(R.id.stv_2);


    String url_imag =
      "https://raw.githubusercontent.com/chenBingX/img/master/其它/u=2051975322,3785503293&fm=26&gp=0.jpg";
    String url_avatar =
        "https://raw.githubusercontent.com/chenBingX/img/master/其它/download.jpg";


    stv_1.setUrlImage(url_imag, false);
    stv_2.setUrlImage(url_avatar);

    stv_1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(ThirdActivity.this, TestActivity.class));
      }
    });
    stv_2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(ThirdActivity.this, ListActivity.class));
      }
    });
  }

}
