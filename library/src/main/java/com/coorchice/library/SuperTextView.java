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
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;


public class SuperTextView extends TextView {

    // Some Property Default Value
    private static final float DEFAULT_CORNER = 0f;
    private static final int DEFAULT_SOLID = Color.WHITE;
    private static final float DEFAULT_STROKE_WIDTH = 0f;
    private static final int DEFAULT_STROKE_COLOR = Color.WHITE;
    private static final int DEFAULT_STATE_DRAWABLE_MODE = 4;
    private static final int DEFAULT_TEXT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXT_FILL_COLOR = Color.BLACK;
    private static final float DEFAULT_TEXT_STROKE_WIDTH = 0f;

    private float corner;
    private boolean leftTopCornerEnable;
    private boolean rightTopCornerEnable;
    private boolean leftBottomCornerEnable;
    private boolean rightBottomCornerEnable;
    //  private int solid;
    private int bgColorNormal;
    private int bgColorPressed;
    private int bgColorDisable;
    private int strokeColorNormal;
    private int strokeColorPressed;
    private int strokeColorDisable;
    private float strokeWidth;
    //  private int strokeColor;
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
    private boolean pressed = false;
    private int mTouchSlop; //控件边缘的溢出值，在溢出值范围内也可以看做是在控件区域里


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
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
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
            strokeWidth = typedArray.getDimension(R.styleable.SuperTextView_stroke_width,
                    DEFAULT_STROKE_WIDTH);
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

            ColorStateList bgColors = typedArray.getColorStateList(R.styleable.SuperTextView_solid);
            if (bgColors != null) {
                setBgColorWithStateList(bgColors, DEFAULT_SOLID);
            } else {
                bgColorNormal = DEFAULT_SOLID;
                bgColorPressed = Color.parseColor("#0cff0000");
                bgColorDisable = Color.parseColor("#06ff0000");
            }

            ColorStateList strokeColors = typedArray.getColorStateList(R.styleable.SuperTextView_stroke_color);
            if (strokeColors != null) {
                setStrokeColorWithStateList(strokeColors, DEFAULT_STROKE_COLOR);
            } else {
                strokeColorNormal = DEFAULT_STROKE_COLOR;
                strokeColorPressed = Color.parseColor("#0cff0000");
                strokeColorDisable = Color.parseColor("#06ff0000");
            }
            typedArray.recycle();
        }
    }

    private void setBgColorWithStateList(ColorStateList bgColors, int defaultColor) {
        bgColorNormal = bgColors.getColorForState(new int[]{android.R.attr.state_enabled}, defaultColor);
        bgColorPressed = bgColors.getColorForState(new int[]{android.R.attr.state_pressed}, bgColorNormal);
        bgColorDisable = bgColors.getColorForState(new int[]{-android.R.attr.state_enabled}, bgColorNormal);
    }

    private void setStrokeColorWithStateList(ColorStateList strokeColors, int defaultColor) {
        strokeColorNormal = strokeColors.getColorForState(new int[]{android.R.attr.state_enabled}, defaultColor);
        strokeColorPressed = strokeColors.getColorForState(new int[]{android.R.attr.state_pressed}, strokeColorNormal);
        strokeColorDisable = strokeColors.getColorForState(new int[]{-android.R.attr.state_enabled}, strokeColorNormal);
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
            if (isEnabled()) {
                if (pressed) {
                    paint.setColor(strokeColorPressed);
                } else {
                    paint.setColor(strokeColorNormal);
                }
            } else {
                paint.setColor(strokeColorDisable);
            }
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
        if (isEnabled()) {
            if (pressed) {
                paint.setColor(bgColorPressed);
            } else {
                paint.setColor(bgColorNormal);
            }
        } else {
            paint.setColor(bgColorDisable);
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

    public float getCorner() {
        return corner;
    }

    public SuperTextView setCorner(float corner) {
        this.corner = corner;
        postInvalidate();

        return this;
    }

    public int getSolid() {
        return bgColorNormal;
    }

    /**
     * Sets bgColor attrId
     *
     * @param attrId the color attr id
     * @return
     */
    public SuperTextView setBgColorAttr(@AttrRes int attrId) {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, typedValue, true);
        setBgColor(typedValue.resourceId);
        return this;
    }

    /**
     * Sets bgColor colorId
     *
     * @param colorId the color id
     * @return
     */
    public SuperTextView setBgColor(@ColorRes int colorId) {
        ColorStateList colorStateList = ContextCompat.getColorStateList(getContext(), colorId);
        if (colorStateList != null) {
            setBgColorWithStateList(colorStateList, bgColorNormal);
        }
        postInvalidate();
        return this;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public SuperTextView setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        postInvalidate();

        return this;
    }

    public int getStrokeColor() {
        return strokeColorNormal;
    }

    /**
     * Sets StrokeColor colorId
     *
     * @param colorId the color id
     * @return
     */
    public SuperTextView setStrokeColor(@ColorRes int colorId) {
        ColorStateList colorStateList = ContextCompat.getColorStateList(getContext(), colorId);
        if (colorStateList != null) {
            setStrokeColorWithStateList(colorStateList, strokeColorNormal);
        }
        postInvalidate();
        return this;
    }

    /**
     * Sets StrokeColor attrId
     *
     * @param attrId the color attr id
     * @return
     */
    public SuperTextView setStrokeColorAttr(@AttrRes int attrId) {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, typedValue, true);
        setStrokeColor(typedValue.resourceId);
        return this;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public SuperTextView setDrawable(Drawable drawable) {
        this.drawable = drawable;
        postInvalidate();

        return this;
    }

    public boolean isShowState() {
        return isShowState;
    }

    public SuperTextView setShowState(boolean showState) {
        isShowState = showState;
        postInvalidate();

        return this;
    }

    public int getStateDrawableMode() {
        return stateDrawableMode;
    }

    public SuperTextView setStateDrawableMode(int stateDrawableMode) {
        this.stateDrawableMode = stateDrawableMode;
        postInvalidate();

        return this;
    }

    public Adjuster getAdjuster() {
        return adjuster;
    }

    public SuperTextView setAdjuster(Adjuster adjuster) {
        this.adjuster = adjuster;
        postInvalidate();

        return this;
    }

    public boolean isTextStroke() {
        return textStroke;
    }

    public SuperTextView setTextStroke(boolean textStroke) {
        this.textStroke = textStroke;
        postInvalidate();

        return this;
    }

    public int getTextStrokeColor() {
        return textStrokeColor;
    }

    public SuperTextView setTextStrokeColor(int textStrokeColor) {
        this.textStrokeColor = textStrokeColor;
        postInvalidate();

        return this;
    }

    public int getTextFillColor() {
        return textFillColor;
    }

    public SuperTextView setTextFillColor(int textFillColor) {
        this.textFillColor = textFillColor;
        postInvalidate();

        return this;
    }

    public float getTextStrokeWidth() {
        return textStrokeWidth;
    }

    public SuperTextView setTextStrokeWidth(float textStrokeWidth) {
        this.textStrokeWidth = textStrokeWidth;
        postInvalidate();

        return this;
    }

    public boolean isAutoAdjust() {
        return autoAdjust;
    }

    public SuperTextView setAutoAdjust(boolean autoAdjust) {
        this.autoAdjust = autoAdjust;
        postInvalidate();

        return this;
    }

    public boolean isLeftTopCornerEnable() {
        return leftTopCornerEnable;
    }

    public SuperTextView setLeftTopCornerEnable(boolean leftTopCornerEnable) {
        this.leftTopCornerEnable = leftTopCornerEnable;
        postInvalidate();

        return this;
    }

    public boolean isRightTopCornerEnable() {
        return rightTopCornerEnable;
    }

    public SuperTextView setRightTopCornerEnable(boolean rightTopCornerEnable) {
        this.rightTopCornerEnable = rightTopCornerEnable;
        postInvalidate();

        return this;
    }

    public boolean isLeftBottomCornerEnable() {
        return leftBottomCornerEnable;
    }

    public SuperTextView setLeftBottomCornerEnable(boolean leftBottomCornerEnable) {
        this.leftBottomCornerEnable = leftBottomCornerEnable;
        postInvalidate();

        return this;
    }

    public boolean isRightBottomCornerEnable() {
        return rightBottomCornerEnable;
    }

    public SuperTextView setRightBottomCornerEnable(boolean rightBottomCornerEnable) {
        this.rightBottomCornerEnable = rightBottomCornerEnable;
        postInvalidate();

        return this;
    }

    public float getDrawableWidth() {
        return drawableWidth;
    }

    public SuperTextView setDrawableWidth(float drawableWidth) {
        this.drawableWidth = drawableWidth;
        postInvalidate();

        return this;
    }

    public float getDrawableHeight() {
        return drawableHeight;
    }

    public SuperTextView setDrawableHeight(float drawableHeight) {
        this.drawableHeight = drawableHeight;
        postInvalidate();

        return this;
    }

    public float getDrawablePaddingLeft() {
        return drawablePaddingLeft;
    }

    public SuperTextView setDrawablePaddingLeft(float drawablePaddingLeft) {
        this.drawablePaddingLeft = drawablePaddingLeft;
        postInvalidate();

        return this;
    }

    public float getDrawablePaddingTop() {
        return drawablePaddingTop;
    }

    public SuperTextView setDrawablePaddingTop(float drawablePaddingTop) {
        this.drawablePaddingTop = drawablePaddingTop;
        postInvalidate();

        return this;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public SuperTextView setFrameRate(int frameRate) {
        if (frameRate > 0) {
            this.frameRate = frameRate;
        } else {
            this.frameRate = 60;
        }
        return this;
    }

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
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressed = true;
                    postInvalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (pressed) {
                        if (!isInWidget(event)) {
                            pressed = false;
                            postInvalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    pressed = false;
                    postInvalidate();
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 事件是否在控件里面
     *
     * @param event 事件
     * @return
     */
    private boolean isInWidget(MotionEvent event) {
        if (event.getX() >= (-mTouchSlop) && event.getX() <= (getWidth() + mTouchSlop)
                && event.getY() >= (-mTouchSlop) && event.getY() <= (getHeight() + mTouchSlop)) {
            return true;
        }
        return false;
    }

    /**
     * 模拟发送Cancel事件
     *
     * @param x
     * @param y
     */
    private void simulateTouch(float x, float y) {

        final long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(
                downTime, downTime, MotionEvent.ACTION_CANCEL, x, y, 0);
        onTouchEvent(downEvent);
        downEvent.recycle();
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

    public static enum DrawableMode {
        LEFT(0), TOP(1), RIGHT(2), BOTTOM(3), CENTER(4), FILL(5), LEFT_TOP(6), RIGHT_TOP(7),
        LEFT_BOTTOM(8), RIGHT_BOTTOM(9);
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

    public static abstract class Adjuster {
        private Opportunity opportunity = Opportunity.BEFORE_TEXT;

        protected abstract void adjust(SuperTextView v, Canvas canvas);

        public boolean onTouch(SuperTextView v, MotionEvent event) {
            return false;
        }

        ;

        public Opportunity getOpportunity() {
            return opportunity;
        }

        public void setOpportunity(Opportunity opportunity) {
            this.opportunity = opportunity;
        }

        public static enum Opportunity {
            BEFORE_DRAWABLE, BEFORE_TEXT, AT_LAST
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
