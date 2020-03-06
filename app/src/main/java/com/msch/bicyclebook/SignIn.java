package com.msch.bicyclebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class SignIn extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        View anim_elements = findViewById(R.id.signIn_container);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha_transition);
        anim_elements.startAnimation(anim);

    }

    public void returnToMaps(View v) {
        finish();
    }
}
