package com.example.lyy.newjust.activity.Tools;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lyy.newjust.R;
import com.example.lyy.newjust.util.Audio;
import com.githang.statusbar.StatusBarCompat;
import com.shinelw.library.ColorArcProgressBar;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class AudioActivity extends SwipeBackActivity {

//    private TextView volume;//显示音量的文本框
    Audio MyAudio;//自己写的用于获取音量的类
    public MyHandler myHandler;//用于传递数据给主线程更新UI

    private ColorArcProgressBar progressbar;

    private LinearLayout mLayout;
    //private NewCreditSesameView newCreditSesameView;

    private final int[] mColors = new int[]{
            Color.rgb(135, 206, 250),
            Color.rgb(135, 206, 235),
            Color.rgb(135, 206, 235),
            Color.rgb(95, 158, 160)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        StatusBarCompat.setStatusBarColor(this, Color.rgb(0, 172, 193));

        setSwipeBackEnable(true);   // 可以调用该方法，设置是否允许滑动退出
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        mSwipeBackLayout.setEdgeSize(200);

        //设置和toolbar相关的
        Toolbar toolbar = (Toolbar) findViewById(R.id.audio_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mLayout = (LinearLayout) findViewById(R.id.layout);
//        newCreditSesameView = (NewCreditSesameView) findViewById(R.id.sesame_view);
        /*
         * 进行一些，初始化
         */
        //volume = (TextView) findViewById(R.id.volume);
        myHandler = new MyHandler();
        MyAudio = new Audio(myHandler);
        MyAudio.getNoiseLevel();//获取音量

        progressbar = (ColorArcProgressBar) findViewById(R.id.bar1);

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            /*
             * 当收到message的时候，这个函数被执行
             * 读取message的数据，并显示到文本框中
             */
            super.handleMessage(msg);
            Bundle b = msg.getData();
            Double sound = b.getDouble("sound");
            //AudioActivity.this.volume.setText(sound + "" + " 分贝");
            progressbar.setCurrentValues(new Double(sound).intValue());
//            newCreditSesameView.setSesameValues(new Double(sound*10).intValue());
            startColorChangeAnim();
        }
    }

    public void startColorChangeAnim() {
        ObjectAnimator animator = ObjectAnimator.ofInt(mLayout, "backgroundColor", mColors);
        animator.setDuration(3000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.start();
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

    @Override
    public void onBackPressed() {
        MyAudio.cancel();
        finish();
        super.onBackPressed();
    }
}
