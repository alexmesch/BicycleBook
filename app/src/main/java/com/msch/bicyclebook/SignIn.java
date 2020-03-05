package com.msch.bicyclebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SignIn extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
    }

    public void returnToMaps(View v){
        finish();
    }
}
