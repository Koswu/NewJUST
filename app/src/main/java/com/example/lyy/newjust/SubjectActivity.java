package com.example.lyy.newjust;

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
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lyy.newjust.adapter.Subject;
import com.example.lyy.newjust.adapter.SubjectAdapter;
import com.example.lyy.newjust.db.Subjects;
import com.example.lyy.newjust.gson.g_Subject;
import com.example.lyy.newjust.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<com.example.lyy.newjust.adapter.Subject> subjects_List = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        searchScoreRequest();
        init();
//        queryDataFromDB();

    }

    private void init() {
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
            List<g_Subject> gSubjectList = gson.fromJson(response, new TypeToken<List<g_Subject>>() {
            }.getType());
            for (g_Subject gSubject : gSubjectList) {
                Subjects dbSubjects = new Subjects();
                dbSubjects.setCourse_name(gSubject.getCourse_name());
                dbSubjects.setCredit(gSubject.getCredit());
                dbSubjects.setExamination_method(gSubject.getExamination_method());
                dbSubjects.setScore(gSubject.getScore());
                dbSubjects.setStart_semester(gSubject.getStart_semester());
                dbSubjects.save();
            }
            Log.d(TAG, "parseJSONToDB: " + gSubjectList.size());
        } else {
            Toast.makeText(getApplicationContext(), "服务器没有响应", Toast.LENGTH_SHORT).show();
        }
    }

    //查询考试课成绩
    private void queryDataType1FromDB() {
        subjects_List = new ArrayList<>();
        List<Subjects> dbSubjectsList = DataSupport.where("examination_method=?", "考试").find(Subjects.class);
        if (subjects_List.size() != 0) {
            subjects_List.clear();
            for (Subjects dbSubjects : dbSubjectsList) {
                Subject subject = new Subject(dbSubjects.getCourse_name(), dbSubjects.getCredit(), dbSubjects.getScore());
                subjects_List.add(subject);
            }
            Log.d(TAG, "queryDataFromDB: " + subjects_List.size());
        } else {
            for (Subjects dbSubjects : dbSubjectsList) {
                Subject subject = new Subject(dbSubjects.getCourse_name(), dbSubjects.getCredit(), dbSubjects.getScore());
                subjects_List.add(subject);
            }
        }
    }

    //查询考查课成绩
    private void queryDataType2FromDB() {
        subjects_List = new ArrayList<>();
        List<Subjects> dbSubjectsList = DataSupport.where("examination_method=?", "考查").find(Subjects.class);
        if (subjects_List.size() != 0) {
            subjects_List.clear();
            for (Subjects dbSubjects : dbSubjectsList) {
                Subject subject = new Subject(dbSubjects.getCourse_name(), dbSubjects.getCredit(), dbSubjects.getScore());
                subjects_List.add(subject);
            }
            Log.d(TAG, "queryDataFromDB: " + subjects_List.size());
        } else {
            for (Subjects dbSubjects : dbSubjectsList) {
                Subject subject = new Subject(dbSubjects.getCourse_name(), dbSubjects.getCredit(), dbSubjects.getScore());
                subjects_List.add(subject);
            }
        }
    }


    //显示考试课结果
    private void showResult() {
        SubjectAdapter subjectAdapter = new SubjectAdapter(SubjectActivity.this, R.layout.subject_item, subjects_List);
        FragmentLayout fragment = adapter.getCurrentFragment();
        View view = fragment.getView();
        ListView listView = view.findViewById(R.id.subject_list_item);
        listView.setAdapter(subjectAdapter);
        subjects_List.clear();
        Log.d(TAG, "showResult_1: " + "考试课" + subjects_List.size());
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0) {
            queryDataType1FromDB();
            showResult();
            Toast.makeText(getApplicationContext(), "这是考试课", Toast.LENGTH_SHORT).show();
        } else {
            queryDataType2FromDB();
            showResult();
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

        private FragmentLayout mCurrentFragment;

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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mCurrentFragment = (FragmentLayout) object;
            super.setPrimaryItem(container, position, object);
        }


        public FragmentLayout getCurrentFragment() {
            return mCurrentFragment;
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
    protected void onResume() {
        super.onResume();
        subjects_List.clear();
    }
}
