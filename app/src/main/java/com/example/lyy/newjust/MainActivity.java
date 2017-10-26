package com.example.lyy.newjust;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lyy.newjust.gson.Weather;
import com.example.lyy.newjust.service.LongRunningService;
import com.example.lyy.newjust.util.AppConstants;
import com.example.lyy.newjust.util.HttpUtil;
import com.example.lyy.newjust.util.SpUtils;
import com.example.lyy.newjust.util.Util;
import com.githang.statusbar.StatusBarCompat;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static int index = 0;

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    private String headPicUrl;
    private String imageBase64;
    private String constellation_en;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ImageView head_image_view;

    private CircleImageView civ_header;

    private FlowingDrawer mDrawer;

    private AlertDialog dialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 判断是否是第一次开启应用
        boolean isFirstOpen = SpUtils.getBoolean(this, AppConstants.FIRST_OPEN);
        Log.d(TAG, "onCreate: " + isFirstOpen);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        StatusBarCompat.setStatusBarColor(this, Color.rgb(0, 127, 193));

        Intent service = new Intent(this, LongRunningService.class);
        startService(service);

        StatusBarCompat.setStatusBarColor(this, Color.rgb(0, 127, 193));

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
                        Permission.STORAGE,
                        Permission.CAMERA,
                        Permission.MICROPHONE
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

    //初始化相关控件
    private void init() {

        //设置和toolbar相关的
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_menu);
        }
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("New JUST");

        //设置顶部照片
        head_image_view = (ImageView) findViewById(R.id.head_image_view);

        //设置有关存储信息的
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
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

        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
//        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
//            @Override
//            public void onDrawerStateChange(int oldState, int newState) {
//                if (newState == ElasticDrawer.STATE_CLOSED) {
//                    Log.i("MainActivity", "Drawer STATE_CLOSED");
//                } else if (newState == ElasticDrawer.STATE_OPEN) {
//                    Log.i("MainActivity", "Drawer STATE_OPEN");
//                }
//            }
//
//            @Override
//            public void onDrawerSlide(float openRatio, int offsetPixels) {
//                //Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
//            }
//        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setBackground(getResources().getDrawable(R.drawable.bg_our));
        navigationView.setNavigationItemSelectedListener(this);

        requestWeather();

        //设置菜单栏头像
        imageBase64 = sharedPreferences.getString("image", null);
        civ_header = (CircleImageView) findViewById(R.id.civ_header);
        civ_header.setOnClickListener(this);
        if (imageBase64 != null) {
            byte[] byte64 = Base64.decode(imageBase64, 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(byte64);
            Bitmap bitmap = BitmapFactory.decodeStream(bais);
            civ_header.setImageBitmap(bitmap);
        }

        ImageView iv_constellation = (ImageView) findViewById(R.id.iv_constellation);
        ImageView iv_health = (ImageView) findViewById(R.id.iv_health);
        ImageView iv_weibo = (ImageView) findViewById(R.id.iv_weibo);
        ImageView iv_schedule = (ImageView) findViewById(R.id.iv_schedule);
        ImageView iv_one = (ImageView) findViewById(R.id.iv_one);
        ImageView iv_memory = (ImageView) findViewById(R.id.iv_memory);
        ImageView iv_history = (ImageView) findViewById(R.id.iv_history);

        Glide.with(this).load(R.drawable.bg_constellation).into(iv_constellation);
        Glide.with(this).load(R.drawable.bg_health).into(iv_health);
        Glide.with(this).load(R.drawable.bg_weibo).into(iv_weibo);
        Glide.with(this).load(R.drawable.bg_every_day).into(iv_one);
        Glide.with(this).load(R.drawable.bg_memory).into(iv_memory);
        Glide.with(this).load(R.drawable.bg_schedule).into(iv_schedule);
        Glide.with(this).load(R.drawable.bg_history).into(iv_history);

        iv_constellation.setOnClickListener(this);
        iv_health.setOnClickListener(this);
        iv_one.setOnClickListener(this);
        iv_weibo.setOnClickListener(this);
        iv_memory.setOnClickListener(this);
        iv_schedule.setOnClickListener(this);
        iv_history.setOnClickListener(this);

        LinearLayout nav_todo = (LinearLayout) findViewById(R.id.nav_todo);
        LinearLayout nav_eat = (LinearLayout) findViewById(R.id.nav_eat);
        nav_todo.setOnClickListener(this);
        nav_eat.setOnClickListener(this);


        //设置底部弹窗
        showBoomMenu();
    }

    //设置底部弹窗按钮--------------------------开始----------------------------
    private void showBoomMenu() {
        BoomMenuButton boomMenuButton = (BoomMenuButton) findViewById(R.id.bmb);
        for (int i = 0; i < boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
            TextOutsideCircleButton.Builder builder = new TextOutsideCircleButton.Builder()
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            switch (index) {
                                case 0:
                                    Intent emsIntent = new Intent(MainActivity.this, EMSActivity.class);
                                    startActivity(emsIntent);
                                    break;
                                case 1:
                                    Intent ocrIntent = new Intent(MainActivity.this, OCRActivity.class);
                                    startActivity(ocrIntent);
                                    break;
                                case 2:
                                    Intent audioIntent = new Intent(MainActivity.this, AudioActivity.class);
                                    startActivity(audioIntent);
                                    break;
                                case 3:
                                    Intent translateIntent = new Intent(MainActivity.this, TranslateActivity.class);
                                    startActivity(translateIntent);
                                    break;
                                case 4:
                                    Intent eipIntent = new Intent(MainActivity.this, EipActivity.class);
                                    startActivity(eipIntent);
                                    break;
                            }
                        }
                    })
                    .imagePadding(new Rect(25, 25, 25, 25))
                    .normalImageRes(getImageResource())
                    .normalText(getext())
                    .textTopMargin(10)
                    .textSize(12);
            boomMenuButton.addBuilder(builder);
        }
    }

    static String getext() {
        if (index >= text.length) index = 0;
        return text[index++];

    }

    private static String[] text = new String[]{"快递查询", "文字识别", "分贝计", "在线翻译", "表情包制作"};
    private static int imageResourceIndex = 0;

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    private static int[] imageResources = new int[]{
            R.drawable.ic_menu_ems,
            R.drawable.ic_menu_ocr,
            R.drawable.ic_menu_eip,
            R.drawable.ic_menu_translate,
            R.drawable.ic_menu_library,
    };
    //设置底部弹窗按钮--------------------------结束----------------------------

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
                    Log.d(TAG, "onResponse: " + responseText);
                    Weather weather = Util.handleWeatherResponse(responseText);
                    parseWeatherData(weather);
                } else {
                    Toast.makeText(getApplicationContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //解析天气请求的数据
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
        String requestHeadPic = "http://120.25.88.41/just/img";
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
                    Log.d(TAG, "onResponse: " + pic_url);
                    final String general_Info = object1.getString("综合运势");
                    final String love_Info = object2.getString("爱情运势");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showConstellation(general_Info, love_Info, pic_url);
                            dialog.dismiss();
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
            case R.id.action_exit:
                this.finish();
                break;
            case android.R.id.home:
                mDrawer.openMenu();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        mDrawer.closeMenu();

        switch (item.getItemId()) {
            case R.id.nav_grade:
                Intent search_subject_intent = new Intent(MainActivity.this, SubjectActivity.class);
                startActivity(search_subject_intent);
                break;
            case R.id.nav_library:
                Toast.makeText(MainActivity.this, "你点击了馆藏查询按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_pe:
                Toast.makeText(MainActivity.this, "你点击了体育课查询按钮", Toast.LENGTH_SHORT).show();
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

        return true;
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
            case R.id.civ_header:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.iv_constellation:
                constellation_en = sharedPreferences.getString("constellation_en", null);
                if (constellation_en != null) {
                    dialog = new SpotsDialog(MainActivity.this);
                    dialog.show();
                    requestConstellation(constellation_en);
                } else {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("未检测到你的星座！")
                            .setContentText("需要前往设置你的星座吗？")
                            .setCancelText("关闭")
                            .setConfirmText("设置")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                    sDialog.dismiss();
                                }
                            })
                            .show();
                }
                break;
            case R.id.iv_memory:
                Toast.makeText(MainActivity.this, "最美时光", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_schedule:
                Toast.makeText(MainActivity.this, "课程表", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_history:
                Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.iv_weibo:
                Intent weiboIntent = new Intent(MainActivity.this, WeiBoActivity.class);
                startActivity(weiboIntent);
                break;
            case R.id.iv_one:
                Intent oneIntent = new Intent(MainActivity.this, OneActivity.class);
                startActivity(oneIntent);
                break;
            case R.id.nav_todo:
                Intent todoIntent = new Intent(MainActivity.this, ToDoActivity.class);
                startActivity(todoIntent);
                break;
            case R.id.nav_eat:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageBase64 = sharedPreferences.getString("image", null);
        Log.d(TAG, "onResume: " + imageBase64);
        if (imageBase64 != null) {
            byte[] byte64 = Base64.decode(imageBase64, 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(byte64);
            Bitmap bitmap = BitmapFactory.decodeStream(bais);
            civ_header.setImageBitmap(bitmap);
        }
    }

}
