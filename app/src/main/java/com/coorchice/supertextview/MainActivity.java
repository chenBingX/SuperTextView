package com.coorchice.supertextview;

import com.coorchice.library.SuperTextView;
import com.coorchice.library.gifdecoder.GifDrawable;
import com.coorchice.library.utils.LogUtils;
import com.coorchice.supertextview.SuperTextView.Adjuster.MoveEffectAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.OpportunityDemoAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.RippleAdjuster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {
    //
    private SuperTextView stv_17;
    private SuperTextView stv_18;
    private SuperTextView stv_19;
    private SuperTextView stv_20;
    private SuperTextView stv_21;
    private SuperTextView stv_22;
    private SuperTextView btn_next;
    private SuperTextView stv_0;
    private SuperTextView stv_14;
    private SuperTextView stv_clickDemo1;
    private float density;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        density = getResources().getDisplayMetrics().density;
        LogUtils.DEBUG = true;
        SuperTextView.GIF_CACHE_ENABLE = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViews();

//        stv_17.addAdjuster(new MoveEffectAdjuster().setOpportunity(SuperTextView.Adjuster.Opportunity.BEFORE_DRAWABLE))
//                .setAutoAdjust(true)
//                .startAnim();

//    stv_18.addAdjuster(new RippleAdjuster(getResources().getColor(R.color.opacity_5_a58fed)));
        stv_18.addAdjuster(new RippleAdjuster(getResources().getColor(R.color.opacity_9_blue)));

        OpportunityDemoAdjuster opportunityDemoAdjuster1 = new OpportunityDemoAdjuster();
        opportunityDemoAdjuster1.setOpportunity(SuperTextView.Adjuster.Opportunity.BEFORE_DRAWABLE);
        stv_19.addAdjuster(opportunityDemoAdjuster1);
        stv_19.setAutoAdjust(true);

        OpportunityDemoAdjuster opportunityDemoAdjuster2 = new OpportunityDemoAdjuster();
        opportunityDemoAdjuster2.setOpportunity(SuperTextView.Adjuster.Opportunity.BEFORE_TEXT);
        stv_20.addAdjuster(opportunityDemoAdjuster2);
        stv_20.setAutoAdjust(true);

        OpportunityDemoAdjuster opportunityDemoAdjuster3 = new OpportunityDemoAdjuster();
        opportunityDemoAdjuster3.setOpportunity(SuperTextView.Adjuster.Opportunity.AT_LAST);
        stv_21.addAdjuster(opportunityDemoAdjuster3);
        stv_21.setAutoAdjust(true);
        stv_21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stv_21.removeAdjuster(0);
            }
        });

        stv_clickDemo1.setOnDrawableClickedListener(new SuperTextView.OnDrawableClickedListener() {
            @Override
            public void onDrawable1Clicked(SuperTextView stv) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(stv_clickDemo1, "drawablePaddingLeft", density * 3, density * 7);
                anim.setDuration(50);
                anim.setRepeatCount(6);
                anim.setRepeatMode(ValueAnimator.REVERSE);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        stv_clickDemo1.setDrawablePaddingLeft(5 * density);
                    }
                });
                anim.start();
            }

            @Override
            public void onDrawable2Clicked(SuperTextView stv) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(stv_clickDemo1, "drawable2PaddingLeft", density * -7, density * -3);
                anim.setDuration(50);
                anim.setRepeatCount(6);
                anim.setRepeatMode(ValueAnimator.REVERSE);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        stv_clickDemo1.setDrawable2PaddingLeft(-5 * density);
                    }
                });
                anim.start();
            }
        });
        stv_clickDemo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "非 Drawable 区域被点击", Toast.LENGTH_SHORT).show();
            }
        });

        btn_next.setFrameRate(60);
        btn_next.setShaderStartColor(Color.RED);

//    stv_22.setShaderStartColor(Color.BLUE);
//    stv_22.setShaderEndColor(Color.YELLOW);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
//        try {
//            InputStream is = getResources().openRawResource(R.drawable.gif_emoji_1);
//            byte[] bytes = new byte[is.available()];
//            is.read(bytes);
//            GifDrawable drawable = GifDrawable.createDrawable(bytes);
//            stv_14.setDrawable(drawable);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        stv_14.setDrawable(R.drawable.gif_emoji_1);
        stv_14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GifDrawable) stv_14.getDrawable()).play();
            }
        });

        // 进入测试Activity
        stv_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

    }

    private void findViews() {
        stv_0 = (SuperTextView) findViewById(R.id.stv_0);
        stv_14 = (SuperTextView) findViewById(R.id.stv_14);
        stv_17 = (SuperTextView) findViewById(R.id.stv_17);
        stv_18 = (SuperTextView) findViewById(R.id.stv_18);
        stv_19 = (SuperTextView) findViewById(R.id.stv_19);
        stv_20 = (SuperTextView) findViewById(R.id.stv_20);
        stv_21 = (SuperTextView) findViewById(R.id.stv_21);
        stv_22 = (SuperTextView) findViewById(R.id.stv_22);
        stv_clickDemo1 = (SuperTextView) findViewById(R.id.stv_click_demo1);
//        SuperTextView stv_30 = (SuperTextView) findViewById(R.id.stv_30);
//        stv_30.setTextShaderMode(SuperTextView.ShaderMode.LEFT_TO_RIGHT);
        btn_next = (SuperTextView) findViewById(R.id.btn_next);
    }
}
