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
 * Last modified 17-11-14 上午12:02
 */

package com.coorchice.library.sys_adjusters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.coorchice.library.SuperTextView;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2017/8/2
 * Notes:
 */

/**
 * @hide
 */
public class PressAdjuster extends SuperTextView.Adjuster {

  private int pressBgColor = Color.TRANSPARENT;
  private int pressTextColor = -99;
  private int normalTextColor = -99;
  private boolean press = false;
  private Path solidPath;
  private RectF solidRectF;
  private Paint paint;

  public PressAdjuster(int pressBgColor) {
    this.pressBgColor = pressBgColor;
    setOpportunity(Opportunity.BEFORE_DRAWABLE);
    initPaint();
  }

  private void initPaint() {
    if (paint == null) {
      paint = new Paint();
    }
    paint.reset();
    paint.setAntiAlias(true);
    paint.setDither(true);
  }

  public SuperTextView.Adjuster setPressTextColor(int pressTextColor) {
    this.pressTextColor = pressTextColor;
    return this;
  }

  public SuperTextView.Adjuster setPressBgColor(int pressBgColor) {
    this.pressBgColor = pressBgColor;
    return this;
  }



  @Override
  public void adjust(SuperTextView v, Canvas canvas) {
    if (press) {
      if (solidPath == null) {
        solidPath = new Path();
      } else {
        solidPath.reset();
      }
      if (solidRectF == null) {
        solidRectF = new RectF();
      } else {
        solidRectF.setEmpty();
      }
      float strokeWidth = v.getStrokeWidth();
      solidRectF.set(strokeWidth, strokeWidth, v.getWidth() - strokeWidth,
          v.getHeight() - strokeWidth);
      solidPath.addRoundRect(solidRectF, v.getCorners(), Path.Direction.CW);
      paint.setStyle(Paint.Style.FILL);
      paint.setColor(pressBgColor);
      canvas.drawPath(solidPath, paint);
    }
  }


  @Override
  public boolean onTouch(SuperTextView v, MotionEvent event) {
    int action = event.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        if (normalTextColor == -99) {
          normalTextColor = v.getCurrentTextColor();
        }
        if (pressTextColor != -99 && v.getCurrentTextColor() != pressTextColor) {
          v.setTextColor(pressTextColor);
        }
        press = true;
        v.postInvalidate();
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        if (normalTextColor != -99 && v.getCurrentTextColor() != normalTextColor) {
          v.setTextColor(normalTextColor);
        }
        press = false;
        v.postInvalidate();
        break;
    }
    return true;
  }
}
