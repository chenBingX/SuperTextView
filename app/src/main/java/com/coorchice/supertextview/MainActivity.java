package com.coorchice.supertextview;

import com.coorchice.library.SuperTextView;
import com.coorchice.supertextview.SuperTextView.Adjuster.MoveEffectAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.OpportunityDemoAdjuster;
import com.coorchice.supertextview.SuperTextView.Adjuster.RippleAdjuster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
//
   private SuperTextView stv_17;
   private SuperTextView stv_18;
   private SuperTextView stv_19;
   private SuperTextView stv_20;
   private SuperTextView stv_21;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
  }

  private void initView() {
    findViews();

    stv_17.setAdjuster(new MoveEffectAdjuster())
        .setAutoAdjust(true)
        .startAnim();

    stv_18.setAdjuster(new RippleAdjuster(getResources().getColor(R.color.opacity_5_a58fed)));

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
    stv_21.setAdjuster(opportunityDemoAdjuster3);
    stv_21.setAutoAdjust(true);
  }

  private void findViews() {
     stv_17 = (SuperTextView) findViewById(R.id.stv_17);
     stv_18 = (SuperTextView) findViewById(R.id.stv_18);
     stv_19 = (SuperTextView) findViewById(R.id.stv_19);
     stv_20 = (SuperTextView) findViewById(R.id.stv_20);
     stv_21 = (SuperTextView) findViewById(R.id.stv_21);
  }
}
