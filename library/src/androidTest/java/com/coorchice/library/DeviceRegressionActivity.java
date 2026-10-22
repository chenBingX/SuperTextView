package com.coorchice.library;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

/** Deliberately empty host: tests exercise the production View, not a demo substitute. */
public class DeviceRegressionActivity extends Activity {
    FrameLayout root;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        root = new FrameLayout(this);
        setContentView(root);
    }
}
