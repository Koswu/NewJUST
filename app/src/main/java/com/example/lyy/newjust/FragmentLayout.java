package com.example.lyy.newjust;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lyy.newjust.adapter.Subject;
import com.example.lyy.newjust.adapter.SubjectAdapter;
import com.example.lyy.newjust.gson.g_Subject;
import com.example.lyy.newjust.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentLayout extends Fragment {

    private static final String TAG = "FragmentLayout";
    private ArrayList<Subject> adapter_list_kaoshi;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, null);
        searchScoreRequest();

        adapter_list_kaoshi = new ArrayList<>();
        SubjectAdapter subjectAdapter = new SubjectAdapter(getActivity(), R.layout.subjects_item, adapter_list_kaoshi);
        ListView listView = view.findViewById(R.id.subject_list_item);
        listView.setAdapter(subjectAdapter);

        return view;
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
                parseJSONData(responseText);
            }
        });
    }

    //对服务器响应的数据进行接收同时保存到数据库中
    private void parseJSONData(String response) {
        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            List<g_Subject> gSubjectList = gson.fromJson(response, new TypeToken<List<g_Subject>>() {
            }.getType());
            for (int i = 0; i < gSubjectList.size(); i++) {
                if (gSubjectList.get(i).getExamination_method().equals("考试")) {
                    Subject subject = new Subject(gSubjectList.get(i).getCourse_name(), gSubjectList.get(i).getCredit(), gSubjectList.get(i).getScore());
                    adapter_list_kaoshi.add(subject);
                }
            }
        } else {
            Toast.makeText(getActivity(), "服务器没有响应", Toast.LENGTH_SHORT).show();
        }
    }


}
