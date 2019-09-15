package com.coorchice.supertextview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.coorchice.library.OnDrawableClickedListenerAdapter;
import com.coorchice.library.SuperTextView;
import com.coorchice.library.gifdecoder.GifDrawable;
import com.coorchice.library.utils.STVUtils;
import com.coorchice.library.utils.ThreadPool;

public class GifListActivity extends Activity {

  private SuperTextView stv_1;
  private SuperTextView stv_4;
  private SuperTextView stv_5;
  private SuperTextView stv_6;
  private SuperTextView stv_next;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gif_list);
    initView();

    ThreadPool.run(new Runnable() {
      @Override
      public void run() {
        byte[] resBytes = STVUtils.getResBytes(GifListActivity.this, R.drawable.gif_v2);
        final GifDrawable gifDrawable = GifDrawable.createDrawable(resBytes);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            stv_4.setVisibility(View.VISIBLE);
            stv_4.setDrawable(gifDrawable);
          }
        });
      }
    });
  }

  private void initView() {
    stv_1 = (SuperTextView) findViewById(R.id.stv_1);
    stv_4 = (SuperTextView) findViewById(R.id.stv_4);
    stv_5 = (SuperTextView) findViewById(R.id.stv_5);
    stv_6 = (SuperTextView) findViewById(R.id.stv_6);
    stv_next = (SuperTextView) findViewById(R.id.stv_next);

    if (stv_1.getDrawable() instanceof GifDrawable) {
      GifDrawable gifDrawable = (GifDrawable) stv_1.getDrawable();
//      gifDrawable.setFrameDuration(25);
    }

    stv_5.setSelected(true);
    stv_5.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        stv_5.setSelected(!stv_5.isSelected());
        if (stv_5.getDrawable() instanceof GifDrawable){
          GifDrawable gifDrawable = (GifDrawable)stv_5.getDrawable();
          if (stv_5.isSelected()){
            gifDrawable.play();
          } else {
            gifDrawable.stop();
          }
        }
        stv_5.setDrawable2(stv_5.isSelected() ? R.drawable.icon_stop : R.drawable.icon_play);
      }
    });

    stv_next.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(GifListActivity.this, GifActivity.class));
      }
    });

    stv_6.setOnDrawableClickedListener(new OnDrawableClickedListenerAdapter(){
      /**
       * 当 Drawable1 被点击时触发
       *
       * @param stv
       */
      @Override
      public void onDrawable1Clicked(SuperTextView stv) {
        Toast.makeText(GifListActivity.this, "Drawable1 区域被点击", Toast.LENGTH_SHORT).show();
      }

      /**
       * 当 Drawable2 被点击时触发
       *
       * @param stv
       */
      @Override
      public void onDrawable2Clicked(SuperTextView stv) {
        Toast.makeText(GifListActivity.this, "Drawable2 区域被点击", Toast.LENGTH_SHORT).show();
      }
    });
  }
}


