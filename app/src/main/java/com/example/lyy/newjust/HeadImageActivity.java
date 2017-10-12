package com.example.lyy.newjust;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lyy.newjust.util.SDFileHelper;
import com.github.ppamorim.dragger.DraggerActivity;
import com.github.ppamorim.dragger.DraggerPosition;
import com.github.ppamorim.dragger.DraggerView;

public class HeadImageActivity extends DraggerActivity {

    private static final String TAG = "HeadImageActivity";

    private DraggerView draggerView;

    private String headPicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_image);


        draggerView = (DraggerView) findViewById(R.id.dragger_view);
        draggerView.setDraggerPosition(DraggerPosition.BOTTOM);

        Intent intent = getIntent();
        headPicUrl = intent.getStringExtra("headPicUrl");

        ImageView bd_imageview = (ImageView) findViewById(R.id.bg_imageView);

        Log.d(TAG, "onCreate: " + headPicUrl);

        Glide.with(this).load(headPicUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(bd_imageview);

        ImageView download_imageView = (ImageView) findViewById(R.id.download_imageView);
        download_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SDFileHelper helper = new SDFileHelper(getApplicationContext());
                helper.savePicture("bg.jpg", headPicUrl);
            }
        });
    }

    @Override
    public void onBackPressed() {
        draggerView.closeActivity();
    }
}
