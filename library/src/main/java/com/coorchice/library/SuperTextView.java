

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
 * Last modified 17-4-20 下午5:32
 */

package com.coorchice.library;


import java.util.ArrayList;
import java.util.List;

import com.coorchice.library.sys_adjusters.PressAdjuster;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;


public class SuperTextView extends TextView {

  // Some Property Default Value
  private static final float DEFAULT_CORNER = 0f;
  private static final int DEFAULT_SOLID = Color.TRANSPARENT;
  private static final float DEFAULT_STROKE_WIDTH = 0f;
  private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
  private static final int DEFAULT_STATE_DRAWABLE_MODE = 4;
  private static final int DEFAULT_TEXT_STROKE_COLOR = Color.BLACK;
  private static final int DEFAULT_TEXT_FILL_COLOR = Color.BLACK;
  private static final float DEFAULT_TEXT_STROKE_WIDTH = 0f;
  private static final int ALLOW_CUSTOM_ADJUSTER_SIZE = 3;
  private static int SYSTEM_ADJUSTER_SIZE = 0;

  private float corner;
  private boolean leftTopCornerEnable;
  private boolean rightTopCornerEnable;
  private boolean leftBottomCornerEnable;
  private boolean rightBottomCornerEnable;
  private int solid;
  private float strokeWidth;
  private int strokeColor;
  private int stateDrawableMode;
  private int stateDrawable2Mode;
  private boolean isShowState;
  private boolean isShowState2;

  private Paint paint;
  private int width;
  private int height;
  private Drawable drawable;
  private Drawable drawable2;
  private float density;
  private boolean autoAdjust;
  // private Adjuster adjuster;
  private Adjuster pressAdjuster;
  private boolean textStroke;
  private int textStrokeColor;
  private int textFillColor;
  private float textStrokeWidth;
  private boolean runnable = false;
  private boolean needRun = false;
  private Thread animThread;
  private Path strokeWidthPath;
  private Path solidPath;
  private RectF strokeLineRectF;
  private RectF solidRectF;
  private float leftTopCorner[] = new float[2];
  private float rightTopCorner[] = new float[2];
  private float leftBottomCorner[] = new float[2];
  private float rightBottomCorner[] = new float[2];
  private float corners[] = new float[8];
  private float[] drawableBounds = new float[4];
  private float drawableWidth;
  private float drawableHeight;
  private float drawablePaddingLeft;
  private float drawablePaddingTop;
  private float[] drawable2Bounds = new float[4];
  private float drawable2Width;
  private float drawable2Height;
  private float drawable2PaddingLeft;
  private float drawable2PaddingTop;
  private boolean cacheRunnableState;
  private boolean cacheNeedRunState;
  private int frameRate = 60;
  private Runnable invalidate;
  private int shaderStartColor;
  private int shaderEndColor;
  private int shaderMode;
  private LinearGradient shader;
  private boolean shaderEnable;
  private int pressBgColor;
  private int pressTextColor;
  private boolean drawableAsBackground;
  private BitmapShader drawableBackgroundShader;

  private List<Adjuster> adjusterList = new ArrayList<>();
  private List<Adjuster> touchAdjusters = new ArrayList<>();
  private Runnable handleAnim;
  private boolean superTouchEvent;


  public SuperTextView(Context context) {
    super(context);
    init(null);
  }

  public SuperTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    density = getContext().getResources().getDisplayMetrics().density;
    initAttrs(attrs);
    paint = new Paint();
    initPaint();
  }

  private void initAttrs(AttributeSet attrs) {
    if (attrs != null) {
      TypedArray typedArray =
          getContext().obtainStyledAttributes(attrs, R.styleable.SuperTextView);
      corner = typedArray.getDimension(R.styleable.SuperTextView_corner, DEFAULT_CORNER);
      leftTopCornerEnable =
          typedArray.getBoolean(R.styleable.SuperTextView_left_top_corner, false);
      rightTopCornerEnable =
          typedArray.getBoolean(R.styleable.SuperTextView_right_top_corner, false);
      leftBottomCornerEnable =
          typedArray.getBoolean(R.styleable.SuperTextView_left_bottom_corner, false);
      rightBottomCornerEnable =
          typedArray.getBoolean(R.styleable.SuperTextView_right_bottom_corner, false);
      solid = typedArray.getColor(R.styleable.SuperTextView_solid, DEFAULT_SOLID);
      strokeWidth = typedArray.getDimension(R.styleable.SuperTextView_stroke_width,
          DEFAULT_STROKE_WIDTH);
      strokeColor =
          typedArray.getColor(R.styleable.SuperTextView_stroke_color, DEFAULT_STROKE_COLOR);
      drawable = typedArray.getDrawable(R.styleable.SuperTextView_state_drawable);
      drawableWidth =
          typedArray.getDimension(R.styleable.SuperTextView_state_drawable_width, 0);
      drawableHeight =
          typedArray.getDimension(R.styleable.SuperTextView_state_drawable_height, 0);
      drawablePaddingLeft =
          typedArray.getDimension(R.styleable.SuperTextView_state_drawable_padding_left, 0);
      drawablePaddingTop =
          typedArray.getDimension(R.styleable.SuperTextView_state_drawable_padding_top, 0);
      drawable2 = typedArray.getDrawable(R.styleable.SuperTextView_state_drawable2);
      drawable2Width =
          typedArray.getDimension(R.styleable.SuperTextView_state_drawable2_width, 0);
      drawable2Height =
          typedArray.getDimension(R.styleable.SuperTextView_state_drawable2_height, 0);
      drawable2PaddingLeft =
          typedArray.getDimension(R.styleable.SuperTextView_state_drawable2_padding_left, 0);
      drawable2PaddingTop =
          typedArray.getDimension(R.styleable.SuperTextView_state_drawable2_padding_top, 0);
      isShowState = typedArray.getBoolean(R.styleable.SuperTextView_isShowState, false);
      drawableAsBackground =
          typedArray.getBoolean(R.styleable.SuperTextView_drawableAsBackground, false);
      isShowState2 = typedArray.getBoolean(R.styleable.SuperTextView_isShowState2, false);
      stateDrawableMode = typedArray.getInteger(R.styleable.SuperTextView_state_drawable_mode,
          DEFAULT_STATE_DRAWABLE_MODE);
      stateDrawable2Mode = typedArray.getInteger(R.styleable.SuperTextView_state_drawable2_mode,
          DEFAULT_STATE_DRAWABLE_MODE);
      textStroke = typedArray.getBoolean(R.styleable.SuperTextView_text_stroke, false);
      textStrokeColor = typedArray.getColor(R.styleable.SuperTextView_text_stroke_color,
          DEFAULT_TEXT_STROKE_COLOR);
      textFillColor = typedArray.getColor(R.styleable.SuperTextView_text_fill_color,
          DEFAULT_TEXT_FILL_COLOR);
      textStrokeWidth = typedArray.getDimension(R.styleable.SuperTextView_text_stroke_width,
          DEFAULT_TEXT_STROKE_WIDTH);
      autoAdjust = typedArray.getBoolean(R.styleable.SuperTextView_autoAdjust, false);
      shaderStartColor =
          typedArray.getColor(R.styleable.SuperTextView_shaderStartColor, 0);
      shaderEndColor =
          typedArray.getColor(R.styleable.SuperTextView_shaderEndColor, 0);
      shaderMode = typedArray.getInteger(R.styleable.SuperTextView_shaderMode, 0);
      shaderEnable = typedArray.getBoolean(R.styleable.SuperTextView_shaderEnable, false);
      pressBgColor = typedArray.getColor(R.styleable.SuperTextView_pressBgColor, Color.TRANSPARENT);
      pressTextColor = typedArray.getColor(R.styleable.SuperTextView_pressTextColor, -99);
      typedArray.recycle();
    }
  }

  private void initPaint() {
    paint.reset();
    paint.setAntiAlias(true);
    paint.setDither(true);
    paint.setFilterBitmap(true);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (w != oldw && h != oldh) {
      drawableBackgroundShader = null;
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    width = getWidth();
    height = getHeight();
    drawStrokeLine(canvas);
    drawSolid(canvas);
    checkPressColor(canvas);
    isNeedToAdjust(canvas, Adjuster.Opportunity.BEFORE_DRAWABLE);
    drawStateDrawable(canvas);
    isNeedToAdjust(canvas, Adjuster.Opportunity.BEFORE_TEXT);
    if (textStroke) {
      getPaint().setStyle(Paint.Style.STROKE);
      setTextColor(textStrokeColor);
      getPaint().setFakeBoldText(true);
      getPaint().setStrokeWidth(textStrokeWidth);
      super.onDraw(canvas);
      getPaint().setStyle(Paint.Style.FILL);
      getPaint().setFakeBoldText(false);
      setTextColor(textFillColor);
    }
    super.onDraw(canvas);
    isNeedToAdjust(canvas, Adjuster.Opportunity.AT_LAST);
  }

  private void drawStrokeLine(Canvas canvas) {
    if (strokeWidth > 0) {
      if (strokeWidthPath == null) {
        strokeWidthPath = new Path();
      } else {
        strokeWidthPath.reset();
      }
      if (strokeLineRectF == null) {
        strokeLineRectF = new RectF();
      } else {
        strokeLineRectF.setEmpty();
      }
      strokeLineRectF.set(strokeWidth / 2, strokeWidth / 2, width - strokeWidth / 2,
          height - strokeWidth / 2);
      getCorners(corner);
      strokeWidthPath.addRoundRect(strokeLineRectF, corners, Path.Direction.CW);
      initPaint();
      paint.setStyle(Paint.Style.STROKE);
      paint.setColor(strokeColor);
      paint.setStrokeWidth(strokeWidth);
      canvas.drawPath(strokeWidthPath, paint);
    }
  }

  private void drawSolid(Canvas canvas) {
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
    solidRectF.set(strokeWidth, strokeWidth, width - strokeWidth, height - strokeWidth);
    getCorners(corner - strokeWidth / 2);
    solidPath.addRoundRect(solidRectF, corners, Path.Direction.CW);

    initPaint();
    paint.setStyle(Paint.Style.FILL);
    if (shaderEnable) {
      useShader(paint);
    } else {
      paint.setColor(solid);
    }
    canvas.drawPath(solidPath, paint);
  }

  private float[] getCorners(float corner) {
    leftTopCorner[0] = 0;
    leftTopCorner[1] = 0;
    rightTopCorner[0] = 0;
    rightTopCorner[1] = 0;
    leftBottomCorner[0] = 0;
    leftBottomCorner[1] = 0;
    rightBottomCorner[0] = 0;
    rightBottomCorner[1] = 0;
    if (this.leftTopCornerEnable || this.rightTopCornerEnable || this.leftBottomCornerEnable
        || this.rightBottomCornerEnable) {
      if (this.leftTopCornerEnable) {
        leftTopCorner[0] = corner;
        leftTopCorner[1] = corner;
      }
      if (this.rightTopCornerEnable) {
        rightTopCorner[0] = corner;
        rightTopCorner[1] = corner;
      }
      if (this.leftBottomCornerEnable) {
        leftBottomCorner[0] = corner;
        leftBottomCorner[1] = corner;
      }
      if (this.rightBottomCornerEnable) {
        rightBottomCorner[0] = corner;
        rightBottomCorner[1] = corner;
      }
    } else {
      leftTopCorner[0] = corner;
      leftTopCorner[1] = corner;
      rightTopCorner[0] = corner;
      rightTopCorner[1] = corner;
      leftBottomCorner[0] = corner;
      leftBottomCorner[1] = corner;
      rightBottomCorner[0] = corner;
      rightBottomCorner[1] = corner;
    }
    corners[0] = leftTopCorner[0];
    corners[1] = leftTopCorner[1];
    corners[2] = rightTopCorner[0];
    corners[3] = rightTopCorner[1];
    corners[4] = rightBottomCorner[0];
    corners[5] = rightBottomCorner[1];
    corners[6] = leftBottomCorner[0];
    corners[7] = leftBottomCorner[1];
    return corners;
  }

  /**
   *
   * @return 返回SuperTextView的圆角信息 {@link SuperTextView#getCorners(float)}.
   */
  public float[] getCorners(){
    return corners;
  }

  private void checkPressColor(Canvas canvas) {
    if (pressBgColor != Color.TRANSPARENT || pressTextColor != -99) {
      if (pressAdjuster == null) {
        pressAdjuster = new PressAdjuster(pressBgColor)
            .setPressTextColor(pressTextColor)
            .setType(Adjuster.TYPE_SYSTEM);
        adjusterList.add(0, pressAdjuster);
        SYSTEM_ADJUSTER_SIZE++;
      }
      ((PressAdjuster) pressAdjuster).setPressTextColor(pressTextColor);
      ((PressAdjuster) pressAdjuster).setPressBgColor(pressBgColor);
    }
  }

  private void useShader(Paint paint) {
    if (shader == null) {
      createShader();
    }
    paint.setShader(shader);
  }

  private boolean createShader() {
    if (shaderStartColor != 0 && shaderEndColor != 0) {
      float x0 = 0;
      float x1 = 0;
      float y0 = 0;
      float y1 = 0;
      int startColor = shaderStartColor;
      int endColor = shaderEndColor;
      switch (ShaderMode.valueOf(shaderMode)) {
        case TOP_TO_BOTTOM:
          y1 = height;
          break;
        case BOTTOM_TO_TOP:
          y1 = height;
          startColor = shaderEndColor;
          endColor = shaderStartColor;
          break;
        case LEFT_TO_RIGHT:
          x1 = width;
          break;
        case RIGHT_TO_LEFT:
          x1 = width;
          startColor = shaderEndColor;
          endColor = shaderStartColor;
          break;
      }
      shader = new LinearGradient(x0, y0, x1, y1, startColor, endColor, Shader.TileMode.CLAMP);
      return true;
    } else {
      return false;
    }
  }

  private void drawStateDrawable(Canvas canvas) {
    if (drawable != null) {
      if (drawableAsBackground) {
        drawDrawableBackground(canvas);
      } else if (isShowState) {
        getDrawableBounds();
        drawable.setBounds((int) drawableBounds[0], (int) drawableBounds[1],
            (int) drawableBounds[2], (int) drawableBounds[3]);
        drawable.draw(canvas);
      }
    }

    if (drawable2 != null && isShowState2) {
      getDrawable2Bounds();
      drawable2.setBounds((int) drawable2Bounds[0], (int) drawable2Bounds[1],
          (int) drawable2Bounds[2], (int) drawable2Bounds[3]);
      drawable2.draw(canvas);
    }
  }

  private void drawDrawableBackground(Canvas canvas) {
    if (drawableBackgroundShader == null) {
      Bitmap bitmap = drawableToBitmap(drawable);
      bitmap = computeSuitedBitmapSize(bitmap);
      drawableBackgroundShader =
          new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    Shader shader = paint.getShader();
    int color = paint.getColor();
    paint.setColor(Color.WHITE);
    paint.setShader(drawableBackgroundShader);
    canvas.drawPath(solidPath, paint);
    paint.setShader(shader);
    paint.setColor(color);
  }

  private Bitmap drawableToBitmap(Drawable drawable) {
    Bitmap bitmap = Bitmap.createBitmap(
        drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(),
        drawable.getOpacity() != PixelFormat.OPAQUE
            ? Bitmap.Config.ARGB_8888
            : Bitmap.Config.RGB_565);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    drawable.draw(canvas);
    return bitmap;
  }

  private Bitmap computeSuitedBitmapSize(Bitmap bitmap) {
    int suitedWidth = width;
    int suitedHeight = height;
    if ((float) bitmap.getWidth() / (float) width > (float) bitmap.getHeight()
        / (float) height) {
      suitedWidth = (int) (((float) bitmap.getWidth() / (float) bitmap.getHeight())
          * (float) suitedHeight);
    } else {
      suitedHeight = (int) ((float) suitedWidth
          / ((float) bitmap.getWidth() / (float) bitmap.getHeight()));
    }
    bitmap = Bitmap.createScaledBitmap(bitmap, suitedWidth, suitedHeight, true);
    bitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - width / 2,
        bitmap.getHeight() / 2 - height / 2, width, height);
    return bitmap;
  }

  private float[] getDrawableBounds() {
    for (int i = 0; i < drawableBounds.length; i++) {
      drawableBounds[i] = 0;
    }
    drawableWidth = (drawableWidth == 0 ? width / 2f : drawableWidth);
    drawableHeight = (drawableHeight == 0 ? height / 2f : drawableHeight);
    switch (DrawableMode.valueOf(stateDrawableMode)) {
      case LEFT: // left
        drawableBounds[0] = 0 + drawablePaddingLeft;
        drawableBounds[1] = height / 2f - drawableHeight / 2f + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
      case TOP: // top
        drawableBounds[0] = width / 2f - drawableWidth / 2f + drawablePaddingLeft;
        drawableBounds[1] = 0 + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
      case RIGHT: // right
        drawableBounds[0] = width - drawableWidth + drawablePaddingLeft;
        drawableBounds[1] = height / 2 - drawableHeight / 2 + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
      case BOTTOM: // bottom
        drawableBounds[0] = width / 2f - drawableWidth / 2f + drawablePaddingLeft;
        drawableBounds[1] = height - drawableHeight + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
      case CENTER: // center
        drawableBounds[0] = width / 2f - drawableWidth / 2f + drawablePaddingLeft;
        drawableBounds[1] = height / 2 - drawableHeight / 2 + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
      case FILL: // fill
        drawableBounds[0] = 0;
        drawableBounds[1] = 0;
        drawableBounds[2] = width;
        drawableBounds[3] = height;
        break;
      case LEFT_TOP: // leftTop
        drawableBounds[0] = 0 + drawablePaddingLeft;
        drawableBounds[1] = 0 + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
      case RIGHT_TOP: // rightTop
        drawableBounds[0] = width - drawableWidth + drawablePaddingLeft;
        drawableBounds[1] = 0 + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
      case LEFT_BOTTOM: // leftBottom
        drawableBounds[0] = 0 + drawablePaddingLeft;
        drawableBounds[1] = height - drawableHeight + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
      case RIGHT_BOTTOM: // rightBottom
        drawableBounds[0] = width - drawableWidth + drawablePaddingLeft;
        drawableBounds[1] = height - drawableHeight + drawablePaddingTop;
        drawableBounds[2] = drawableBounds[0] + drawableWidth;
        drawableBounds[3] = drawableBounds[1] + drawableHeight;
        break;
    }

    return drawableBounds;
  }

  private float[] getDrawable2Bounds() {
    for (int i = 0; i < drawable2Bounds.length; i++) {
      drawable2Bounds[i] = 0;
    }
    drawable2Width = (drawable2Width == 0 ? width / 2f : drawable2Width);
    drawable2Height = (drawable2Height == 0 ? height / 2f : drawable2Height);
    switch (DrawableMode.valueOf(stateDrawable2Mode)) {
      case LEFT: // left
        drawable2Bounds[0] = 0 + drawable2PaddingLeft;
        drawable2Bounds[1] = height / 2f - drawable2Height / 2f + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
      case TOP: // top
        drawable2Bounds[0] = width / 2f - drawable2Width / 2f + drawable2PaddingLeft;
        drawable2Bounds[1] = 0 + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
      case RIGHT: // right
        drawable2Bounds[0] = width - drawable2Width + drawable2PaddingLeft;
        drawable2Bounds[1] = height / 2 - drawable2Height / 2 + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
      case BOTTOM: // bottom
        drawable2Bounds[0] = width / 2f - drawable2Width / 2f + drawable2PaddingLeft;
        drawable2Bounds[1] = height - drawable2Height + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
      case CENTER: // center
        drawable2Bounds[0] = width / 2f - drawable2Width / 2f + drawable2PaddingLeft;
        drawable2Bounds[1] = height / 2 - drawable2Height / 2 + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
      case FILL: // fill
        drawable2Bounds[0] = 0;
        drawable2Bounds[1] = 0;
        drawable2Bounds[2] = width;
        drawable2Bounds[3] = height;
        break;
      case LEFT_TOP: // leftTop
        drawable2Bounds[0] = 0 + drawable2PaddingLeft;
        drawable2Bounds[1] = 0 + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
      case RIGHT_TOP: // rightTop
        drawable2Bounds[0] = width - drawable2Width + drawable2PaddingLeft;
        drawable2Bounds[1] = 0 + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
      case LEFT_BOTTOM: // leftBottom
        drawable2Bounds[0] = 0 + drawable2PaddingLeft;
        drawable2Bounds[1] = height - drawable2Height + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
      case RIGHT_BOTTOM: // rightBottom
        drawable2Bounds[0] = width - drawable2Width + drawable2PaddingLeft;
        drawable2Bounds[1] = height - drawable2Height + drawable2PaddingTop;
        drawable2Bounds[2] = drawable2Bounds[0] + drawable2Width;
        drawable2Bounds[3] = drawable2Bounds[1] + drawable2Height;
        break;
    }

    return drawable2Bounds;
  }


  private void isNeedToAdjust(Canvas canvas, Adjuster.Opportunity currentOpportunity) {

    for (int i = 0; i < adjusterList.size(); i++) {
      Adjuster adjuster = adjusterList.get(i);
      if (currentOpportunity == adjuster.getOpportunity()) {
        if (adjuster.getType() == Adjuster.TYPE_SYSTEM) {
          adjuster.adjust(this, canvas);
        } else if (autoAdjust) {
          adjuster.adjust(this, canvas);
        }
      }
    }

  }

  /**
   * @return 获取Corner值。默认为0。
   */
  public float getCorner() {
    return corner;
  }


  /**
   * @param corner 圆角大小，默认值为0。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setCorner(float corner) {
    this.corner = corner;
    postInvalidate();

    return this;
  }

  /**
   * @return 返回控件填充颜色。
   */
  public int getSolid() {
    return solid;
  }

  /**
   * @param solid 控件填充颜色, 默认为{@link Color#TRANSPARENT}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setSolid(int solid) {
    this.solid = solid;
    postInvalidate();

    return this;
  }

  /**
   * @return 返回控件边框的宽度。
   */
  public float getStrokeWidth() {
    return strokeWidth;
  }

  /**
   * @param strokeWidth 描边宽度。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setStrokeWidth(float strokeWidth) {
    this.strokeWidth = strokeWidth;
    postInvalidate();

    return this;
  }

  /**
   * @return 返回描边颜色。默认为{@link Color#BLACK}。
   */
  public int getStrokeColor() {
    return strokeColor;
  }

  /**
   * @param strokeColor 描边颜色。默认为{@link Color#BLACK}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setStrokeColor(int strokeColor) {
    this.strokeColor = strokeColor;
    postInvalidate();

    return this;
  }

  /**
   * 该方法或许在后面版本会移除，请尽快使用{@link SuperTextView#addAdjuster(Adjuster)}来添加一个Adjuster。
   * 
   * @param adjuster 添加一个Adjuster。{@link SuperTextView#addAdjuster(Adjuster)}
   *          注意最多支持添加3个Adjuster，否则新的Adjuster总是会覆盖最后一个Adjuster。
   *          {@link Adjuster}。会触发一次重绘。
   * 
   * @return SuperTextView
   */
  @Deprecated
  public SuperTextView setAdjuster(Adjuster adjuster) {

    return addAdjuster(adjuster);
  }

  /**
   * @return 获得最后一个Adjuster，如果存在的话。
   */
  public Adjuster getAdjuster() {
    if (adjusterList.size() > SYSTEM_ADJUSTER_SIZE) {
      return adjusterList.get(adjusterList.size() - 1);
    }
    return null;
  }

  /**
   * 
   * @param adjuster 添加一个Adjuster。{@link SuperTextView#addAdjuster(Adjuster)}
   *          注意最多支持添加3个Adjuster，否则新的Adjuster总是会覆盖最后一个Adjuster。
   *          {@link Adjuster}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView addAdjuster(Adjuster adjuster) {
    if (adjusterList.size() < SYSTEM_ADJUSTER_SIZE + ALLOW_CUSTOM_ADJUSTER_SIZE) {
      adjusterList.add(adjuster);
    } else {
      adjusterList.remove(adjusterList.size() - 1);
      adjusterList.add(adjuster);
    }
    postInvalidate();
    return this;
  }

  /**
   * 移除index对应的Adjuster。
   * 
   * @param index 期望移除的Adjuster的index。
   * @return 被移除的Adjuster，如果参数错误返回null。
   */
  public Adjuster removeAdjuster(int index) {
    int realIndex = SYSTEM_ADJUSTER_SIZE + index;
    if (realIndex > SYSTEM_ADJUSTER_SIZE - 1 && realIndex < adjusterList.size()) {
      Adjuster remove = adjusterList.remove(realIndex);
      postInvalidate();
      return remove;
    }
    return null;
  }

  /**
   * 获得index对应的Adjuster。
   * 
   * @param index 期望获得的Adjuster的index。
   * @return index对应的Adjuster，如果参数错误返回null。
   */
  public Adjuster getAdjuster(int index) {
    int realIndex = SYSTEM_ADJUSTER_SIZE + index;
    if (realIndex > SYSTEM_ADJUSTER_SIZE - 1 && realIndex < adjusterList.size()) {
      return adjusterList.remove(realIndex);
    }
    return null;
  }

  /**
   * @return true 表示开启了文字描边，否则表示没开启。
   */
  public boolean isTextStroke() {
    return textStroke;
  }

  /**
   * @param textStroke true表示开启文字描边。默认为false。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setTextStroke(boolean textStroke) {
    this.textStroke = textStroke;
    postInvalidate();

    return this;
  }

  /**
   * @return 文字描边的颜色。
   */
  public int getTextStrokeColor() {
    return textStrokeColor;
  }

  /**
   * @param textStrokeColor 设置文字描边的颜色。默认为{@link Color#BLACK}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setTextStrokeColor(int textStrokeColor) {
    this.textStrokeColor = textStrokeColor;
    postInvalidate();

    return this;
  }

  /**
   * @return 文字填充颜色。
   */
  public int getTextFillColor() {
    return textFillColor;
  }

  /**
   * @param textFillColor 设置文字填充颜色。默认为{@link Color#BLACK}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setTextFillColor(int textFillColor) {
    this.textFillColor = textFillColor;
    postInvalidate();

    return this;
  }

  /**
   * @return 文字描边宽度。
   */
  public float getTextStrokeWidth() {
    return textStrokeWidth;
  }

  /**
   * @param textStrokeWidth 设置文字描边宽度。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setTextStrokeWidth(float textStrokeWidth) {
    this.textStrokeWidth = textStrokeWidth;
    postInvalidate();

    return this;
  }

  /**
   * @return true表示开启了Adjuster功能。
   */
  public boolean isAutoAdjust() {
    return autoAdjust;
  }

  /**
   * @param autoAdjust true开启Adjuster功能。反之，关闭。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setAutoAdjust(boolean autoAdjust) {
    this.autoAdjust = autoAdjust;
    postInvalidate();

    return this;
  }

  /**
   * @return true表示左上角为圆角。
   */
  public boolean isLeftTopCornerEnable() {
    return leftTopCornerEnable;
  }

  /**
   * @param leftTopCornerEnable true左上角圆角化。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setLeftTopCornerEnable(boolean leftTopCornerEnable) {
    this.leftTopCornerEnable = leftTopCornerEnable;
    postInvalidate();

    return this;
  }

  /**
   * @return true表示右上角为圆角。
   */
  public boolean isRightTopCornerEnable() {
    return rightTopCornerEnable;
  }

  /**
   * @param rightTopCornerEnable true右上角圆角化。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setRightTopCornerEnable(boolean rightTopCornerEnable) {
    this.rightTopCornerEnable = rightTopCornerEnable;
    postInvalidate();

    return this;
  }

  /**
   * @return true表示左下角为圆角。
   */
  public boolean isLeftBottomCornerEnable() {
    return leftBottomCornerEnable;
  }

  /**
   * @param leftBottomCornerEnable true左下角圆角化。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setLeftBottomCornerEnable(boolean leftBottomCornerEnable) {
    this.leftBottomCornerEnable = leftBottomCornerEnable;
    postInvalidate();

    return this;
  }

  /**
   * @return true表示右下角为圆角。
   */
  public boolean isRightBottomCornerEnable() {
    return rightBottomCornerEnable;
  }

  /**
   * @param rightBottomCornerEnable true右下角圆角化。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setRightBottomCornerEnable(boolean rightBottomCornerEnable) {
    this.rightBottomCornerEnable = rightBottomCornerEnable;
    postInvalidate();

    return this;
  }

  /**
   * @return 状态图。
   */
  public Drawable getDrawable() {
    return drawable;
  }

  /**
   * @return 状态图2。
   */
  public Drawable getDrawable2() {
    return drawable2;
  }

  /**
   * @param drawable 设置状态图。需要调用isShowState(true)才能显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable(Drawable drawable) {
    this.drawable = drawable;
    drawableBackgroundShader = null;
    postInvalidate();

    return this;
  }

  /**
   * @param drawable 设置状态图2。需要调用isShowState2(true)才能显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable2(Drawable drawable) {
    this.drawable2 = drawable;
    postInvalidate();

    return this;
  }

  /**
   * @param drawableRes 使用drawable资源设置状态图。需要调用isShowState(true)才能显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable(int drawableRes) {
    this.drawable = getResources().getDrawable(drawableRes);
    drawableBackgroundShader = null;
    postInvalidate();

    return this;
  }

  /**
   * @param drawableRes 使用drawable资源设置状态图2。需要调用isShowState2(true)才能显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable2(int drawableRes) {
    this.drawable2 = getResources().getDrawable(drawableRes);
    postInvalidate();

    return this;
  }

  /**
   * @return 当前是否显示状态图。
   */
  public boolean isShowState() {
    return isShowState;
  }

  /**
   * @return 当前Drawable是否作为SuperTextView的背景图。
   */
  public boolean isDrawableAsBackground() {
    return drawableAsBackground;
  }

  /**
   * @return 当前是否显示状态图2。
   */
  public boolean isShowState2() {
    return isShowState2;
  }

  /**
   * @param showState true，表示显示状态图。反之，不显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setShowState(boolean showState) {
    isShowState = showState;
    postInvalidate();

    return this;
  }

  /**
   *
   * @param drawableAsBackground true，表示将Drawable作为背景图，其余所有对drawable的设置都会失效，直到该项为false。
   * @return SuperTextView
   */
  public SuperTextView setDrawableAsBackground(boolean drawableAsBackground) {
    this.drawableAsBackground = drawableAsBackground;
    if (!drawableAsBackground) {
      drawableBackgroundShader = null;
    }
    postInvalidate();
    return this;
  }

  /**
   * @param showState true，表示显示状态图2。反之，不显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setShowState2(boolean showState) {
    isShowState2 = showState;
    postInvalidate();

    return this;
  }

  /**
   * @return 状态图显示模式。{@link DrawableMode}
   */
  public int getStateDrawableMode() {
    return stateDrawableMode;
  }

  /**
   * @return 状态图2显示模式。{@link DrawableMode}
   */
  public int getStateDrawable2Mode() {
    return stateDrawable2Mode;
  }

  /**
   * @param stateDrawableMode 设置状态图显示模式。默认为{@link DrawableMode#CENTER}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setStateDrawableMode(int stateDrawableMode) {
    this.stateDrawableMode = stateDrawableMode;
    postInvalidate();

    return this;
  }

  /**
   * @param stateDrawableMode 设置状态图2显示模式。默认为{@link DrawableMode#CENTER}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setStateDrawable2Mode(int stateDrawableMode) {
    this.stateDrawable2Mode = stateDrawableMode;
    postInvalidate();

    return this;
  }

  /**
   * @return 状态图的宽度。
   */
  public float getDrawableWidth() {
    return drawableWidth;
  }

  /**
   * @return 状态图2的宽度。
   */
  public float getDrawable2Width() {
    return drawable2Width;
  }

  /**
   * @param drawableWidth 设置状态图宽度。默认为控件的1／2。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawableWidth(float drawableWidth) {
    this.drawableWidth = drawableWidth;
    postInvalidate();

    return this;
  }

  /**
   * @param drawableWidth 设置状态图2宽度。默认为控件的1／2。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable2Width(float drawableWidth) {
    this.drawable2Width = drawableWidth;
    postInvalidate();

    return this;
  }

  /**
   * @return 状态图的高度。
   */
  public float getDrawableHeight() {
    return drawableHeight;
  }

  /**
   * @return 状态图2的高度。
   */
  public float getDrawable2Height() {
    return drawable2Height;
  }

  /**
   * @param drawableHeight 设置状态图高度。默认为控件的1／2。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawableHeight(float drawableHeight) {
    this.drawableHeight = drawableHeight;
    postInvalidate();

    return this;
  }

  /**
   * @param drawableHeight 设置状态图2高度。默认为控件的1／2。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable2Height(float drawableHeight) {
    this.drawable2Height = drawableHeight;
    postInvalidate();

    return this;
  }

  /**
   * @return 状态图相对于左边相对位置的左边距。
   */
  public float getDrawablePaddingLeft() {
    return drawablePaddingLeft;
  }

  /**
   * @return 状态图2相对于左边相对位置的左边距。
   */
  public float getDrawable2PaddingLeft() {
    return drawable2PaddingLeft;
  }

  /**
   * @param drawablePaddingLeft 设置状态图相对于左边相对位置的左边距。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawablePaddingLeft(float drawablePaddingLeft) {
    this.drawablePaddingLeft = drawablePaddingLeft;
    postInvalidate();

    return this;
  }

  /**
   * @param drawablePaddingLeft 设置状态图2相对于左边相对位置的左边距。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable2PaddingLeft(float drawablePaddingLeft) {
    this.drawable2PaddingLeft = drawable2PaddingLeft;
    postInvalidate();

    return this;
  }

  /**
   * @return 状态图相对于上边相对位置的上边距。
   */
  public float getDrawablePaddingTop() {
    return drawablePaddingTop;
  }

  /**
   * @return 状态图2相对于上边相对位置的上边距。
   */
  public float getDrawable2PaddingTop() {
    return drawable2PaddingTop;
  }

  /**
   * @param drawablePaddingTop 设置状态图相对于上边相对位置的上边距。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawablePaddingTop(float drawablePaddingTop) {
    this.drawablePaddingTop = drawablePaddingTop;
    postInvalidate();
    return this;
  }

  /**
   * @param drawablePaddingTop 设置状态图相对于上边相对位置的上边距。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable2PaddingTop(float drawablePaddingTop) {
    this.drawable2PaddingTop = drawablePaddingTop;
    postInvalidate();
    return this;
  }

  /**
   * @return 渐变起始色。
   */
  public int getShaderStartColor() {
    return shaderStartColor;
  }

  /**
   * @param shaderStartColor 设置渐变起始色。需要调用{@link SuperTextView#setShaderEnable(boolean)}后才能生效。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setShaderStartColor(int shaderStartColor) {
    this.shaderStartColor = shaderStartColor;
    shader = null;
    postInvalidate();
    return this;
  }

  /**
   * @return 渐变结束色。
   */
  public int getShaderEndColor() {
    return shaderEndColor;
  }

  /**
   * @param shaderEndColor 设置渐变结束色。需要调用{@link SuperTextView#setShaderEnable(boolean)}后才能生效。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setShaderEndColor(int shaderEndColor) {
    this.shaderEndColor = shaderEndColor;
    shader = null;
    postInvalidate();
    return this;
  }

  /**
   * @return 渐变模式。{@link ShaderMode}。
   */
  public int getShaderMode() {
    return shaderMode;
  }

  /**
   * @param shaderMode 设置渐变模式。{@link ShaderMode}。
   * @return SuperTextView
   */
  public SuperTextView setShaderMode(int shaderMode) {
    this.shaderMode = shaderMode;
    shader = null;
    postInvalidate();
    return this;
  }

  /**
   * @return 是否启用了渐变功能。
   */
  public boolean isShaderEnable() {
    return shaderEnable;
  }

  /**
   * @param shaderEnable true启用渐变功能。反之，停用。
   * @return SuperTextView
   */
  public SuperTextView setShaderEnable(boolean shaderEnable) {
    this.shaderEnable = shaderEnable;
    postInvalidate();
    return this;
  }

  /**
   * 获得当前按压背景色。没有设置默认为Color.TRANSPARENT。
   * @return
   */
  public int getPressBgColor() {
    return pressBgColor;
  }

  /**
   * 获得当前按压背景色。一旦设置，立即生效。
   * 取消可以设置Color.TRANSPARENT。
   *
   * @param pressBgColor
   */
  public SuperTextView setPressBgColor(int pressBgColor) {
    this.pressBgColor = pressBgColor;
    return this;
  }

  /**
   * 获得当前按压文字色。没有设置默认为-99。
   * @return
   */
  public int getPressTextColor() {
    return pressTextColor;
  }

  /**
   * 获得当前按压文字色。一旦设置，立即生效。
   * 取消可以设置-99。
   * @param pressTextColor
   */
  public SuperTextView setPressTextColor(int pressTextColor) {
    this.pressTextColor = pressTextColor;
    return this;
  }

  /**
   * @return 帧率
   */
  public int getFrameRate() {
    return frameRate;
  }

  /**
   * @param frameRate 设置帧率，即每秒帧数。可在动画过程中随时改变。
   * @return SuperTextView
   */
  public SuperTextView setFrameRate(int frameRate) {
    if (frameRate > 0) {
      this.frameRate = frameRate;
    } else {
      this.frameRate = 60;
    }
    return this;
  }


  /**
   * 启动动画。需要设置{@link SuperTextView#setAutoAdjust(boolean)}为true才能看到。
   */
  public void startAnim() {
    needRun = true;
    runnable = false;
    if (animThread == null) {
      checkWhetherNeedInitInvalidate();
      needRun = true;
      runnable = true;
      if (handleAnim == null){
        initHandleAnim();
      }
      animThread = new Thread(handleAnim);
      animThread.start();
    }
  }

  private void initHandleAnim() {
    handleAnim = new Runnable() {
      @Override
      public void run() {
        while (runnable) {
          synchronized (invalidate) {
            post(invalidate);
          }
          try {
            Thread.sleep(1000 / frameRate);
          } catch (InterruptedException e) {
            e.printStackTrace();
            runnable = false;
          }
          // Log.e("SuperTextView", " -> startAnim: " + Thread.currentThread().getId() + "-> "
          // + hashCode() + ": It's running!");
        }
        animThread = null;
        if (needRun) {
          startAnim();
        }
      }
    };
  }

  private void checkWhetherNeedInitInvalidate() {
    if (invalidate == null) {
      invalidate = new Runnable() {
        @Override
        public void run() {
          postInvalidate();
        }
      };
    }
  }

  /**
   * 停止动画。不能保证立即停止，但最终会停止。
   */
  public void stopAnim() {
    runnable = false;
    needRun = false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {

    boolean hasConsume = false;
    int action = event.getAction();
    int actionMasked = action & MotionEvent.ACTION_MASK;
    if (actionMasked == MotionEvent.ACTION_DOWN) {
      for (int i = 0; i < adjusterList.size(); i++) {
        Adjuster adjuster = adjusterList.get(i);
        if (adjuster.onTouch(this, event)) {
          if (adjuster.type == Adjuster.TYPE_SYSTEM || isAutoAdjust()) {
            hasConsume = true;
            touchAdjusters.add(adjuster);
          }
        }
      }
      superTouchEvent = super.onTouchEvent(event);
    } else {
      for (int i = 0; i < touchAdjusters.size(); i++) {
        Adjuster adjuster = touchAdjusters.get(i);
        adjuster.onTouch(this, event);
        hasConsume = true;
      }
      if (superTouchEvent){
        super.onTouchEvent(event);
      }
      if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
        touchAdjusters.clear();
        superTouchEvent = false;
      }
    }
    return hasConsume || superTouchEvent;
  }

  @Override
  protected void onWindowVisibilityChanged(int visibility) {
    super.onWindowVisibilityChanged(visibility);
    if (visibility != VISIBLE && visibility != INVISIBLE) {
      cacheRunnableState = runnable;
      cacheNeedRunState = needRun;
      stopAnim();
    } else if (cacheRunnableState && cacheNeedRunState) {
      startAnim();
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    stopAnim();
    super.onDetachedFromWindow();
  }

  /**
   * Adjuster被设计用来在SuperTextView的绘制过程中插入一些操作。
   * 这具有非常重要的意义。比如，默认实现的{@link DefaultAdjuster}能够动态的调整文字的大小。
   * 当然，你可以用它来实现各种各样的效果。
   * 你可以指定Adjuster的作用层级，通过调用{@link Adjuster#setOpportunity(Opportunity)}，
   * {@link Opportunity}。默认为{@link Opportunity#BEFORE_TEXT}。
   */
  public static abstract class Adjuster {
    private static final int TYPE_SYSTEM = 0x001;
    private static final int TYPE_CUSTOM = 0x002;


    private Opportunity opportunity = Opportunity.BEFORE_TEXT;
    private int type = TYPE_CUSTOM;

    /**
     * 在Canvas上绘制的东西将能够呈现在SuperTextView上。
     * 提示：你需要注意图层的使用。
     *
     * @param v SuperTextView
     * @param canvas 用于绘制的Canvas。注意对Canvas的变换最好使用图层，否则会影响后续的绘制。
     */
    protected abstract void adjust(SuperTextView v, Canvas canvas);

    /**
     * 在这个方法中，你能够捕获到SuperTextView中发生的触摸事件。
     * 需要注意，如果你在该方法中返回了true来处理SuperTextView的触摸事件的话，你将在
     * SuperTextView的setOnTouchListener中设置的OnTouchListener中同样能够捕获到这些触摸事件，即使你在OnTouchListener中返回了false。
     * 但是，如果你在OnTouchListener中返回了true，这个方法将会失效，因为事件在OnTouchListener中被拦截了。
     *
     * @param v SuperTextView
     * @param event 控件件接收到的触摸事件。
     * @return 默认返回false。如果想持续的处理控件的触摸事件就返回true。否则，只能接收到{@link MotionEvent#ACTION_DOWN}事件。
     */
    public boolean onTouch(SuperTextView v, MotionEvent event) {
      return false;
    };

    /**
     * @return Adjuster的作用层级。
     */
    public Opportunity getOpportunity() {
      return opportunity;
    }

    /**
     * @param opportunity 设置Adjuster的作用层级。{@link Opportunity}
     * @return 返回Adjuster本身，方便调用。
     */
    public Adjuster setOpportunity(Opportunity opportunity) {
      this.opportunity = opportunity;
      return this;
    }

    /**
     * @hide
     */
    private Adjuster setType(int type) {
      this.type = type;
      return this;
    }

    private int getType() {
      return type;
    }

    /**
     * Adjuster贴心的设计了控制作用层级的功能。
     * 你可以通过{@link Adjuster#setOpportunity(Opportunity)}来指定Adjuster的绘制层级。
     * 在SuperTextView中，绘制层级被从下到上分为：背景层、Drawable层、文字层3个层级。
     * 通过Opportunity来指定你的Adjuster想要插入到那个层级间。
     */
    public static enum Opportunity {
      /**
       * 背景层和Drawable层之间
       */
      BEFORE_DRAWABLE,
      /**
       * Drawable层和文字层之间
       */
      BEFORE_TEXT,
      /**
       * 最上层
       */
      AT_LAST
    }
  }

  /**
   * 状态图的显示模式。SuperTextView定义了10中显示模式。它们控制着状态图的相对位置。
   * 默认为居中，即{@link DrawableMode#CENTER}。
   */
  public static enum DrawableMode {
    /**
     * 正左
     */
    LEFT(0),
    /**
     * 正上
     */
    TOP(1),
    /**
     * 正右
     */
    RIGHT(2),
    /**
     * 正下
     */
    BOTTOM(3),
    /**
     * 居中
     */
    CENTER(4),
    /**
     * 充满整个控件
     */
    FILL(5),
    /**
     * 左上
     */
    LEFT_TOP(6),
    /**
     * 右上
     */
    RIGHT_TOP(7),
    /**
     * 左下
     */
    LEFT_BOTTOM(8),
    /**
     * 右下
     */
    RIGHT_BOTTOM(9);

    public int code;
    DrawableMode(int code) {
      this.code = code;
    }

    public static DrawableMode valueOf(int code) {
      for (DrawableMode mode : DrawableMode.values()) {
        if (mode.code == code) {
          return mode;
        }
      }
      return CENTER;
    }
  }

  /**
   * 渐变模式。
   */
  public static enum ShaderMode {
    /**
     * 从上到下
     */
    TOP_TO_BOTTOM(0),
    /**
     * 从下到上
     */
    BOTTOM_TO_TOP(1),
    /**
     * 从左到右
     */
    LEFT_TO_RIGHT(2),
    /**
     * 从右到左
     */
    RIGHT_TO_LEFT(3);

    public int code;

    ShaderMode(int code) {
      this.code = code;
    }

    public static ShaderMode valueOf(int code) {
      for (ShaderMode mode : ShaderMode.values()) {
        if (mode.code == code) {
          return mode;
        }
      }
      return TOP_TO_BOTTOM;
    }
  }

  public static class DefaultAdjuster extends Adjuster {

    @Override
    public void adjust(SuperTextView v, Canvas canvas) {
      int length = v.length();
      float scale = v.getWidth() / (116.28f * v.getResources().getDisplayMetrics().density);
      float[] textSizes = {
          37.21f, 37.21f, 24.81f, 27.9f, 24.81f,
          22.36f, 18.6f,
          18.6f
      };
      switch (length) {
        case 1:
          v.setTextSize(textSizes[0] * scale);
          break;
        case 2:
          v.setTextSize(textSizes[1] * scale);
          break;
        case 3:
          v.setTextSize(textSizes[2] * scale);
          break;
        case 4:
          v.setTextSize(textSizes[3] * scale);
          break;
        case 5:
        case 6:
          v.setTextSize(textSizes[4] * scale);
          break;
        case 7:
        case 8:
        case 9:
          v.setTextSize(textSizes[5] * scale);
          break;
        case 10:
        case 11:
        case 12:
          v.setTextSize(textSizes[6] * scale);
          break;
        case 13:
        case 14:
        case 15:
        case 16:
          v.setTextSize(textSizes[7] * scale);
          break;
      }
    }
  }
}
