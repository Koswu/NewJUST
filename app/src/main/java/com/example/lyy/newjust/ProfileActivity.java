package com.example.lyy.newjust;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";

    private TextView tv_constellation;
    private TextView tv_birthday;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String birthday;
    private String constellation;
    private String constellation_en;
    private int year, month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        }

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();

        constellation = sharedPreferences.getString("constellation", null);
        constellation_en = sharedPreferences.getString("constellation_en", null);
        birthday = sharedPreferences.getString("birthday", null);
        year = sharedPreferences.getInt("year", 1994);
        month = sharedPreferences.getInt("month", 12);
        day = sharedPreferences.getInt("day", 1);

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
    }

    //显示时间选择弹窗
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
        }
    }


}
