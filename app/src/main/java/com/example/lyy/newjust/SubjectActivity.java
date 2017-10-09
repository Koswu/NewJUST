package com.example.lyy.newjust;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyy.newjust.db.Subjects;
import com.example.lyy.newjust.gson.Subject;
import com.example.lyy.newjust.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SubjectActivity extends AppCompatActivity {

    private static final String TAG = "SubjectActivity";

//    private ListView listView;
//
//    private ArrayAdapter<String> adapter;
//
//    private List<String> dataList = new ArrayList<>();
//
//    private List<Subjects> subjectsList;

    private String course_name, credit, examination_method, score, start_semester;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        searchScoreRequest();

        textView = (TextView) findViewById(R.id.textView);

        Button button = (Button) findViewById(R.id.refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchScoreRequest();
                queryDataFromDB();
            }
        });

//        listView = (ListView) findViewById(R.id.list_view);
//        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, dataList);
//        listView.setAdapter(adapter);


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
                final String responseText = response.body().string();
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
                    textView.append("所在学年是：" + subjects.getStart_semester() + "\n");
                    textView.append("该科名称是：" + subjects.getCourse_name() + "\n");
                    textView.append("考试类别是：" + subjects.getExamination_method() + "\n");
                    textView.append("该科学分是：" + subjects.getCredit() + "\n");
                    textView.append("考试分数是：" + subjects.getScore() + "\n");
                    textView.append("\n");
                }
            }
        });

    }
}
