package com.example.lyy.newjust.activity.Setting;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyy.newjust.R;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.githang.statusbar.StatusBarCompat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class ProfileActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;


    private Uri imageUri;

    private TextView tv_constellation;
    private TextView tv_birthday;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String birthday;
    private String constellation;
    private String constellation_en;
    private int year, month, day;

    private String imageBase64;

    private CircleImageView civ_header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        StatusBarCompat.setStatusBarColor(this, Color.rgb(0, 127, 193));
        setSwipeBackEnable(true);   // 可以调用该方法，设置是否允许滑动退出
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        mSwipeBackLayout.setEdgeSize(200);
        init();
    }

    @SuppressLint("CommitPrefEdits")
    private void init() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_back_blue);
        }

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();

        constellation = sharedPreferences.getString("constellation", null);
        constellation_en = sharedPreferences.getString("constellation_en", null);
        birthday = sharedPreferences.getString("birthday", null);
        year = sharedPreferences.getInt("year", 1994);
        month = sharedPreferences.getInt("month", 12);
        day = sharedPreferences.getInt("day", 1);

        imageBase64 = sharedPreferences.getString("image", null);

        LinearLayout ll_birthday = (LinearLayout) findViewById(R.id.ll_birthday);
        LinearLayout ll_constellation = (LinearLayout) findViewById(R.id.ll_constellation);
        ll_birthday.setOnClickListener(this);
        ll_constellation.setOnClickListener(this);

        tv_constellation = (TextView) findViewById(R.id.tv_constellation);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);

        if (constellation != null)
            tv_birthday.setText(birthday);
        if (constellation != null)
            tv_constellation.setText(constellation);

        civ_header = (CircleImageView) findViewById(R.id.civ_header);
        civ_header.setOnClickListener(this);
        if (imageBase64 != null) {
            byte[] byte64 = Base64.decode(imageBase64, 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(byte64);
            Bitmap bitmap = BitmapFactory.decodeStream(bais);
            civ_header.setImageBitmap(bitmap);
        }

    }

    private void take_photos() {
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(ProfileActivity.this, "com.example.lyy.newjust.fileprovider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void choosePhotoFromGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    //显示日期选择弹窗
    private void showDatePicker() {
        DatePickerDialog dd = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String temp_constellation = getConstellation(month + 1, day);
                String temp_constellation_en = getENConstellation(temp_constellation);
                String temp_birthday = year + "年" + (month + 1) + "月" + day + "日";

                tv_birthday.setText(temp_birthday);
                tv_constellation.setText(temp_constellation);

                editor.putInt("year", year);
                editor.putInt("month", month);
                editor.putInt("day", day);

                editor.putString("birthday", temp_birthday);
                editor.putString("constellation", temp_constellation);
                editor.putString("constellation_en", temp_constellation_en);
                editor.apply();
            }
        }, year, month, day);
        dd.show();
    }

    //将日期转换成星座
    private String getConstellation(int month, int day) {
        String[] starArr = {"魔羯座", "水瓶座", "双鱼座", "白羊座",
                "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座"};
        int[] DayArr = {22, 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22};  // 两个星座分割日
        int index = month;
        // 所查询日期在分割日之前，索引-1，否则不变
        if (day < DayArr[month - 1]) {
            index = index - 1;
        }
        // 返回索引指向的星座string
        return starArr[index];
    }

    //将中文的星座转换成英文的星座
    public static String getENConstellation(String select) {
        String horoscope_name[] = {"白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "魔羯座", "水瓶座", "双鱼座"};
        String horoscope_english[] = {"aries", "taurus", "gemini", "cancer", "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius", "aquarius", "pisces"};
        for (int i = 0; i < horoscope_name.length; i++) {
            if (horoscope_name[i].contains(select)) {
                return horoscope_english[i];
            }
        }
        return select;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Intent cropIntent = new Intent(ProfileActivity.this, CropViewActivity.class);
                        cropIntent.putExtra("uri", imageUri.toString());
                        startActivity(cropIntent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        Uri uri = data.getData();
                        Log.d(TAG, "onActivityResult: " + uri);
                        Intent cropIntent = new Intent(ProfileActivity.this, CropViewActivity.class);
                        cropIntent.putExtra("uri", uri.toString());
                        startActivity(cropIntent);
                    } else {
                        Toast.makeText(ProfileActivity.this, "安卓版本过低", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_birthday:
                showDatePicker();
                break;
            case R.id.ll_constellation:
                if (tv_constellation.getText().equals(""))
                    Toasty.warning(ProfileActivity.this, "你还未设置星座，点击生日设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.civ_header:
                final String[] stringItems = {"拍照", "从相册中选择"};
                final ActionSheetDialog dialog = new ActionSheetDialog(ProfileActivity.this, stringItems, null);
                dialog.isTitleShow(false).show();

                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                take_photos();
                                break;
                            case 1:
                                choosePhotoFromGallery();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
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
