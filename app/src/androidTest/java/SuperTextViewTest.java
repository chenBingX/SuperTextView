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
 * Last modified 18-9-2 下午1:22
 */

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.internal.util.LogUtil;
import android.support.test.runner.AndroidJUnit4;

import com.coorchice.library.SuperTextView;
import com.coorchice.supertextview.SuperTextView.Adjuster.MoveEffectAdjuster;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.TestCase.assertEquals;


/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2018/9/2
 * Notes:
 */
@RunWith(AndroidJUnit4.class)
public class SuperTextViewTest {


  @Test
  public void test_getAdjusterList() throws Exception {
    Context context = InstrumentationRegistry.getTargetContext();
    SuperTextView stv = new SuperTextView(context);
    stv.addAdjuster(new MoveEffectAdjuster());
    List<SuperTextView.Adjuster> adjusterList = stv.getAdjusterList();
    System.out.println(adjusterList.size());
  }
}
