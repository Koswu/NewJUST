package com.example.lyy.newjust;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.lyy.newjust.util.AppConstants;
import com.example.lyy.newjust.util.SpUtils;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by lyy on 2017/10/24.
 */

public class WelcomeActivity extends AppIntro {

    @Override
    public void init(@Nullable Bundle savedInstanceState) {

        addSlide(SlideFragment.newInstance(R.layout.welcome_layout_1));
        addSlide(SlideFragment.newInstance(R.layout.welcome_layout_2));
        addSlide(SlideFragment.newInstance(R.layout.welcome_layout_3));
        addSlide(SlideFragment.newInstance(R.layout.welcome_layout_4));
        setSeparatorColor(getResources().getColor(R.color.colorAccent));
        setVibrateIntensity(30);
        setSkipText("跳过");
        setDoneText("完成");
    }

    @Override
    public void onSkipPressed() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        SpUtils.putBoolean(WelcomeActivity.this, AppConstants.FIRST_OPEN, true);
        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        SpUtils.putBoolean(WelcomeActivity.this, AppConstants.FIRST_OPEN, true);
        finish();
    }

    @Override
    public void onSlideChanged() {

    }
}
