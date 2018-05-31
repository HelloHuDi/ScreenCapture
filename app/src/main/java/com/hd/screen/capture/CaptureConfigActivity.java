package com.hd.screen.capture;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class CaptureConfigActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new CaptureConfigFragment()).commit();
    }
}
