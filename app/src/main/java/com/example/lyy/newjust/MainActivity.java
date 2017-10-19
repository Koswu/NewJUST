package com.example.lyy.newjust;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

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

        //设置状态栏和toolbar颜色一致
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        changeStatusBar();  //将背景图和状态栏融合到一起的方法

        obtain_permission();//获取权限

        init();             //初始化相关控件

        loadHeadPic();      //添加每日一图

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
        setDrawerLeftEdgeSize(MainActivity.this, drawer, 1);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requestWeather();

        ImageView iv_constellation = (ImageView) findViewById(R.id.iv_constellation);
        ImageView iv_health = (ImageView) findViewById(R.id.iv_health);
        ImageView iv_eip = (ImageView) findViewById(R.id.iv_eip);
        ImageView iv_weibo = (ImageView) findViewById(R.id.iv_weibo);
        ImageView iv_schedule = (ImageView) findViewById(R.id.iv_schedule);
        Glide.with(this).load(R.drawable.bg_constellation).into(iv_constellation);
        Glide.with(this).load(R.drawable.bg_eip).into(iv_eip);
        Glide.with(this).load(R.drawable.bg_health).into(iv_health);
        Glide.with(this).load(R.drawable.bg_weibo).into(iv_weibo);
//        Glide.with(this).load(R.drawable.bg_schedule).into(iv_schedule);
        iv_constellation.setOnClickListener(this);
        iv_eip.setOnClickListener(this);
        iv_health.setOnClickListener(this);
        iv_weibo.setOnClickListener(this);
//        iv_schedule.setOnClickListener(this);
        //changeLight(iv_health, -50);
        changeLight(iv_constellation, -50);
        changeLight(iv_eip, -50);
        changeLight(iv_weibo, -50);
//        changeLight(iv_schedule,-50);
    }

    //改变图片的亮度方法 0--原样  >0---调亮  <0---调暗
    private void changeLight(ImageView imageView, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0,
                brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
    }

    //解决DrawerLayout不能全屏滑动的问题
    private void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) return;
        try {
            // 找到 ViewDragHelper 并设置 Accessible 为true
            Field leftDraggerField =
                    drawerLayout.getClass().getDeclaredField("mLeftDragger");//Right
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);

            // 找到 edgeSizeField 并设置 Accessible 为true
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);

            // 设置新的边缘大小
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (displaySize.x *
                    displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }


    //发送查询天气的请求
    private void requestWeather() {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=CN101190301&key=38c845e8310644ee83a8a7bba9b9be64";
        HttpUtil.sendHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, "onResponse: " + responseText);
                if (response.isSuccessful()) {
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    parseWeatherData(weather);
                } else {
                    Toast.makeText(getApplicationContext(), "服务器错误", Toast.LENGTH_SHORT).show();
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
        String requestHeadPic = "http://111.230.129.182:8080/just/img";
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

    //发送星座运势的请求
    private void requestConstellation(String constellation) {
        final String requestUrl = "http://120.25.88.41/horoscope/" + constellation;
        HttpUtil.sendHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseText);
                    JSONObject object0 = jsonArray.getJSONObject(0);    //这个json对象包含“图片地址”信息
                    JSONObject object1 = jsonArray.getJSONObject(1);    //这个json对象包含“综合运势”信息
                    JSONObject object2 = jsonArray.getJSONObject(2);      //这个json对象包含“爱情运势”信息
                    final String pic_url = object0.getString("imgUrl");
                    final String general_Info = object1.getString("综合运势");
                    final String love_Info = object2.getString("爱情运势");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showConstellation(general_Info, love_Info, pic_url);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //显示星座运势信息
    private void showConstellation(String general_Info, String love_Info, String pic_url) {
        CustomBaseDialog customBaseDialog = new CustomBaseDialog(MainActivity.this, general_Info, love_Info, pic_url);
        customBaseDialog.onCreateView();
        customBaseDialog.setUiBeforShow();
        //点击空白区域能不能退出
        customBaseDialog.setCanceledOnTouchOutside(true);
        //按返回键能不能退出
        customBaseDialog.setCancelable(true);
        customBaseDialog.show();
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
            case R.id.action_bg:
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
                Intent search_subject_intent = new Intent(MainActivity.this, SubjectActivity.class);
                startActivity(search_subject_intent);
                break;
            case R.id.nav_library:
                Toast.makeText(MainActivity.this, "你点击了馆藏查询按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_myday:
                Intent to_do_intent = new Intent(MainActivity.this, ToDoActivity.class);
                startActivity(to_do_intent);
                break;
            case R.id.nav_setting:
                Intent settings_intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings_intent);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_constellation:
                requestConstellation("libra");
                break;
        }
    }

}
