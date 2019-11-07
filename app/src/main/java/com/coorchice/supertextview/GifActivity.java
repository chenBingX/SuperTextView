package com.coorchice.supertextview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.coorchice.library.SuperTextView;
import com.coorchice.library.gifdecoder.GifCache;
import com.coorchice.library.gifdecoder.GifDrawable;
import com.coorchice.library.utils.LogUtils;
import com.coorchice.library.utils.STVUtils;
import com.coorchice.library.utils.ThreadPool;

public class GifActivity extends Activity {

  private SuperTextView stvGifScreen;
  private SuperTextView stvPre;
  private SuperTextView stvPlay;
  private SuperTextView stvNext;
  private SuperTextView stvStrict;
  private SuperTextView stvScaleType;
  private EditText etFrameDuration;
  private SuperTextView stvFrameDuration;
  private EditText etGotoFrame;
  private SuperTextView stvGotoFrame;
  private EditText etGetFrame;
  private SuperTextView stvGetFrame;
  private SuperTextView stvShowFrame;
  private SuperTextView tvInfo;

  private long gifMemory = 0;
  private long lastDrawTime = System.currentTimeMillis();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gif);
    initView();
    addListener();

  }

  private void initView() {
    stvGifScreen = (SuperTextView) findViewById(R.id.stv_gif_screen);
    stvPre = (SuperTextView) findViewById(R.id.stv_pre);
    stvPlay = (SuperTextView) findViewById(R.id.stv_play);
    stvNext = (SuperTextView) findViewById(R.id.stv_next);
    stvStrict = (SuperTextView) findViewById(R.id.stv_strict);
    stvScaleType = (SuperTextView) findViewById(R.id.stv_scale_type);
    etFrameDuration = (EditText) findViewById(R.id.et_frame_duration);
    stvFrameDuration = (SuperTextView) findViewById(R.id.stv_frame_duration);
    etGotoFrame = (EditText) findViewById(R.id.et_goto_frame);
    stvGotoFrame = (SuperTextView) findViewById(R.id.stv_goto_frame);
    etGetFrame = (EditText) findViewById(R.id.et_get_frame);
    stvGetFrame = (SuperTextView) findViewById(R.id.stv_get_frame);
    stvShowFrame = (SuperTextView) findViewById(R.id.stv_show_frame);
    tvInfo = (SuperTextView) findViewById(R.id.tv_info);

    stvPlay.setSelected(true);
    ThreadPool.run(new Runnable() {
      @Override
      public void run() {
        final long start = System.currentTimeMillis();
        LogUtils.e("gif-begin = " + start);
        byte[] resBytes = STVUtils.getResBytes(GifActivity.this, R.drawable.gif_m_7);
        LogUtils.e("gif-end = " + (System.currentTimeMillis() - start));
        gifMemory = resBytes.length / 1024;
        final GifDrawable drawable = GifCache.fromResource(GifActivity.this, R.drawable.gif_m_7);
        LogUtils.e("gif-end2 = " + (System.currentTimeMillis() - start));
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            stvGifScreen.setDrawable(drawable);
            LogUtils.e("gif-end3 = " + (System.currentTimeMillis() - start));
          }
        });
      }
    });
  }

  private void addListener() {
    stvGifScreen.setAutoAdjust(true);
    stvGifScreen.addAdjuster(new SuperTextView.Adjuster() {
      @Override
      protected void adjust(SuperTextView v, Canvas canvas) {
        if (stvGifScreen.getDrawable() instanceof GifDrawable) {
          GifDrawable gifDrawable = (GifDrawable) stvGifScreen.getDrawable();
          StringBuilder sb = new StringBuilder()
            .append("gif memory : ").append(gifMemory / 1000).append("MB").append(gifMemory % 1000).append("KB\n")
            .append("ptr : ").append(gifDrawable.getPtr()).append("\n")
            .append("width : ").append(gifDrawable.getWidth()).append("\n")
            .append("height : ").append(gifDrawable.getHeight()).append("\n")
            .append("frame count : ").append(gifDrawable.getFrameCount()).append("\n")
            .append("frame duration : ").append(gifDrawable.getFrameDuration()).append("ms\n")
//            .append("rel frame duration : ").append(System.currentTimeMillis() - lastDrawTime).append("ms\n")
            .append("enable strict : ").append(gifDrawable.isStrict()).append("\n")
            .append("current frame : ").append(gifDrawable.getCurrentFrame());
          if (!(tvInfo.getText() != null && tvInfo.getText().toString().equals(sb.toString()))) {
            tvInfo.setText(sb);
          }
          lastDrawTime = System.currentTimeMillis();
        }
      }
    }.setOpportunity(SuperTextView.Adjuster.Opportunity.AT_LAST));

    stvScaleType.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switch (stvGifScreen.getScaleType()) {
          case FIT_XY:
            stvGifScreen.setScaleType(SuperTextView.ScaleType.FIT_CENTER);
            Toast.makeText(GifActivity.this, "ScaleType：FIT_CENTER", Toast.LENGTH_SHORT).show();
            break;
          case FIT_CENTER:
            stvGifScreen.setScaleType(SuperTextView.ScaleType.CENTER);
            Toast.makeText(GifActivity.this, "ScaleType：CENTER", Toast.LENGTH_SHORT).show();
            break;
          case CENTER:
            stvGifScreen.setScaleType(SuperTextView.ScaleType.FIT_XY);
            Toast.makeText(GifActivity.this, "ScaleType：FIT_XY", Toast.LENGTH_SHORT).show();
            break;
        }
      }
    });

    stvPlay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stvPlay.setSelected(!stvPlay.isSelected());
        stvPlay.setDrawable(stvPlay.isSelected() ? R.drawable.icon_stop : R.drawable.icon_play);
        if (stvGifScreen.getDrawable() instanceof GifDrawable) {
          GifDrawable gifDrawable = (GifDrawable) stvGifScreen.getDrawable();
          if (stvPlay.isSelected()) {
            gifDrawable.play();
          } else {
            gifDrawable.stop();
          }
        }
      }
    });

    stvPre.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (stvGifScreen.getDrawable() instanceof GifDrawable) {
          GifDrawable gifDrawable = (GifDrawable) stvGifScreen.getDrawable();
          int pre = gifDrawable.getCurrentFrame() - 1;
          if (pre < 0) {
            pre = 0;
          }
          gifDrawable.gotoFrame(pre);
        }
      }
    });

    stvNext.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (stvGifScreen.getDrawable() instanceof GifDrawable) {
          GifDrawable gifDrawable = (GifDrawable) stvGifScreen.getDrawable();
          int frameCount = gifDrawable.getFrameCount();
          int next = gifDrawable.getCurrentFrame() + 1;
          if (next >= frameCount) {
            next = frameCount - 1;
          }
          gifDrawable.gotoFrame(next);
        }
      }
    });

    stvStrict.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (stvGifScreen.getDrawable() instanceof GifDrawable) {
          GifDrawable gifDrawable = (GifDrawable) stvGifScreen.getDrawable();
          boolean strict = !gifDrawable.isStrict();
          stvStrict.setDrawable(strict ? R.drawable.icon_selected : R.drawable.icon_unselected);
          gifDrawable.setStrict(strict);
        }
      }
    });

    stvFrameDuration.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String num = etFrameDuration.getText().toString();
        if (!TextUtils.isEmpty(num) && stvGifScreen.getDrawable() instanceof GifDrawable) {
          int i = Integer.valueOf(num);
          GifDrawable gifDrawable = (GifDrawable) stvGifScreen.getDrawable();
          gifDrawable.setFrameDuration(i);
        }
      }
    });

    stvGotoFrame.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String num = etGotoFrame.getText().toString();
        if (!TextUtils.isEmpty(num) && stvGifScreen.getDrawable() instanceof GifDrawable) {
          int i = Integer.valueOf(num);
          GifDrawable gifDrawable = (GifDrawable) stvGifScreen.getDrawable();
          int frameCount = gifDrawable.getFrameCount();
          if (i >= frameCount) {
            i = frameCount - 1;
          } else if (i < 0) {
            i = 0;
          }
          gifDrawable.gotoFrame(i);
        }
      }
    });

    stvGetFrame.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String num = etGetFrame.getText().toString();
        if (!TextUtils.isEmpty(num) && stvGifScreen.getDrawable() instanceof GifDrawable) {
          int i = Integer.valueOf(num);
          final GifDrawable gifDrawable = (GifDrawable) stvGifScreen.getDrawable();
          int frameCount = gifDrawable.getFrameCount();
          if (i >= frameCount) {
            i = frameCount - 1;
          } else if (i < 0) {
            i = 0;
          }
          if (gifDrawable.isStrict()) {
            final int finalPosition = i;
            ThreadPool.run(new Runnable() {
              @Override
              public void run() {
                final Bitmap frame = gifDrawable.getFrame(finalPosition);
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    stvShowFrame.setDrawable(frame);
                    stvShowFrame.setVisibility(View.VISIBLE);
                  }
                });

              }
            });
          } else {
            Bitmap frame = gifDrawable.getFrame(i);
            stvShowFrame.setDrawable(frame);
            stvShowFrame.setVisibility(View.VISIBLE);
          }
        }
      }
    });

    stvShowFrame.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stvShowFrame.setVisibility(View.GONE);
      }
    });
  }
}
