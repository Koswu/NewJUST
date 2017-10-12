package com.example.lyy.newjust;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lyy.newjust.db.Subjects;
import com.example.lyy.newjust.gson.Subject;
import com.example.lyy.newjust.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SubjectActivity extends ActionBarActivity implements MaterialTabListener {

    private static final String TAG = "SubjectActivity";


    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

//        //将背景图和状态栏融合到一起的方法
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            );
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

        searchScoreRequest();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }


        tabHost = (MaterialTabHost) findViewById(R.id.tabHost);
        pager = (ViewPager) findViewById(R.id.pager);

        // init view pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);

            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }
    }

    //发出分数查询的请求
    private void searchScoreRequest() {
        String url = "http://120.25.88.41";

        HttpUtil.sendHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                parseJSONToDB(responseText);
            }
        });
    }

    //对服务器响应的数据进行接收同时保存到数据库中
    private void parseJSONToDB(String response) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            List<Subject> subjectList = gson.fromJson(response, new TypeToken<List<Subject>>() {
            }.getType());
            for (Subject subject : subjectList) {
                Subjects subjects = new Subjects();
                subjects.setCourse_name(subject.getCourse_name());
                subjects.setCredit(subject.getCredit());
                subjects.setExamination_method(subject.getExamination_method());
                subjects.setScore(subject.getScore());
                subjects.setStart_semester(subject.getStart_semester());
                subjects.save();
            }
        } else {
            Toast.makeText(getApplicationContext(), "服务器没有响应", Toast.LENGTH_SHORT).show();
        }
    }

    //从数据库中读取数据
    private void queryDataFromDB() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Subjects> subjectsList = DataSupport.findAll(Subjects.class);
                for (Subjects subjects : subjectsList) {
                    Log.d(TAG, "所在学年是：" + subjects.getStart_semester());
                    Log.d(TAG, "该科名称是：" + subjects.getCourse_name());
                    Log.d(TAG, "考试类别是：" + subjects.getExamination_method() + "\n");
                    Log.d(TAG, "该科学分是：" + subjects.getCredit() + "\n");
                    Log.d(TAG, "考试分数是：" + subjects.getScore() + "\n");

                }
            }
        });

    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0) {
            Toast.makeText(getApplicationContext(), "这是考试课", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "这是考查课", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            return new FragmentLayout();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "考试课";
            } else {
                return "考查课";
            }
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
}
