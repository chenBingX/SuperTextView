package com.coorchice.supertextview;

import com.coorchice.library.SuperTextView;
import com.coorchice.supertextview.SuperTextView.Adjuster.MoveEffectAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.OpportunityDemoAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.RippleAdjuster;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViews();

        stv_17.addAdjuster(new MoveEffectAdjuster().setOpportunity(SuperTextView.Adjuster.Opportunity.BEFORE_DRAWABLE))
                .setAutoAdjust(true)
                .startAnim();

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
        stv_0 = (SuperTextView) findViewById(R.id.stv_0);
        stv_17 = (SuperTextView) findViewById(R.id.stv_17);
        stv_18 = (SuperTextView) findViewById(R.id.stv_18);
        stv_19 = (SuperTextView) findViewById(R.id.stv_19);
        stv_20 = (SuperTextView) findViewById(R.id.stv_20);
        stv_21 = (SuperTextView) findViewById(R.id.stv_21);
        stv_22 = (SuperTextView) findViewById(R.id.stv_22);
//        SuperTextView stv_30 = (SuperTextView) findViewById(R.id.stv_30);
//        stv_30.setTextShaderMode(SuperTextView.ShaderMode.LEFT_TO_RIGHT);
        btn_next = (SuperTextView) findViewById(R.id.btn_next);
    }
}
