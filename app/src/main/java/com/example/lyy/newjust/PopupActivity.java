package com.example.lyy.newjust;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.githang.statusbar.StatusBarCompat;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class PopupActivity extends SwipeBackActivity {

    private static final String TAG = "PopupActivity";

    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        StatusBarCompat.setStatusBarColor(this, Color.rgb(0,127,193));

        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");

        webView = (WebView) findViewById(R.id.detail_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        Log.d(TAG, "onCreate: " + url);
        webView.loadUrl(url);

        setSwipeBackEnable(true);   // 可以调用该方法，设置是否允许滑动退出
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        mSwipeBackLayout.setEdgeSize(200);

        //设置和toolbar相关的
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.detail_swipe);
        //设置转的圈的颜色
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        //设置水波纹的颜色
        mWaveSwipeRefreshLayout.setWaveColor(Color.rgb(0, 172, 193));
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(webView.getUrl());
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
