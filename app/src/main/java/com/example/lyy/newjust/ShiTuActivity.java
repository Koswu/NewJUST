package com.example.lyy.newjust;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ShiTuActivity extends SwipeBackActivity {

    private String keyword;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shi_tu);

        setSwipeBackEnable(true);   // 可以调用该方法，设置是否允许滑动退出
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        mSwipeBackLayout.setEdgeSize(200);

        webView = (WebView) findViewById(R.id.shitu_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        if (type.equals("picture")) {
            webView.loadUrl("http://shitu.baidu.com/");
        } else if (type.equals("keywords")) {
            final EditText editText = new EditText(ShiTuActivity.this);
            AlertDialog.Builder inputDialog = new AlertDialog.Builder(ShiTuActivity.this);
            inputDialog.setTitle("输入搜索关键字").setView(editText);
            inputDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            keyword = editText.getText().toString();
                            webView.loadUrl("https://m.baidu.com/sf/vsearch?pd=image_content&word=" + keyword + "&tn=vsearch&sa=vs_tab&lid=16465178688154213928&ms=1&atn=page&fr=tab");
                        }
                    }).show();
        }


    }
}
