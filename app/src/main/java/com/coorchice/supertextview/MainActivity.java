package com.coorchice.supertextview;

import com.coorchice.supertextview.SuperTextView.SuperTextView;
import com.coorchice.supertextview.SuperTextView.Adjuster.MoveEffectAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.RippleAdjuster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private SuperTextView stv_17;
  private SuperTextView stv_18;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
  }

  private void initView() {
    stv_17 = (SuperTextView) findViewById(R.id.stv_17);
    stv_17.setAdjuster(new MoveEffectAdjuster());
    stv_17.setAutoAdjust(true);
    stv_17.startAnim();

    stv_18 = (SuperTextView) findViewById(R.id.stv_18);
    stv_18.setAdjuster(new RippleAdjuster(getResources().getColor(R.color.opacity_5_a58fed)));
  }
}
