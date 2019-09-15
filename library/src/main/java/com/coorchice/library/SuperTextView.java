

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


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.coorchice.library.gifdecoder.GifDecoder;
import com.coorchice.library.gifdecoder.GifDrawable;
import com.coorchice.library.image_engine.Engine;
import com.coorchice.library.sys_adjusters.PressAdjuster;
import com.coorchice.library.utils.STVUtils;
import com.coorchice.library.utils.track.Event;
import com.coorchice.library.utils.track.TimeEvent;
import com.coorchice.library.utils.track.Tracker;

import java.util.ArrayList;
import java.util.List;


public class SuperTextView extends TextView {

    /**
     * 标识不进行颜色的更改
     */
    public static final int NO_COLOR = -99;
    /**
     * 标识不进行旋转
     */
    public static final float NO_ROTATE = -1000;
    // Some Property Default Value
    private static final float DEFAULT_CORNER = 0f;
    private static final int DEFAULT_SOLID = Color.TRANSPARENT;
    private static final float DEFAULT_STROKE_WIDTH = 0f;
    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_STATE_DRAWABLE_MODE = DrawableMode.CENTER.code;
    private static final int DEFAULT_TEXT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXT_FILL_COLOR = Color.BLACK;
    private static final float DEFAULT_TEXT_STROKE_WIDTH = 0f;
    private static final int ALLOW_CUSTOM_ADJUSTER_SIZE = 3;
    private int SYSTEM_ADJUSTER_SIZE = 0;

    private float corner;
    private boolean leftTopCornerEnable;
    private boolean rightTopCornerEnable;
    private boolean leftBottomCornerEnable;
    private boolean rightBottomCornerEnable;
    private int solid;
    private float strokeWidth;
    private int strokeColor;
    private DrawableMode stateDrawableMode;
    private DrawableMode stateDrawable2Mode;
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
    private boolean cacheRunnableState, cacheDrawablePlaying, cacheDrawable2Playing;
    private boolean cacheNeedRunState;
    private int frameRate = 60;
    private Runnable invalidate;
    private int shaderStartColor;
    private int shaderEndColor;
    private ShaderMode shaderMode;
    private LinearGradient solidShader;
    private boolean shaderEnable;
    private int textShaderStartColor;
    private int textShaderEndColor;
    private ShaderMode textShaderMode;
    private boolean textShaderEnable;
    private LinearGradient textShader;
    private int pressBgColor = Color.TRANSPARENT;
    private int pressTextColor = NO_COLOR;
    private boolean drawableAsBackground;
    private BitmapShader drawableBackgroundShader;

    private List<Adjuster> adjusterList = new ArrayList<>();
    private List<Adjuster> touchAdjusters = new ArrayList<>();
    private Runnable handleAnim;
    private boolean superTouchEvent;
    private boolean drawable1Clicked = false;
    private boolean drawable2Clicked = false;
    private String curImageUrl;

    private int drawableTint = NO_COLOR;
    private float drawableRotate = NO_ROTATE;
    private int drawable2Tint = NO_COLOR;
    private float drawable2Rotate = NO_ROTATE;

    private OnDrawableClickedListener onDrawableClickedListener;

    private int[] suitedSize;
    private Canvas drawableBgCanvas, tempDrawableBgCanvas, drawable1Canvas, drawable2Canvas;
    private Bitmap drawableBgCanvasBitmap, tempDrawableBgCanvasBitmap, drawable1CanvasBitmap, drawable2CanvasBitmap;
    private ScaleType backgroundScaleType = ScaleType.CENTER;
    private Tracker tracker;

    /**
     * 简单的构造函数
     *
     * @param context View运行的Context环境
     */
    public SuperTextView(Context context) {
        super(context);
        init(null);
    }

    /**
     * inflate Xml布局文件时会被调用
     *
     * @param context View运行的Context环境
     * @param attrs   View在xml布局文件中配置的属性集合对象
     */
    public SuperTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * 略
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SuperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 略
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
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
            corner = typedArray.getDimension(R.styleable.SuperTextView_stv_corner, DEFAULT_CORNER);
            leftTopCornerEnable =
                    typedArray.getBoolean(R.styleable.SuperTextView_stv_left_top_corner, false);
            rightTopCornerEnable =
                    typedArray.getBoolean(R.styleable.SuperTextView_stv_right_top_corner, false);
            leftBottomCornerEnable =
                    typedArray.getBoolean(R.styleable.SuperTextView_stv_left_bottom_corner, false);
            rightBottomCornerEnable =
                    typedArray.getBoolean(R.styleable.SuperTextView_stv_right_bottom_corner, false);
            solid = typedArray.getColor(R.styleable.SuperTextView_stv_solid, DEFAULT_SOLID);
            strokeWidth = typedArray.getDimension(R.styleable.SuperTextView_stv_stroke_width,
                    DEFAULT_STROKE_WIDTH);
            strokeColor =
                    typedArray.getColor(R.styleable.SuperTextView_stv_stroke_color, DEFAULT_STROKE_COLOR);

            if (isInEditMode()) {
                drawable = typedArray.getDrawable(R.styleable.SuperTextView_stv_state_drawable);
                if (drawable != null) {
                    drawable = drawable.mutate();
                }
            } else {
                int drawableId = typedArray.getResourceId(R.styleable.SuperTextView_stv_state_drawable, 0);
                if (drawableId != 0) {
                    innerSetDrawable(drawableId);
                }
            }
            drawableWidth =
                    typedArray.getDimension(R.styleable.SuperTextView_stv_state_drawable_width, 0);
            drawableHeight =
                    typedArray.getDimension(R.styleable.SuperTextView_stv_state_drawable_height, 0);
            drawablePaddingLeft =
                    typedArray.getDimension(R.styleable.SuperTextView_stv_state_drawable_padding_left, 0);
            drawablePaddingTop =
                    typedArray.getDimension(R.styleable.SuperTextView_stv_state_drawable_padding_top, 0);
            drawableTint = typedArray.getColor(R.styleable.SuperTextView_stv_state_drawable_tint, NO_COLOR);
            drawableRotate = typedArray.getFloat(R.styleable.SuperTextView_stv_state_drawable_rotate, NO_ROTATE);

            if (isInEditMode()) {
                drawable2 = typedArray.getDrawable(R.styleable.SuperTextView_stv_state_drawable2);
                if (drawable2 != null) {
                    drawable2 = drawable2.mutate();
                }
            } else {
                int drawable2Id = typedArray.getResourceId(R.styleable.SuperTextView_stv_state_drawable2, 0);
                if (drawable2Id != 0) {
                    innerSetDrawable2(drawable2Id);
                }
            }
            drawable2Width =
                    typedArray.getDimension(R.styleable.SuperTextView_stv_state_drawable2_width, 0);
            drawable2Height =
                    typedArray.getDimension(R.styleable.SuperTextView_stv_state_drawable2_height, 0);
            drawable2PaddingLeft =
                    typedArray.getDimension(R.styleable.SuperTextView_stv_state_drawable2_padding_left, 0);
            drawable2PaddingTop =
                    typedArray.getDimension(R.styleable.SuperTextView_stv_state_drawable2_padding_top, 0);
            drawable2Tint = typedArray.getColor(R.styleable.SuperTextView_stv_state_drawable2_tint, NO_COLOR);
            drawable2Rotate = typedArray.getFloat(R.styleable.SuperTextView_stv_state_drawable2_rotate, NO_ROTATE);

            isShowState = typedArray.getBoolean(R.styleable.SuperTextView_stv_isShowState, false);
            drawableAsBackground =
                    typedArray.getBoolean(R.styleable.SuperTextView_stv_drawableAsBackground, false);
            backgroundScaleType = ScaleType.valueOf(typedArray.getInteger(R.styleable.SuperTextView_stv_scaleType, ScaleType.CENTER.code));
            isShowState2 = typedArray.getBoolean(R.styleable.SuperTextView_stv_isShowState2, false);
            stateDrawableMode = DrawableMode.valueOf(typedArray.getInteger(R.styleable.SuperTextView_stv_state_drawable_mode,
                    DEFAULT_STATE_DRAWABLE_MODE));
            stateDrawable2Mode = DrawableMode.valueOf(typedArray.getInteger(R.styleable.SuperTextView_stv_state_drawable2_mode,
                    DEFAULT_STATE_DRAWABLE_MODE));
            textStroke = typedArray.getBoolean(R.styleable.SuperTextView_stv_text_stroke, false);
            textStrokeColor = typedArray.getColor(R.styleable.SuperTextView_stv_text_stroke_color,
                    DEFAULT_TEXT_STROKE_COLOR);
            textFillColor = typedArray.getColor(R.styleable.SuperTextView_stv_text_fill_color,
                    DEFAULT_TEXT_FILL_COLOR);
            textStrokeWidth = typedArray.getDimension(R.styleable.SuperTextView_stv_text_stroke_width,
                    DEFAULT_TEXT_STROKE_WIDTH);
            autoAdjust = typedArray.getBoolean(R.styleable.SuperTextView_stv_autoAdjust, false);
            shaderStartColor =
                    typedArray.getColor(R.styleable.SuperTextView_stv_shaderStartColor, 0);
            shaderEndColor =
                    typedArray.getColor(R.styleable.SuperTextView_stv_shaderEndColor, 0);
            shaderMode = ShaderMode.valueOf(typedArray.getInteger(R.styleable.SuperTextView_stv_shaderMode, ShaderMode.TOP_TO_BOTTOM.code));
            shaderEnable = typedArray.getBoolean(R.styleable.SuperTextView_stv_shaderEnable, false);

            textShaderStartColor =
                    typedArray.getColor(R.styleable.SuperTextView_stv_textShaderStartColor, 0);
            textShaderEndColor =
                    typedArray.getColor(R.styleable.SuperTextView_stv_textShaderEndColor, 0);
            textShaderMode =
                    ShaderMode.valueOf(typedArray.getInteger(R.styleable.SuperTextView_stv_textShaderMode,
                            ShaderMode.TOP_TO_BOTTOM.code));
            textShaderEnable = typedArray.getBoolean(R.styleable.SuperTextView_stv_textShaderEnable, false);

            pressBgColor = typedArray.getColor(R.styleable.SuperTextView_stv_pressBgColor, Color.TRANSPARENT);
            pressTextColor = typedArray.getColor(R.styleable.SuperTextView_stv_pressTextColor, -99);
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
    public void invalidateDrawable(Drawable drawable) {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getVisibility() != VISIBLE || !isAttachedToWindow() || getWidth() < 0 || getHeight() < 0)
            return;
        long startDrawTime = System.currentTimeMillis();
        Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnDrawStart, startDrawTime));
        width = getWidth();
        height = getHeight();
        boolean needScroll = getScrollX() != 0 || getScrollY() != 0;
        if (needScroll) {
            canvas.translate(getScrollX(), getScrollY());
        }
        long startDrawStrokeTime = System.currentTimeMillis();
        drawStrokeLine(canvas);
        Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnDrawStrokeEnd, System.currentTimeMillis() - startDrawStrokeTime));
        long startDrawSolidTime = System.currentTimeMillis();
        drawSolid(canvas);
        Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnDrawSolidEnd, System.currentTimeMillis() - startDrawSolidTime));
        checkPressColor(canvas);
        isNeedToAdjust(canvas, Adjuster.Opportunity.BEFORE_DRAWABLE);
        long startDrawDrawableTime = System.currentTimeMillis();
        drawStateDrawable(canvas);
        Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnDrawDrawableEnd, System.currentTimeMillis() - startDrawDrawableTime));
        isNeedToAdjust(canvas, Adjuster.Opportunity.BEFORE_TEXT);
        if (needScroll) {
            canvas.translate(-getScrollX(), -getScrollY());
        }
        if (textStroke) {
            drawTextStroke(canvas);
        }
        if (textShaderEnable) {
            drawShaderText(canvas);
        } else {
            sdkOnDraw(canvas);
        }
        isNeedToAdjust(canvas, Adjuster.Opportunity.AT_LAST);
        Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnDrawEnd, System.currentTimeMillis() - startDrawTime));
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
            if (solidShader == null) {
                solidShader = createShader(shaderStartColor, shaderEndColor, shaderMode, 0, 0, width, height);
            }
            paint.setShader(solidShader);
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
     * 获取SuperTextView的详细圆角信息，共4个圆角，每个圆角包含两个值。
     *
     * @return 返回SuperTextView的圆角信息 {@link SuperTextView#getCorners(float)}.
     */
    public float[] getCorners() {
        return corners;
    }

    private void checkPressColor(Canvas canvas) {
        if (pressBgColor != Color.TRANSPARENT || pressTextColor != -99) {
            if (pressAdjuster == null) {
                pressAdjuster = new PressAdjuster(pressBgColor)
                        .setPressTextColor(pressTextColor);
                addSysAdjuster(pressAdjuster);
            }
            ((PressAdjuster) pressAdjuster).setPressTextColor(pressTextColor);
            ((PressAdjuster) pressAdjuster).setPressBgColor(pressBgColor);
        }
    }

    private LinearGradient createShader(int startColor, int endColor, ShaderMode shaderMode,
                                        float x0, float y0, float x1, float y1) {
        if (startColor != 0 && endColor != 0) {
            int temp = 0;
            switch (shaderMode) {
                case TOP_TO_BOTTOM:
                    x1 = x0;
                    break;
                case BOTTOM_TO_TOP:
                    x1 = x0;
                    temp = startColor;
                    startColor = endColor;
                    endColor = temp;
                    break;
                case LEFT_TO_RIGHT:
                    y1 = y0;
                    break;
                case RIGHT_TO_LEFT:
                    y1 = y0;
                    temp = startColor;
                    startColor = endColor;
                    endColor = temp;
                    break;
            }
            return new LinearGradient(x0, y0, x1, y1, startColor, endColor, Shader.TileMode.CLAMP);
        } else {
            return null;
        }
    }

    private void drawStateDrawable(Canvas canvas) {
        if (drawable != null) {
            if (drawableAsBackground) {
                long startDrawDrawableBackgroundTime = System.currentTimeMillis();
                drawDrawableBackground(canvas);
                Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnDrawDrawableBackgroundEnd, System.currentTimeMillis() - startDrawDrawableBackgroundTime));
            } else if (isShowState) {
                getDrawableBounds();
                drawable.setBounds((int) drawableBounds[0], (int) drawableBounds[1], (int) drawableBounds[2], (int) drawableBounds[3]);
                if (drawableTint != NO_COLOR) {
                    drawable.setColorFilter(drawableTint, PorterDuff.Mode.SRC_IN);
                }
                if (drawable instanceof GifDrawable) {
                    if (drawable1Canvas == null || drawable1Canvas.getWidth() != drawable.getIntrinsicWidth() || drawable1Canvas.getHeight() != drawable.getIntrinsicHeight()) {
                        if (drawable1Canvas != null) {
                            drawable1CanvasBitmap.recycle();
                            drawable1CanvasBitmap = null;
                            drawable1Canvas = null;
                        }
                        drawable1CanvasBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        drawable1Canvas = new Canvas(drawable1CanvasBitmap);
                    }
                    drawable.getBounds().offset(-(int) drawableBounds[0], -(int) drawableBounds[1]);
                    drawable.draw(drawable1Canvas);
                    drawable.getBounds().offset((int) drawableBounds[0], (int) drawableBounds[1]);
                }
                if (drawableRotate != NO_ROTATE) {
                    canvas.save();
                    canvas.rotate(drawableRotate,
                            drawableBounds[0] + (drawableBounds[2] - drawableBounds[0]) / 2,
                            drawableBounds[1] + (drawableBounds[3] - drawableBounds[1]) / 2);
                    if (drawable instanceof GifDrawable && drawable1CanvasBitmap != null) {
                        canvas.drawBitmap(drawable1CanvasBitmap, drawableBounds[0], drawableBounds[1], paint);
                    } else {
                        drawable.draw(canvas);
                    }
                    canvas.restore();
                } else {
                    if (drawable instanceof GifDrawable && drawable1CanvasBitmap != null) {
                        canvas.drawBitmap(drawable1CanvasBitmap, drawableBounds[0], drawableBounds[1], paint);
                    } else {
                        drawable.draw(canvas);
                    }
                }
            }
        }

        if (drawable2 != null && isShowState2) {
            getDrawable2Bounds();
            drawable2.setBounds((int) drawable2Bounds[0], (int) drawable2Bounds[1], (int) drawable2Bounds[2], (int) drawable2Bounds[3]);
            if (drawable2Tint != NO_COLOR) {
                drawable2.setColorFilter(drawable2Tint, PorterDuff.Mode.SRC_IN);
            }
            if (drawable2 instanceof GifDrawable) {
                if (drawable2Canvas == null || drawable2Canvas.getWidth() != drawable2.getIntrinsicWidth() || drawable2Canvas.getHeight() != drawable2.getIntrinsicHeight()) {
                    if (drawable2Canvas != null) {
                        drawable2CanvasBitmap.recycle();
                        drawable2CanvasBitmap = null;
                        drawable2Canvas = null;
                    }
                    drawable2CanvasBitmap = Bitmap.createBitmap(drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    drawable2Canvas = new Canvas(drawable2CanvasBitmap);
                }
                drawable2.getBounds().offset(-(int) drawable2Bounds[0], -(int) drawable2Bounds[1]);
                drawable2.draw(drawable2Canvas);
                drawable2.getBounds().offset((int) drawable2Bounds[0], (int) drawable2Bounds[1]);
            }
            if (drawable2Rotate != NO_ROTATE) {
                canvas.save();
                canvas.rotate(drawable2Rotate,
                        drawable2Bounds[0] + (drawable2Bounds[2] - drawable2Bounds[0]) / 2,
                        drawable2Bounds[1] + (drawable2Bounds[3] - drawable2Bounds[1]) / 2);
                if (drawable2 instanceof GifDrawable && drawable2CanvasBitmap != null) {
                    canvas.drawBitmap(drawable2CanvasBitmap, drawable2Bounds[0], drawable2Bounds[1], paint);
                } else {
                    drawable2.draw(canvas);
                }
                canvas.restore();
            } else {
                if (drawable2 instanceof GifDrawable && drawable2CanvasBitmap != null) {
                    canvas.drawBitmap(drawable2CanvasBitmap, drawable2Bounds[0], drawable2Bounds[1], paint);
                } else {
                    drawable2.draw(canvas);
                }
            }
        }
    }

    private void drawDrawableBackground(Canvas canvas) {
        long startCreateDrawableBackgroundShaderTime = System.currentTimeMillis();
        boolean needCopyDrawableToShader = false;
        if (drawableBackgroundShader == null) {
            if (!(drawable.getIntrinsicHeight() > 0)
                    || !(drawable.getIntrinsicWidth() > 0)) {
                drawable.setBounds(0, 0, width, height);
            }
            int[] size = computeSuitedBitmapSize(drawable);
            if (backgroundScaleType == ScaleType.FIT_CENTER) {
                if (drawableBgCanvas == null
                        || (drawableBgCanvas.getWidth() != width || drawableBgCanvas.getHeight() != height)) {
                    if (drawableBgCanvasBitmap != null) {
                        drawableBgCanvasBitmap.recycle();
                        drawableBgCanvasBitmap = null;
                        drawableBgCanvas = null;
                    }
                    if (tempDrawableBgCanvasBitmap != null) {
                        tempDrawableBgCanvasBitmap.recycle();
                        tempDrawableBgCanvasBitmap = null;
                        tempDrawableBgCanvas = null;
                    }
                    drawableBgCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    drawableBgCanvas = new Canvas(drawableBgCanvasBitmap);
                    tempDrawableBgCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    tempDrawableBgCanvas = new Canvas(tempDrawableBgCanvasBitmap);
                }
            } else {
                if (drawableBgCanvas == null
                        || (drawableBgCanvas.getWidth() != size[0] || drawableBgCanvas.getHeight() != size[1])) {
                    if (drawableBgCanvasBitmap != null) {
                        drawableBgCanvasBitmap.recycle();
                        drawableBgCanvasBitmap = null;
                        drawableBgCanvas = null;
                    }
                    if (tempDrawableBgCanvasBitmap != null) {
                        tempDrawableBgCanvasBitmap.recycle();
                        tempDrawableBgCanvasBitmap = null;
                        tempDrawableBgCanvas = null;
                    }
                    drawableBgCanvasBitmap = Bitmap.createBitmap(size[0], size[1], Bitmap.Config.ARGB_8888);
                    drawableBgCanvas = new Canvas(drawableBgCanvasBitmap);
                }
            }
            drawableBgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (tempDrawableBgCanvas != null) {
                tempDrawableBgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            drawableBackgroundShader = new BitmapShader(drawableBgCanvasBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            needCopyDrawableToShader = true;
        }
        Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnCreateDrawableBackgroundShaderEnd, System.currentTimeMillis() - startCreateDrawableBackgroundShaderTime));
        long startUpdateDrawableBackgroundShaderTime = System.currentTimeMillis();
        if (drawableBgCanvas != null && (needCopyDrawableToShader || drawable instanceof GifDrawable)) {
            Rect orgBounds = drawable.getBounds();
            drawable.setBounds(suitedSize[2], suitedSize[3], suitedSize[2] + suitedSize[0], suitedSize[3] + suitedSize[1]);
            long startCopyDrawableBackgroundToShaderTime = System.currentTimeMillis();
            if (backgroundScaleType == ScaleType.FIT_CENTER && tempDrawableBgCanvas != null) {
                tempDrawableBgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                drawable.draw(tempDrawableBgCanvas);
                int color = paint.getColor();
                paint.setColor(Color.WHITE);
                drawableBgCanvas.drawBitmap(tempDrawableBgCanvasBitmap, 0, 0, paint);
                paint.setColor(color);
            } else {
                drawable.draw(drawableBgCanvas);
            }
            Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnCopyDrawableBackgroundToShaderEnd, System.currentTimeMillis() - startCopyDrawableBackgroundToShaderTime));
            drawable.setBounds(orgBounds);
        }
        Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnUpdateDrawableBackgroundShaderEnd, System.currentTimeMillis() - startUpdateDrawableBackgroundShaderTime));
        long startDrawDrawableBackgroundShaderTime = System.currentTimeMillis();
        if (drawableBackgroundShader != null) {
            Shader shader = paint.getShader();
            int color = paint.getColor();
            paint.setColor(Color.WHITE);
            paint.setShader(drawableBackgroundShader);
            canvas.drawPath(solidPath, paint);
            paint.setShader(shader);
            paint.setColor(color);
        }
        Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnDrawDrawableBackgroundShaderEnd, System.currentTimeMillis() - startDrawDrawableBackgroundShaderTime));
    }

    private int[] computeSuitedBitmapSize(Drawable drawable) {
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        if (!(dWidth > 0)) {
            dWidth = width;
        }
        if (!(dHeight > 0)) {
            dHeight = height;
        }
        int suitedWidth = width;
        int suitedHeight = height;
        if (suitedSize == null) {
            suitedSize = new int[4];
        }
        if (backgroundScaleType == ScaleType.FIT_CENTER) {
            if ((float) dWidth / (float) width > (float) dHeight / (float) height) {
                suitedHeight = (int) ((float) suitedWidth / ((float) dWidth / (float) dHeight));
            } else {
                suitedWidth = (int) (((float) dWidth / (float) dHeight) * (float) suitedHeight);
            }
            suitedSize[0] = suitedWidth;
            suitedSize[1] = suitedHeight;
            suitedSize[2] = width / 2 - suitedSize[0] / 2;
            suitedSize[3] = height / 2 - suitedSize[1] / 2;
        } else if (backgroundScaleType == ScaleType.FIT_XY) {
            suitedSize[0] = suitedWidth;
            suitedSize[1] = suitedHeight;
            suitedSize[2] = 0;
            suitedSize[3] = 0;
        } else {
            if ((float) dWidth / (float) width > (float) dHeight / (float) height) {
                suitedWidth = (int) (((float) dWidth / (float) dHeight) * (float) suitedHeight);
            } else {
                suitedHeight = (int) ((float) suitedWidth / ((float) dWidth / (float) dHeight));
            }
            suitedSize[0] = suitedWidth;
            suitedSize[1] = suitedHeight;
            suitedSize[2] = -(suitedSize[0] / 2 - width / 2);
            suitedSize[3] = -(suitedSize[1] / 2 - height / 2);
        }
        return suitedSize;
    }

    private float[] getDrawableBounds() {
        for (int i = 0; i < drawableBounds.length; i++) {
            drawableBounds[i] = 0;
        }
        drawableWidth = (drawableWidth == 0 ? width / 2f : drawableWidth);
        drawableHeight = (drawableHeight == 0 ? height / 2f : drawableHeight);
        switch (stateDrawableMode) {
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
        switch (stateDrawable2Mode) {
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
                long startDrawAdjustersTime = System.currentTimeMillis();
                if (adjuster.getType() == Adjuster.TYPE_SYSTEM) {
                    adjuster.adjust(this, canvas);
                } else if (autoAdjust) {
                    adjuster.adjust(this, canvas);
                }
                Tracker.notifyEvent(tracker, TimeEvent.create(Event.OnDrawAdjustersEnd, System.currentTimeMillis() - startDrawAdjustersTime));
            }
        }
    }


    private void drawTextStroke(Canvas canvas) {
        getPaint().setStyle(Paint.Style.STROKE);
        setTextColor(textStrokeColor);
        getPaint().setFakeBoldText(true);
        getPaint().setStrokeWidth(textStrokeWidth);
        sdkOnDraw(canvas);
        getPaint().setStyle(Paint.Style.FILL);
        getPaint().setFakeBoldText(false);
        setTextColor(textFillColor);
    }

    private void drawShaderText(Canvas canvas) {
        Shader tempShader = getPaint().getShader();
        if (getLayout() != null && getLayout().getLineCount() > 0) {
            float x0 = getLayout().getLineLeft(0);
            int y0 = getLayout().getLineTop(0);
            float x1 = x0 + getLayout().getLineWidth(0);
            float y1 = y0 + getLayout().getHeight();
            if (getLayout().getLineCount() > 1) {
                for (int i = 1; i < getLayout().getLineCount(); i++) {
                    if (x0 > getLayout().getLineLeft(i)) {
                        x0 = getLayout().getLineLeft(i);
                    }
                    if (x1 < x0 + getLayout().getLineWidth(i)) {
                        x1 = x0 + getLayout().getLineWidth(i);
                    }
                }
            }
            if (textShader == null) {
                textShader = createShader(textShaderStartColor, textShaderEndColor, textShaderMode,
                        x0, y0, x1, y1);
            }
            getPaint().setShader(textShader);
            sdkOnDraw(canvas);
        }
        getPaint().setShader(tempShader);
    }

    @SuppressLint("WrongCall")
    private void sdkOnDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 获取SuperTextView的圆角大小
     *
     * @return 获取Corner值。默认为0。
     */
    public float getCorner() {
        return corner;
    }


    /**
     * 设置圆角大小
     *
     * @param corner 圆角大小，默认值为0。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setCorner(float corner) {
        this.corner = corner;
        postInvalidate();

        return this;
    }

    /**
     * 获取填充颜色
     *
     * @return 返回控件填充颜色。
     */
    public int getSolid() {
        return solid;
    }

    /**
     * 设置填充颜色
     *
     * @param solid 控件填充颜色, 默认为{@link Color#TRANSPARENT}。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setSolid(int solid) {
        this.solid = solid;
        postInvalidate();

        return this;
    }

    /**
     * 获取控件边框宽度
     *
     * @return 返回控件边框的宽度。
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * 设置控件边框宽度
     *
     * @param strokeWidth 描边宽度。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        postInvalidate();

        return this;
    }

    /**
     * 获取边框颜色
     *
     * @return 返回描边颜色。默认为{@link Color#BLACK}。
     */
    public int getStrokeColor() {
        return strokeColor;
    }

    /**
     * 设置边框颜色
     *
     * @param strokeColor 描边颜色。默认为{@link Color#BLACK}。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        postInvalidate();

        return this;
    }

    /**
     * 获取最后一个 {@link Adjuster}
     *
     * @return 获得最后一个 {@link Adjuster}，如果存在的话。
     */
    public Adjuster getAdjuster() {
        if (adjusterList.size() > SYSTEM_ADJUSTER_SIZE) {
            return adjusterList.get(adjusterList.size() - 1);
        }
        return null;
    }

    /**
     * 获得index对应的 {@link Adjuster}。
     *
     * @param index 期望获得的Adjuster的index。
     * @return index对应的Adjuster，如果参数错误返回null。
     */
    public Adjuster getAdjuster(int index) {
        int realIndex = SYSTEM_ADJUSTER_SIZE + index;
        if (realIndex > SYSTEM_ADJUSTER_SIZE - 1 && realIndex < adjusterList.size()) {
            return adjusterList.get(realIndex);
        }
        return null;
    }

    /**
     * 获得SuperTextView中的所有Adjuster，如果没有返回null
     *
     * @return 如果SuperTextView有Adjuster，返回List<Adjuster>；否则返回null
     */
    public List<Adjuster> getAdjusterList() {
        if (adjusterList.size() > SYSTEM_ADJUSTER_SIZE) {
            ArrayList<Adjuster> r = new ArrayList<>();
            r.addAll(SYSTEM_ADJUSTER_SIZE, adjusterList);
            return r;
        }
        return null;
    }

    /**
     * 添加一个Adjuster。
     * 注意，最多支持添加3个Adjuster，否则新的Adjuster总是会覆盖最后一个Adjuster。
     *
     * @param adjuster {@link Adjuster}。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView addAdjuster(Adjuster adjuster) {
        if (adjusterList.size() < SYSTEM_ADJUSTER_SIZE + ALLOW_CUSTOM_ADJUSTER_SIZE) {
            innerAddAdjuster(adjuster);
        } else {
            removeAdjuster(adjusterList.size() - 1);
            innerAddAdjuster(adjuster);
        }
        return this;
    }

    private void addSysAdjuster(Adjuster adjuster) {
        if (adjuster != null) {
            adjuster.setType(Adjuster.TYPE_SYSTEM);
            adjusterList.add(SYSTEM_ADJUSTER_SIZE, adjuster);
            SYSTEM_ADJUSTER_SIZE++;
        }
    }

    private void innerAddAdjuster(Adjuster adjuster) {
        adjusterList.add(adjuster);
        adjuster.attach(this);
        postInvalidate();
    }

    /**
     * 移除指定位置的Adjuster。
     *
     * @param index 期望移除的Adjuster的位置。
     * @return 被移除的Adjuster，如果参数错误返回null。
     */
    public Adjuster removeAdjuster(int index) {
        int realIndex = SYSTEM_ADJUSTER_SIZE + index;
        if (realIndex > SYSTEM_ADJUSTER_SIZE - 1 && realIndex < adjusterList.size()) {
            Adjuster remove = adjusterList.remove(realIndex);
            remove.detach(this);
            postInvalidate();
            return remove;
        }
        return null;
    }

    /**
     * 移除指定的Adjuster，如果包含的话。
     *
     * @param adjuster 需要被移除的Adjuster
     * @return 被移除Adjuster在移除前在Adjuster列表中的位置。如果没有包含，返回-1。
     */
    public int removeAdjuster(Adjuster adjuster) {
        if (adjuster.type != Adjuster.TYPE_SYSTEM && adjusterList.contains(adjuster)) {
            int index = adjusterList.indexOf(adjuster);
            adjusterList.remove(adjuster);
            adjuster.detach(this);
            postInvalidate();
            return index;
        }
        return -1;
    }

    /**
     * 检查是否开启了文字描边
     *
     * @return true 表示开启了文字描边，否则表示没开启。
     */
    public boolean isTextStroke() {
        return textStroke;
    }

    /**
     * 设置是否开启文字描边。
     * 注意，开启文字描边后，文字颜色需要通过 {@link #setTextFillColor(int)} 设置。
     *
     * @param textStroke true表示开启文字描边。默认为false。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setTextStroke(boolean textStroke) {
        this.textStroke = textStroke;
        postInvalidate();

        return this;
    }

    /**
     * 获取文字描边的颜色
     *
     * @return 文字描边的颜色。
     */
    public int getTextStrokeColor() {
        return textStrokeColor;
    }

    /**
     * 设置文字描边的颜色
     *
     * @param textStrokeColor 设置文字描边的颜色。默认为{@link Color#BLACK}。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setTextStrokeColor(int textStrokeColor) {
        this.textStrokeColor = textStrokeColor;
        postInvalidate();

        return this;
    }

    /**
     * 获取文字的填充颜色，在开启文字描边时 {@link #setTextStroke(boolean)} 默认为BLACK。
     *
     * @return 文字填充颜色。
     */
    public int getTextFillColor() {
        return textFillColor;
    }

    /**
     * 设置文字的填充颜色，需要开启文字描边 {@link #setTextStroke(boolean)} 才能生效。默认为BLACK。
     *
     * @param textFillColor 设置文字填充颜色。默认为{@link Color#BLACK}。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setTextFillColor(int textFillColor) {
        this.textFillColor = textFillColor;
        postInvalidate();

        return this;
    }

    /**
     * 获取文字描边的宽度
     *
     * @return 文字描边宽度。
     */
    public float getTextStrokeWidth() {
        return textStrokeWidth;
    }

    /**
     * 设置文字描边的宽度，需要开启文字描边 {@link #setTextStroke(boolean)} 才能生效。
     *
     * @param textStrokeWidth 设置文字描边宽度。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setTextStrokeWidth(float textStrokeWidth) {
        this.textStrokeWidth = textStrokeWidth;
        postInvalidate();

        return this;
    }

    /**
     * 检查是否开启 {@link Adjuster} 功能。
     *
     * @return true表示开启了Adjuster功能。
     */
    public boolean isAutoAdjust() {
        return autoAdjust;
    }

    /**
     * 设置是否开启 {@link Adjuster} 功能。
     *
     * @param autoAdjust true开启Adjuster功能。反之，关闭。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setAutoAdjust(boolean autoAdjust) {
        this.autoAdjust = autoAdjust;
        postInvalidate();

        return this;
    }

    /**
     * 检查左上角是否设置成了圆角。
     *
     * @return true表示左上角为圆角。
     */
    public boolean isLeftTopCornerEnable() {
        return leftTopCornerEnable;
    }

    /**
     * 设置左上角是否为圆角，可以单独控制SuperTextView的每一个圆角。
     *
     * @param leftTopCornerEnable true左上角圆角化。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setLeftTopCornerEnable(boolean leftTopCornerEnable) {
        this.leftTopCornerEnable = leftTopCornerEnable;
        postInvalidate();

        return this;
    }

    /**
     * 检查右上角是否设置成了圆角。
     *
     * @return true表示右上角为圆角。
     */
    public boolean isRightTopCornerEnable() {
        return rightTopCornerEnable;
    }

    /**
     * 设置右上角是否为圆角，可以单独控制SuperTextView的每一个圆角。
     *
     * @param rightTopCornerEnable true右上角圆角化。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setRightTopCornerEnable(boolean rightTopCornerEnable) {
        this.rightTopCornerEnable = rightTopCornerEnable;
        postInvalidate();

        return this;
    }

    /**
     * 检查左下角是否设置成了圆角。
     *
     * @return true表示左下角为圆角。
     */
    public boolean isLeftBottomCornerEnable() {
        return leftBottomCornerEnable;
    }

    /**
     * 设置左下角是否为圆角，可以单独控制SuperTextView的每一个圆角。
     *
     * @param leftBottomCornerEnable true左下角圆角化。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setLeftBottomCornerEnable(boolean leftBottomCornerEnable) {
        this.leftBottomCornerEnable = leftBottomCornerEnable;
        postInvalidate();

        return this;
    }

    /**
     * 检查右下角是否设置成了圆角。
     *
     * @return true表示右下角为圆角。
     */
    public boolean isRightBottomCornerEnable() {
        return rightBottomCornerEnable;
    }

    /**
     * 设置右下角是否为圆角，可以单独控制SuperTextView的每一个圆角。
     *
     * @param rightBottomCornerEnable true右下角圆角化。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setRightBottomCornerEnable(boolean rightBottomCornerEnable) {
        this.rightBottomCornerEnable = rightBottomCornerEnable;
        postInvalidate();

        return this;
    }

    /**
     * 获取状态图，当状态图通过 {@link #setDrawableAsBackground(boolean)} 设置为背景时，获取的就是背景图了。
     *
     * @return 状态图。
     */
    public Drawable getDrawable() {
        return drawable;
    }

    /**
     * 获取状态图2号。
     *
     * @return 状态图2。
     */
    public Drawable getDrawable2() {
        return drawable2;
    }

    /**
     * 设置状态图。需要调用 {@link #setShowState(boolean)} 才能显示。会触发一次重绘。
     * 当通过 {@link #setDrawableAsBackground(boolean)} 将状态图设置为背景后，将会被作为SuperTextView的背景图。
     * 通过 {@link #isDrawableAsBackground()} 来检查状态图是否被设置成了背景。
     *
     * @param drawable 状态图。
     * @return SuperTextView
     */
    public SuperTextView setDrawable(Drawable drawable) {
        Drawable temp = this.drawable;
        this.drawable = drawable;
        this.drawable.setCallback(this);
        drawableBackgroundShader = null;
        postInvalidate();
        resetGifDrawable(temp);
        return this;
    }

    /**
     * 设置状态图2。需要调用{@link #setShowState2(boolean)}才能显示。会触发一次重绘。
     *
     * @param drawable 状态图
     * @return SuperTextView
     */
    public SuperTextView setDrawable2(Drawable drawable) {
        Drawable temp = this.drawable2;
        this.drawable2 = drawable;
        this.drawable2.setCallback(this);
        postInvalidate();
        resetGifDrawable(temp);
        return this;
    }


    private byte[] getResBytes(int drawableRes) {
        return STVUtils.getResBytes(getContext(), drawableRes);
    }

    /**
     * 使用drawable资源设置状态图。需要调用{@link #setShowState(boolean)}才能显示。会触发一次重绘。
     *
     * @param drawableRes 状态图的资源id
     * @return SuperTextView
     */
    public SuperTextView setDrawable(int drawableRes) {
        byte[] bytes = getResBytes(drawableRes);
        if (bytes != null && GifDecoder.isGif(bytes)) {
            return setDrawable(GifDrawable.createDrawable(bytes));
        }
        return setDrawable(getResources().getDrawable(drawableRes).mutate());
    }

    private SuperTextView innerSetDrawable(int drawableRes) {
        try {
            byte[] bytes = getResBytes(drawableRes);
            if (bytes != null && GifDecoder.isGif(bytes)) {
                drawable = GifDrawable.createDrawable(bytes);
                drawable.setCallback(this);
            } else {
                drawable = getResources().getDrawable(drawableRes).mutate();
            }
        } catch (Exception ignored) {

        }
        return this;
    }

    /**
     * 使用drawable资源设置状态图2。需要调用{@link #setShowState2(boolean)}才能显示。会触发一次重绘。
     *
     * @param drawableRes 状态图的资源id
     * @return SuperTextView
     */
    public SuperTextView setDrawable2(int drawableRes) {
        byte[] bytes = getResBytes(drawableRes);
        if (bytes != null && GifDecoder.isGif(bytes)) {
            return setDrawable2(GifDrawable.createDrawable(bytes));
        }
        return setDrawable2(getResources().getDrawable(drawableRes).mutate());
    }

    private SuperTextView innerSetDrawable2(int drawableRes) {
        try {
            byte[] bytes = getResBytes(drawableRes);
            if (bytes != null && GifDecoder.isGif(bytes)) {
                drawable2 = GifDrawable.createDrawable(bytes);
                drawable2.setCallback(this);
            } else {
                drawable2 = getResources().getDrawable(drawableRes).mutate();
            }
        } catch (Exception e) {
        }
        return this;
    }

    private void resetGifDrawable(Drawable drawable) {
        if (drawable != null) drawable.setCallback(null);
        if (drawable instanceof GifDrawable) {
            ((GifDrawable) drawable).stop();
            ((GifDrawable) drawable).destroy();
        }
    }

    /**
     * 使用bitmap资源设置状态图1。需要调用{@link #setShowState(boolean)}才能显示。会触发一次重绘。
     *
     * @param bitmap 要设置的图片
     * @return
     */
    public SuperTextView setDrawable(Bitmap bitmap) {
        return setDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    /**
     * 使用bitmap资源设置状态图2。需要调用{@link #setShowState2(boolean)}才能显示。会触发一次重绘。
     *
     * @param bitmap 要设置的图片
     * @return
     */
    public SuperTextView setDrawable2(Bitmap bitmap) {
        return setDrawable2(new BitmapDrawable(getResources(), bitmap));
    }

    /**
     * 检查是否显示状态图
     *
     * @return 返回true，如果当前显示状态图。
     */
    public boolean isShowState() {
        return isShowState;
    }

    /**
     * 设置是否显示状态图。
     *
     * @param showState true，表示显示状态图。反之，不显示。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setShowState(boolean showState) {
        isShowState = showState;
        postInvalidate();

        return this;
    }

    /**
     * 检查当前状态图是否被作为SuperTextView的背景图。
     *
     * @return 当前Drawable是否作为SuperTextView的背景图。
     */
    public boolean isDrawableAsBackground() {
        return drawableAsBackground;
    }


    /**
     * 设置是否将状态图作为SuperTextView的背景图。
     * 将状态图设置为背景图，可以将SuperTextView变成一个ImageView用来展示图片，对SuperTextView设置的圆角、边框仍然对图片
     * 有效，这对于需要实现圆形图片、给图片加边框很有帮助。而且通过 {@link #setUrlImage(String)} 和 {@link #setUrlImage(String, boolean)}
     * 可以使得SuperTextView能够自动下载网络图片，然后进行展示。
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
     * 检查是否显示状态图2。
     *
     * @return 返回true，如果当前显示状态图2。
     */
    public boolean isShowState2() {
        return isShowState2;
    }


    /**
     * 设置是否显示状态图2
     *
     * @param showState true，表示显示状态图2。反之，不显示。会触发一次重绘。
     * @return SuperTextView
     */
    public SuperTextView setShowState2(boolean showState) {
        isShowState2 = showState;
        postInvalidate();

        return this;
    }

    /**
     * 获取状态图的显示模式。在 {@link DrawableMode} 中查看所有支持的模式。
     *
     * @return 状态图显示模式。{@link DrawableMode}
     */
    public DrawableMode getStateDrawableMode() {
        return stateDrawableMode;
    }

    /**
     * 获取状态图2的显示模式。在 {@link DrawableMode} 中查看所有支持的模式。
     *
     * @return 状态图2显示模式。{@link DrawableMode}
     */
    public DrawableMode getStateDrawable2Mode() {
        return stateDrawable2Mode;
    }

    /**
     * 设置状态图显示模式。默认为{@link DrawableMode#CENTER}。会触发一次重绘。
     * 在 {@link DrawableMode} 中查看所有支持的模式。
     *
     * @param stateDrawableMode 状态图显示模式
     * @return SuperTextView
     */
    public SuperTextView setStateDrawableMode(DrawableMode stateDrawableMode) {
        this.stateDrawableMode = stateDrawableMode;
        postInvalidate();

        return this;
    }

    /**
     * 设置状态图2显示模式。默认为{@link DrawableMode#CENTER}。会触发一次重绘。
     * 在 {@link DrawableMode} 中查看所有支持的模式。
     *
     * @param stateDrawableMode 状态图2显示模式
     * @return SuperTextView
     */
    public SuperTextView setStateDrawable2Mode(DrawableMode stateDrawableMode) {
        this.stateDrawable2Mode = stateDrawableMode;
        postInvalidate();

        return this;
    }

    /**
     * 获取状态图的宽度。
     *
     * @return 状态图的宽度。
     */
    public float getDrawableWidth() {
        return drawableWidth;
    }

    /**
     * 获取状态图2的宽度。
     *
     * @return 状态图2的宽度。
     */
    public float getDrawable2Width() {
        return drawable2Width;
    }

    /**
     * 设置状态图宽度。默认为控件的1／2。会触发一次重绘。
     *
     * @param drawableWidth 状态图宽度
     * @return SuperTextView
     */
    public SuperTextView setDrawableWidth(float drawableWidth) {
        this.drawableWidth = drawableWidth;
        postInvalidate();

        return this;
    }

    /**
     * 设置状态图2宽度。默认为控件的1／2。会触发一次重绘。
     *
     * @param drawableWidth 状态图2宽度
     * @return SuperTextView
     */
    public SuperTextView setDrawable2Width(float drawableWidth) {
        this.drawable2Width = drawableWidth;
        postInvalidate();

        return this;
    }

    /**
     * 获取状态图的高度
     *
     * @return 状态图的高度。
     */
    public float getDrawableHeight() {
        return drawableHeight;
    }

    /**
     * 获取状态图2的高度
     *
     * @return 状态图2的高度。
     */
    public float getDrawable2Height() {
        return drawable2Height;
    }

    /**
     * 设置状态图高度。默认为控件的1／2。会触发一次重绘。
     *
     * @param drawableHeight 状态图高度
     * @return SuperTextView
     */
    public SuperTextView setDrawableHeight(float drawableHeight) {
        this.drawableHeight = drawableHeight;
        postInvalidate();

        return this;
    }

    /**
     * 设置状态图2高度。默认为控件的1／2。会触发一次重绘。
     *
     * @param drawableHeight 状态图2高度
     * @return SuperTextView
     */
    public SuperTextView setDrawable2Height(float drawableHeight) {
        this.drawable2Height = drawableHeight;
        postInvalidate();

        return this;
    }

    /**
     * 获取状态图相对于控件左边的边距。
     *
     * @return 状态图左边距
     */
    public float getDrawablePaddingLeft() {
        return drawablePaddingLeft;
    }

    /**
     * 获取状态图2相对于控件左边的边距。
     *
     * @return 状态图2左边距
     */
    public float getDrawable2PaddingLeft() {
        return drawable2PaddingLeft;
    }

    /**
     * 设置状态图相对于控件左边的边距。会触发一次重绘。
     *
     * @param drawablePaddingLeft 状态图左边距。
     * @return SuperTextView
     */
    public SuperTextView setDrawablePaddingLeft(float drawablePaddingLeft) {
        this.drawablePaddingLeft = drawablePaddingLeft;
        postInvalidate();

        return this;
    }

    /**
     * 设置状态图2相对于控件左边的边距。会触发一次重绘。
     *
     * @param drawablePaddingLeft 状态图左边距。
     * @return SuperTextView
     */
    public SuperTextView setDrawable2PaddingLeft(float drawablePaddingLeft) {
        this.drawable2PaddingLeft = drawablePaddingLeft;
        postInvalidate();

        return this;
    }

    /**
     * 获取状态图相对于控件上边的边距。
     *
     * @return 状态图上边距。
     */
    public float getDrawablePaddingTop() {
        return drawablePaddingTop;
    }

    /**
     * 获取状态图2相对于控件上边的边距。
     *
     * @return 状态图2上边距。
     */
    public float getDrawable2PaddingTop() {
        return drawable2PaddingTop;
    }

    /**
     * 设置状态图相对于控件上边的边距。会触发一次重绘。
     *
     * @param drawablePaddingTop 状态图上边距。
     * @return SuperTextView
     */
    public SuperTextView setDrawablePaddingTop(float drawablePaddingTop) {
        this.drawablePaddingTop = drawablePaddingTop;
        postInvalidate();
        return this;
    }

    /**
     * 设置状态图2相对于控件上边的边距。会触发一次重绘。
     *
     * @param drawablePaddingTop 状态图2上边距。
     * @return SuperTextView
     */
    public SuperTextView setDrawable2PaddingTop(float drawablePaddingTop) {
        this.drawable2PaddingTop = drawablePaddingTop;
        postInvalidate();
        return this;
    }

    /**
     * 设置第一个状态图的混合颜色。可以修改原本的drawable的颜色。
     * <p>
     * 如果需要还原为原来的颜色只需要设置颜色为 {@link SuperTextView#NO_COLOR}.
     *
     * @param tintColor 目标混合颜色
     * @return
     */
    public SuperTextView setDrawableTint(int tintColor) {
        this.drawableTint = tintColor;
        postInvalidate();
        return this;
    }

    /**
     * 获得第一个状态图的混合颜色。
     * <p>
     * 默认为 {@link SuperTextView#NO_COLOR}
     *
     * @return
     */
    public int getDrawableTint() {
        return drawableTint;
    }


    /**
     * 设置第二个状态图的混合颜色。可以修改原本的drawable的颜色。
     * <p>
     * 如果需要还原为原来的颜色只需要设置颜色为 {@link SuperTextView#NO_COLOR}.
     *
     * @param tintColor 目标混合颜色
     * @return
     */
    public SuperTextView setDrawable2Tint(int tintColor) {
        this.drawable2Tint = tintColor;
        postInvalidate();
        return this;
    }

    /**
     * 获得第二个状态图的混合颜色。
     * <p>
     * 默认为 {@link SuperTextView#NO_COLOR}
     *
     * @return
     */
    public int getDrawable2Tint() {
        return drawable2Tint;
    }

    /**
     * 设置第一个状态图的旋转角度。
     * <p>
     * 如果需要恢复默认角度只需要设置为 {@link SuperTextView#NO_ROTATE}.
     *
     * @param rotate 需要旋转的角度
     * @return
     */
    public SuperTextView setDrawableRotate(float rotate) {
        this.drawableRotate = rotate;
        postInvalidate();
        return this;
    }

    /**
     * 获得第一个状态图的旋转角度。
     * <p>
     * 默认为 {@link SuperTextView#NO_ROTATE}
     *
     * @return
     */
    public float getDrawableRotate() {
        return drawableRotate;
    }

    /**
     * 设置第二个状态图的旋转角度。
     * <p>
     * 如果需要恢复默认角度只需要设置为 {@link SuperTextView#NO_ROTATE}.
     *
     * @param rotate 需要旋转的角度
     * @return
     */
    public SuperTextView setDrawable2Rotate(float rotate) {
        this.drawable2Rotate = rotate;
        postInvalidate();
        return this;
    }

    /**
     * 获得第二个状态图的旋转角度。
     * <p>
     * 默认为 {@link SuperTextView#NO_ROTATE}
     *
     * @return
     */
    public float getDrawable2Rotate() {
        return drawable2Rotate;
    }


    /**
     * 获取渐变色的起始颜色。
     *
     * @return 渐变起始色。
     */
    public int getShaderStartColor() {
        return shaderStartColor;
    }

    /**
     * 设置渐变起始色。需要调用{@link SuperTextView#setShaderEnable(boolean)}后才能生效。会触发一次重绘。
     *
     * @param shaderStartColor 渐变起始色
     * @return SuperTextView
     */
    public SuperTextView setShaderStartColor(int shaderStartColor) {
        this.shaderStartColor = shaderStartColor;
        solidShader = null;
        postInvalidate();
        return this;
    }

    /**
     * 获取渐变色的结束颜色。
     *
     * @return 渐变结束色。
     */
    public int getShaderEndColor() {
        return shaderEndColor;
    }

    /**
     * 设置渐变结束色。需要调用{@link SuperTextView#setShaderEnable(boolean)}后才能生效。会触发一次重绘。
     *
     * @param shaderEndColor 渐变结束色
     * @return SuperTextView
     */
    public SuperTextView setShaderEndColor(int shaderEndColor) {
        this.shaderEndColor = shaderEndColor;
        solidShader = null;
        postInvalidate();
        return this;
    }

    /**
     * 获取渐变色模式。在{@link ShaderMode}中可以查看所有支持的模式。
     * 需要调用{@link SuperTextView#setShaderEnable(boolean)}后才能生效。
     *
     * @return 渐变模式。
     */
    public ShaderMode getShaderMode() {
        return shaderMode;
    }

    /**
     * 设置渐变模式。在{@link ShaderMode}中可以查看所有支持的模式。
     * 需要调用{@link SuperTextView#setShaderEnable(boolean)}后才能生效。
     *
     * @param shaderMode 渐变模式
     * @return SuperTextView
     */
    public SuperTextView setShaderMode(ShaderMode shaderMode) {
        this.shaderMode = shaderMode;
        solidShader = null;
        postInvalidate();
        return this;
    }

    /**
     * 检查是否启用了渐变功能。
     *
     * @return 返回true，如果启用了渐变功能。
     */
    public boolean isShaderEnable() {
        return shaderEnable;
    }

    /**
     * 设置是否启用渐变色功能。
     *
     * @param shaderEnable true启用渐变功能。反之，停用。
     * @return SuperTextView
     */
    public SuperTextView setShaderEnable(boolean shaderEnable) {
        this.shaderEnable = shaderEnable;
        postInvalidate();
        return this;
    }

    /**
     * 获取文字渐变色的起始颜色。
     *
     * @return 文字渐变起始色。
     */
    public int getTextShaderStartColor() {
        return textShaderStartColor;
    }

    /**
     * 设置文字渐变起始色。需要调用{@link SuperTextView#setTextShaderEnable(boolean)}后才能生效。会触发一次重绘。
     *
     * @param shaderStartColor 文字渐变起始色
     * @return SuperTextView
     */
    public SuperTextView setTextShaderStartColor(int shaderStartColor) {
        this.textShaderStartColor = shaderStartColor;
        textShader = null;
        postInvalidate();
        return this;
    }

    /**
     * 获取文字渐变色的结束颜色。
     *
     * @return 文字渐变结束色。
     */
    public int getTextShaderEndColor() {
        return textShaderEndColor;
    }

    /**
     * 设置文字渐变结束色。需要调用{@link SuperTextView#setShaderEnable(boolean)}后才能生效。会触发一次重绘。
     *
     * @param shaderEndColor 文字渐变结束色
     * @return SuperTextView
     */
    public SuperTextView setTextShaderEndColor(int shaderEndColor) {
        this.textShaderEndColor = shaderEndColor;
        textShader = null;
        postInvalidate();
        return this;
    }

    /**
     * 获取文字渐变色模式。在{@link ShaderMode}中可以查看所有支持的模式。
     * 需要调用{@link SuperTextView#setTextShaderEnable(boolean)}后才能生效。
     *
     * @return 渐变模式。
     */
    public ShaderMode getTextShaderMode() {
        return textShaderMode;
    }

    /**
     * 设置文字渐变模式。在{@link ShaderMode}中可以查看所有支持的模式。
     * 需要调用{@link SuperTextView#setTextShaderEnable(boolean)}后才能生效。
     *
     * @param shaderMode 文字渐变模式
     * @return SuperTextView
     */
    public SuperTextView setTextShaderMode(ShaderMode shaderMode) {
        this.textShaderMode = shaderMode;
        textShader = null;
        postInvalidate();
        return this;
    }

    /**
     * 检查是否启用了文字渐变功能。
     *
     * @return 返回true，如果启用了文字渐变功能。
     */
    public boolean isTextShaderEnable() {
        return textShaderEnable;
    }

    /**
     * 设置是否启用文字渐变色功能。
     *
     * @param shaderEnable true启用文字渐变功能。反之，停用。
     * @return SuperTextView
     */
    public SuperTextView setTextShaderEnable(boolean shaderEnable) {
        this.textShaderEnable = shaderEnable;
        postInvalidate();
        return this;
    }


    /**
     * 获得当前按压背景色。没有设置默认为Color.TRANSPARENT。
     *
     * @return 按压时的背景色
     */
    public int getPressBgColor() {
        return pressBgColor;
    }

    /**
     * 获得当前按压背景色。一旦设置，立即生效。
     * 取消可以设置Color.TRANSPARENT。
     *
     * @param pressBgColor 按压背景色
     */
    public SuperTextView setPressBgColor(int pressBgColor) {
        this.pressBgColor = pressBgColor;
        return this;
    }

    /**
     * 获得当前按压文字颜色色。没有设置默认为-99。
     *
     * @return 按压时文字颜色
     */
    public int getPressTextColor() {
        return pressTextColor;
    }

    /**
     * 获得当前按压文字色。一旦设置，立即生效。
     * 取消可以设置-99。
     *
     * @param pressTextColor 按压时文字颜色
     */
    public SuperTextView setPressTextColor(int pressTextColor) {
        this.pressTextColor = pressTextColor;
        return this;
    }

    /**
     * 获取当前SuperTextView在播放 {@link Adjuster} 时的帧率。默认为60fps
     *
     * @return 帧率
     */
    public int getFrameRate() {
        return frameRate;
    }

    /**
     * 设置帧率，即每秒帧数。可在动画过程中随时改变。默认为60fps
     *
     * @param frameRate 帧率
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
     * 将一个网络图片作为SuperTextView的StateDrawable。
     * 在调用这个函数前，建议开发者根据当前所使用的图片框架实现{@link com.coorchice.library.image_engine.Engine}，
     * 然后通过{@link ImageEngine#install(Engine)}为SuperTextView的ImageEngine安装一个全局引擎，开发者可以在
     * {@link Application#onCreate()}中进行配置（需要注意任何时候新安装的引擎总会替换掉原本的引擎）。
     * 在未设置引擎的情况下，SuperTextView仍然会通过内置的一个十分简易引擎去下载图片。
     *
     * @param url          网络图片地址
     * @param asBackground 是否将下载的图片作为Background。
     *                     - false表示下载好的图片将作为SuperTextView的StateDrawable
     *                     - true表示将下载好的图片作为背景，效果和{@link SuperTextView#setDrawableAsBackground(boolean)}
     *                     是一样的。
     * @return SuperTextView
     */
    public SuperTextView setUrlImage(final String url, final boolean asBackground) {
        // 检查是否已经安装了Engine，没有安装会安装一个默认的，后面仍然可以随时被替换
        ImageEngine.checkEngine();
        // 缓存当前的imageUrl，当下载完成后需要校验
        curImageUrl = url;
        ImageEngine.load(url, new ImageEngine.Callback() {
            @Override
            public void onCompleted(final Drawable drawable) {
                if (getContext() != null && drawable != null && TextUtils.equals(curImageUrl, url)) {
                    SuperTextView.this.drawableAsBackground = asBackground;
                    setDrawable(drawable);
                }
            }
        });
        return this;
    }

    /**
     * 将一个网络图片作为SuperTextView的StateDrawable，调用这个方法StateDrawable将会被设置为背景模式。
     * 在调用这个函数前，建议开发者根据当前所使用的图片框架实现{@link com.coorchice.library.image_engine.Engine}，
     * 然后通过{@link ImageEngine#install(Engine)}为SuperTextView的ImageEngine安装一个全局引擎，开发者可以在
     * {@link Application#onCreate()}中进行配置（需要注意任何时候新安装的引擎总会替换掉原本的引擎）。
     * 在未设置引擎的情况下，SuperTextView仍然会通过内置的一个十分简易引擎去下载图片。
     *
     * @param url 网络图片地址
     * @return SuperTextView
     */
    public SuperTextView setUrlImage(final String url) {
        return setUrlImage(url, true);
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
            if (handleAnim == null) {
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
            if (onDrawableClickedListener != null) {
                if (drawableClickedListenerEnable(drawable, event.getX(), event.getY()) && !drawableAsBackground) {
                    drawable1Clicked = true;
                }
                if (drawableClickedListenerEnable(drawable2, event.getX(), event.getY())) {
                    drawable2Clicked = true;
                }
            }
            if (drawable1Clicked || drawable2Clicked) {
                hasConsume = true;
            } else {
                /**
                 * 触发 Drawable 事件，就禁止控件处理事件了
                 */
                superTouchEvent = super.onTouchEvent(event);
            }
        } else {
            for (int i = 0; i < touchAdjusters.size(); i++) {
                Adjuster adjuster = touchAdjusters.get(i);
                adjuster.onTouch(this, event);
                hasConsume = true;
            }
            if (superTouchEvent) {
                super.onTouchEvent(event);
            }
            if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
                if (onDrawableClickedListener != null) {
                    if (drawable1Clicked) {
                        onDrawableClickedListener.onDrawable1Clicked(this);
                    }
                    if (drawable2Clicked) {
                        onDrawableClickedListener.onDrawable2Clicked(this);
                    }
                }
                touchAdjusters.clear();
                drawable1Clicked = false;
                drawable2Clicked = false;
                superTouchEvent = false;
            }
        }
        return hasConsume || superTouchEvent;
    }

    private boolean drawableClickedListenerEnable(Drawable drawable, float x, float y) {
        boolean r = false;
        if (drawable != null && drawable.getBounds().contains((int) x, (int) y)) {
            r = true;
        }
        return r;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != VISIBLE && visibility != INVISIBLE) {
            cacheRunnableState = runnable;
            cacheNeedRunState = needRun;
            stopAnim();
            if (drawable instanceof GifDrawable && ((GifDrawable) drawable).isPlaying()) {
                cacheDrawablePlaying = true;
                ((GifDrawable) drawable).stop();
            }
            if (drawable2 instanceof GifDrawable && ((GifDrawable) drawable2).isPlaying()) {
                cacheDrawable2Playing = true;
                ((GifDrawable) drawable2).stop();
            }
        } else if (cacheRunnableState && cacheNeedRunState) {
            startAnim();
        } else {
            if (drawable instanceof GifDrawable && cacheDrawablePlaying) {
                cacheDrawablePlaying = false;
                ((GifDrawable) drawable).play();
            }
            if (drawable2 instanceof GifDrawable && cacheDrawable2Playing) {
                cacheDrawable2Playing = false;
                ((GifDrawable) drawable2).play();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        resetGifDrawable(drawable);
        resetGifDrawable(drawable2);
        stopAnim();
        super.onDetachedFromWindow();
    }

    /**
     * 设置一个监听器，用于监听 Drawable1 或者 Drawable2 的点击事件。
     * 需要注意，对于 Drawable1，只有在它不作为背景图使用使，才能触发点击事件。
     *
     * @param onDrawableClickedListener
     */
    public void setOnDrawableClickedListener(OnDrawableClickedListener onDrawableClickedListener) {
        this.onDrawableClickedListener = onDrawableClickedListener;
    }

    /**
     * 当 Drawable1 被作为背景图时，设置其缩放模式。
     * <p>
     * 具体请参考 {@link ScaleType}，默认缩放模式为 {@link ScaleType#CENTER}
     *
     * @param scaleType
     * @return
     */
    public SuperTextView setScaleType(ScaleType scaleType) {
        if (backgroundScaleType == scaleType) return this;
        this.backgroundScaleType = scaleType;
        drawableBackgroundShader = null;
        invalidate();
        return this;
    }

    /**
     * 当 Drawable1 被作为背景图时，获取其缩放模式。
     *
     * @return
     */
    public ScaleType getScaleType() {
        return backgroundScaleType;
    }

    /**
     * 设置一个跟踪器，用于追踪 SuperTextView 绘制
     *
     * @param tracker
     * @hide
     */
    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Adjuster被设计用来在SuperTextView的绘制过程中插入一些操作。
     * 这具有非常重要的意义。你可以用它来实现各种各样的效果。比如插入动画、修改状态。
     * 你可以指定Adjuster的作用层级，通过调用{@link Adjuster#setOpportunity(Opportunity)}，
     * {@link Opportunity}。默认为{@link Opportunity#BEFORE_TEXT}。
     * 在Adjuster中，可以获取到控件的触摸事件，这对于实现一些复杂的交互效果很有帮助。
     */
    public static abstract class Adjuster {
        private static final int TYPE_SYSTEM = 0x001;
        private static final int TYPE_CUSTOM = 0x002;

        private Opportunity opportunity = Opportunity.BEFORE_TEXT;
        private int type = TYPE_CUSTOM;

        /**
         * 当前Adjuster被设置到的SuperTextView
         */
        public SuperTextView host;

        /**
         * 在Canvas上绘制的东西将能够呈现在SuperTextView上。
         * 提示：你需要注意图层的使用。
         *
         * @param v      SuperTextView
         * @param canvas 用于绘制的Canvas。注意对Canvas的变换最好使用图层，否则会影响后续的绘制。
         */
        protected abstract void adjust(SuperTextView v, Canvas canvas);

        /**
         * 在这个方法中，你能够捕获到SuperTextView中发生的触摸事件。
         * 需要注意，如果你在该方法中返回了true来处理SuperTextView的触摸事件的话，你将在
         * SuperTextView的setOnTouchListener中设置的OnTouchListener中同样能够捕获到这些触摸事件，即使你在OnTouchListener中返回了false。
         * 但是，如果你在OnTouchListener中返回了true，这个方法将会失效，因为事件在OnTouchListener中被拦截了。
         *
         * @param v     SuperTextView
         * @param event 控件件接收到的触摸事件。
         * @return 默认返回false。如果想持续的处理控件的触摸事件就返回true。否则，只能接收到{@link MotionEvent#ACTION_DOWN}事件。
         */
        public boolean onTouch(SuperTextView v, MotionEvent event) {
            return false;
        }

        /**
         * 当Adjuster被通过 {@link SuperTextView#addAdjuster(Adjuster)} 设置到一个SuperTextView中时，
         * 会被调用。用于建立Adjuster与宿主SuperTextView之间的关系。
         *
         * @param stv 当前被设置到的SuperTextView对象
         * @return
         */
        private void attach(SuperTextView stv) {
            this.host = stv;
            onAttach(this.host);
        }

        /**
         * 当Adjuster被通过 {@link SuperTextView#addAdjuster(Adjuster)} 设置到一个SuperTextView中时， 会被调用。
         * <p>
         * 在这个方法中，开发者可以根据当前所处的SuperTextView环境，进行一些初始化的配置。
         *
         * @param stv 当前被设置到的SuperTextView对象
         */
        public void onAttach(SuperTextView stv) {

        }

        /**
         * 当Adjuster被从一个SuperTextView中移除时会被调用，用于解除Adjuster与宿主SuperTextView之间的关系。
         *
         * @param stv 当前被从那个SuperTextView中移除
         * @return
         */
        private void detach(SuperTextView stv) {
            this.host = null;
            onDetach(stv);
        }

        /**
         * 当Adjuster被从一个SuperTextView中移除时会被调用，用于解除Adjuster与宿主SuperTextView之间的关系。
         * <p>
         * 需要注意，在这个方法中，成员变量 {@link Adjuster#host} 已经被释放，不要直接使用该成员变量，而是使用 参数 stv。
         *
         * @param stv 当前被从那个SuperTextView中移除
         * @return
         */
        public void onDetach(SuperTextView stv) {

        }

        /**
         * 获取当前Adjuster的层级。
         *
         * @return Adjuster的作用层级。
         */
        public Opportunity getOpportunity() {
            return opportunity;
        }

        /**
         * 设置Adjuster的作用层级。在 {@link Opportunity} 中可以查看所有支持的层级。
         *
         * @param opportunity Adjuster的作用层级
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
             * 最顶层
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
     * SuperTextView的渐变模式。
     * 可以通过 {@link SuperTextView#setStateDrawableMode(DrawableMode)}设置控件的Shader模式
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

    /**
     * 当 Drawable1 作为背景图时的剪裁模式
     */
    public enum ScaleType {
        /**
         * 将图片拉伸平铺，充满 SuperTextView
         */
        FIT_XY(0),
        /**
         * 将图片自适应居中。
         */
        FIT_CENTER(1),
        /**
         * 将图片剪裁居中。默认值。
         */
        CENTER(2);

        public int code;

        ScaleType(int code) {
            this.code = code;
        }

        public static ScaleType valueOf(int code) {
            for (ScaleType mode : ScaleType.values()) {
                if (mode.code == code) {
                    return mode;
                }
            }
            return CENTER;
        }
    }

    /**
     * 用于监听 Drawable1 和 Drawable2 被点击的事件
     * 需要注意，对于 Drawable1，只有在它不作为背景图使用使，才能触发点击事件。
     */
    public static interface OnDrawableClickedListener {
        /**
         * 当 Drawable1 被点击时触发。
         * 需要注意，对于 Drawable1，只有在它不作为背景图使用使，才能触发点击事件。
         *
         * @param stv
         */
        void onDrawable1Clicked(SuperTextView stv);

        /**
         * 当 Drawable2 被点击时触发。
         *
         * @param stv
         */
        void onDrawable2Clicked(SuperTextView stv);
    }
}
