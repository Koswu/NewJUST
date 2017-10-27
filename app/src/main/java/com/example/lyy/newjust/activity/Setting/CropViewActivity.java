package com.example.lyy.newjust.activity.Setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.lyy.newjust.R;
import com.oginotihiro.cropview.CropUtil;
import com.oginotihiro.cropview.CropView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CropViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CropViewActivity";

    private CropView cropView;
    private LinearLayout btnlay;
    private Button doneBtn;
    private Button cancelBtn;

    private Bitmap croppedBitmap;

    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_view);

        cropView = (CropView) findViewById(R.id.cropView);
        btnlay = (LinearLayout) findViewById(R.id.btnlay);
        doneBtn = (Button) findViewById(R.id.doneBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        doneBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        Intent intent = getIntent();
        uri = Uri.parse(intent.getStringExtra("uri"));

        cropView.setVisibility(View.VISIBLE);
        btnlay.setVisibility(View.VISIBLE);

        cropView.of(uri).asSquare().initialize(CropViewActivity.this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.doneBtn) {
            final ProgressDialog dialog = ProgressDialog.show(CropViewActivity.this, null, "Please wait…", true, false);

            cropView.setVisibility(View.GONE);
            btnlay.setVisibility(View.GONE);

            new Thread() {
                public void run() {
                    croppedBitmap = cropView.getOutput();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
                    String imageBase64 = new String(Base64.encode(baos.toByteArray(), 0));

                    SharedPreferences sPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPreferences.edit();
                    editor.putString("image", imageBase64);
                    editor.apply();

                    Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    CropUtil.saveOutput(CropViewActivity.this, destination, croppedBitmap, 90);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            CropViewActivity.this.finish();
                        }
                    });
                }
            }.start();
        } else if (id == R.id.cancelBtn) {
            reset();
            CropViewActivity.this.finish();
        }
    }

    private void reset() {
        cropView.setVisibility(View.GONE);
        btnlay.setVisibility(View.GONE);
    }
}
