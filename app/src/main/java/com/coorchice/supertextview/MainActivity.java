package com.coorchice.supertextview;

import com.coorchice.library.SuperTextView;
import com.coorchice.supertextview.SuperTextView.Adjuster.MoveEffectAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.OpportunityDemoAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.RippleAdjuster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    //
    private SuperTextView stv_17;
    private SuperTextView stv_18;
    private SuperTextView stv_19;
    private SuperTextView stv_20;
    private SuperTextView stv_21;
    private SuperTextView stv_22;
    private SuperTextView btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViews();

        stv_17.setAdjuster(new MoveEffectAdjuster().setOpportunity(SuperTextView.Adjuster.Opportunity.BEFORE_DRAWABLE))
                .setAutoAdjust(true)
                .startAnim();

//    stv_18.setAdjuster(new RippleAdjuster(getResources().getColor(R.color.opacity_5_a58fed)));
        stv_18.setAdjuster(new RippleAdjuster(getResources().getColor(R.color.opacity_9_blue)));

        OpportunityDemoAdjuster opportunityDemoAdjuster1 = new OpportunityDemoAdjuster();
        opportunityDemoAdjuster1.setOpportunity(SuperTextView.Adjuster.Opportunity.BEFORE_DRAWABLE);
        stv_19.setAdjuster(opportunityDemoAdjuster1);
        stv_19.setAutoAdjust(true);

        OpportunityDemoAdjuster opportunityDemoAdjuster2 = new OpportunityDemoAdjuster();
        opportunityDemoAdjuster2.setOpportunity(SuperTextView.Adjuster.Opportunity.BEFORE_TEXT);
        stv_20.setAdjuster(opportunityDemoAdjuster2);
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
    }

    private void findViews() {
        stv_17 = (SuperTextView) findViewById(R.id.stv_17);
        stv_18 = (SuperTextView) findViewById(R.id.stv_18);
        stv_19 = (SuperTextView) findViewById(R.id.stv_19);
        stv_20 = (SuperTextView) findViewById(R.id.stv_20);
        stv_21 = (SuperTextView) findViewById(R.id.stv_21);
        stv_22 = (SuperTextView) findViewById(R.id.stv_22);
        btn_next = (SuperTextView) findViewById(R.id.btn_next);

        // 开启渐变色功能
        stv_17.setShaderEnable(true);
        // 设置起始颜色
        stv_17.setShaderStartColor(Color.BLUE);
        // 设置结束颜色
        stv_17.setShaderEndColor(Color.RED);
        // 设置模式
        stv_17.setShaderMode(SuperTextView.ShaderMode.LEFT_TO_RIGHT);

        stv_17.setStateDrawableMode(SuperTextView.DrawableMode.CENTER);

        // 设置按压背景变色
        stv_17.setPressBgColor(Color.RED);
        // 取消按压文字变色
        stv_17.setPressTextColor(-99);

    }
}
