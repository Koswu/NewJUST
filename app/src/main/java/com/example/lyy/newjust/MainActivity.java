package com.example.lyy.newjust;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lyy.newjust.gson.Weather;
import com.example.lyy.newjust.util.HttpUtil;
import com.example.lyy.newjust.util.Utility;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.RationaleListener;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private String headPicUrl;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ImageView head_image_view;

    private SharedPreferences.Editor editor;

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeStatusBar();  //将背景图和状态栏融合到一起的方法

        obtain_permission();//获取权限

        loadHeadPic();      //添加每日一图

        init();             //初始化相关控件

    }

    //将背景图和状态栏融合到一起
    private void changeStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //权限获取
    private void obtain_permission() {
        AndPermission.with(this)
                .requestCode(200)
                .permission(
                        Permission.STORAGE
                )
                .callback(listener)
                .start();
    }

    //权限获取的监听器
    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。

            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == 200) {
                // TODO ...
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 200) {
                // TODO ...
                Toast.makeText(getApplicationContext(), "您还未获取权限", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void init() {

        //设置和toolbar相关的
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("New JUST");

        //设置顶部照片
        head_image_view = (ImageView) findViewById(R.id.head_image_view);

        //设置有关存储信息的
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        headPicUrl = sharedPreferences.getString("head_pic", null);
        Log.d(TAG, "init: " + headPicUrl);
        if (headPicUrl != null) {
            Glide.with(this).load(headPicUrl).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(head_image_view);
        } else {
            loadHeadPic();
        }

        head_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HeadImageActivity.class);
                intent.putExtra("headPicUrl", headPicUrl);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requestWeather();

    }

    //发送查询天气的请求
    private void requestWeather() {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=CN101190301&key=38c845e8310644ee83a8a7bba9b9be64";
        HttpUtil.sendHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, "onResponse: " + responseText);
                if (response!=null){
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    parseWeatherData(weather);
                }

            }
        });
    }

    private void parseWeatherData(Weather weather) {
        final String degree = weather.now.temperature + "℃";
        final String weatherInfo = weather.now.more.info;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                collapsingToolbarLayout.setTitle(degree + " " + weatherInfo);
            }
        });

    }

    //发送加载首页图片的请求
    private void loadHeadPic() {
        String requestHeadPic = "http://120.25.88.41:8088/img";
        HttpUtil.sendHttpRequest(requestHeadPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String headPic = response.body().string();
                editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("head_pic", headPic);
                headPicUrl = headPic;
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(headPicUrl).into(head_image_view);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_about:
                Toast.makeText(MainActivity.this, "你点击了关于我们按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_donate:
                Toast.makeText(MainActivity.this, "你点击了支持捐赠按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_update:
                Toast.makeText(MainActivity.this, "你点击了更换背景按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_exit:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_grade:
                Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_library:
                Toast.makeText(MainActivity.this, "你点击了馆藏查询按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
                Toast.makeText(MainActivity.this, "你点击了设置按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_classroom:
                Toast.makeText(MainActivity.this, "你点击了查询空教室按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_theme:
                Toast.makeText(MainActivity.this, "你点击了更换皮肤按钮", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //点击两次返回键退出
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                    return true;
                } else {
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
