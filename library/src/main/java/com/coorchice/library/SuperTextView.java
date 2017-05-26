

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


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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

  private float corner;
  private boolean leftTopCornerEnable;
  private boolean rightTopCornerEnable;
  private boolean leftBottomCornerEnable;
  private boolean rightBottomCornerEnable;
  private int solid;
  private float strokeWidth;
  private int strokeColor;
  private int stateDrawableMode;
  private boolean isShowState;

  private Paint paint;
  private int width;
  private int height;
  private Drawable drawable;
  private float density;
  private boolean autoAdjust;
  private Adjuster adjuster;
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
  private boolean cacheRunnableState;
  private boolean cacheNeedRunState;
  private int frameRate = 60;
  private Runnable invalidate;


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
      isShowState = typedArray.getBoolean(R.styleable.SuperTextView_isShowState, false);
      stateDrawableMode = typedArray.getInteger(R.styleable.SuperTextView_state_drawable_mode,
          DEFAULT_STATE_DRAWABLE_MODE);
      textStroke = typedArray.getBoolean(R.styleable.SuperTextView_text_stroke, false);
      textStrokeColor = typedArray.getColor(R.styleable.SuperTextView_text_stroke_color,
          DEFAULT_TEXT_STROKE_COLOR);
      textFillColor = typedArray.getColor(R.styleable.SuperTextView_text_fill_color,
          DEFAULT_TEXT_FILL_COLOR);
      textStrokeWidth = typedArray.getDimension(R.styleable.SuperTextView_text_stroke_width,
          DEFAULT_TEXT_STROKE_WIDTH);
      autoAdjust = typedArray.getBoolean(R.styleable.SuperTextView_autoAdjust, false);
      typedArray.recycle();
    }
  }

  private void initPaint() {
    paint.reset();
    paint.setAntiAlias(true);
    paint.setDither(true);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    width = getWidth();
    height = getHeight();
    drawStrokeLine(canvas);
    drawSolid(canvas);
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
    paint.setColor(solid);
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

  private void drawStateDrawable(Canvas canvas) {
    if (drawable != null && isShowState) {
      getDrawableBounds();
      drawable.setBounds((int) drawableBounds[0], (int) drawableBounds[1], (int) drawableBounds[2],
          (int) drawableBounds[3]);
      drawable.draw(canvas);
    }
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

  private void isNeedToAdjust(Canvas canvas, Adjuster.Opportunity currentOpportunity) {
    if (autoAdjust) {
      if (adjuster == null) {
        setAdjuster(new DefaultAdjuster());
      } else {
        if (currentOpportunity == adjuster.getOpportunity()) {
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
   * @param adjuster 设置Adjuster。{@link Adjuster}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setAdjuster(Adjuster adjuster) {
    this.adjuster = adjuster;
    postInvalidate();

    return this;
  }

  /**
   * @return 获得Adjuster，如果存在的话。
   */
  public Adjuster getAdjuster() {
    return adjuster;
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
   * @param drawable 设置状态图。需要调用isShowState(true)才能显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable(Drawable drawable) {
    this.drawable = drawable;
    postInvalidate();

    return this;
  }

  /**
   * @param drawableRes 使用drawable资源设置状态图。需要调用isShowState(true)才能显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawable(int drawableRes) {
    this.drawable = getResources().getDrawable(drawableRes);
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
   * @param showState true，表示显示状态图。反之，不显示。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setShowState(boolean showState) {
    isShowState = showState;
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
   * @param stateDrawableMode 设置状态图显示模式。默认为{@link DrawableMode#CENTER}。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setStateDrawableMode(int stateDrawableMode) {
    this.stateDrawableMode = stateDrawableMode;
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
   * @param drawableWidth 设置状态图宽度。默认为控件的1／2。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawableWidth(float drawableWidth) {
    this.drawableWidth = drawableWidth;
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
   * @param drawableHeight 设置状态图高度。默认为控件的1／2。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawableHeight(float drawableHeight) {
    this.drawableHeight = drawableHeight;
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
   * @param drawablePaddingLeft 设置状态图相对于左边相对位置的左边距。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawablePaddingLeft(float drawablePaddingLeft) {
    this.drawablePaddingLeft = drawablePaddingLeft;
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
   * @param drawablePaddingTop 设置状态图相对于上边相对位置的上边距。会触发一次重绘。
   * @return SuperTextView
   */
  public SuperTextView setDrawablePaddingTop(float drawablePaddingTop) {
    this.drawablePaddingTop = drawablePaddingTop;
    postInvalidate();

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
    checkWhetherNeedInitInvalidate();
    needRun = true;
    runnable = false;
    if (animThread == null) {
      needRun = true;
      runnable = true;
      animThread = new Thread(new Runnable() {
        @Override
        public void run() {
          while (runnable) {
            synchronized (invalidate){
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
      });
      animThread.start();
    }
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
    if (adjuster != null) {
      if (adjuster.onTouch(this, event) && isAutoAdjust()) {
        super.onTouchEvent(event);
        return true;
      }
    }
    return super.onTouchEvent(event);
  }

  @Override
  protected void onWindowVisibilityChanged(int visibility) {
    super.onWindowVisibilityChanged(visibility);
    if (visibility != VISIBLE) {
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
    private Opportunity opportunity = Opportunity.BEFORE_TEXT;

    /**
     * @param v SuperTextView
     * @param canvas 用于绘制的Canvas。注意对Canvas的变换最好使用图层，否则会影响后续的绘制。
     */
    protected abstract void adjust(SuperTextView v, Canvas canvas);

    /**
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
     */
    public void setOpportunity(Opportunity opportunity) {
      this.opportunity = opportunity;
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
       * 上层
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
