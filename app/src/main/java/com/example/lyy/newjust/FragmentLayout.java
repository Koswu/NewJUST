package com.example.lyy.newjust;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.lyy.newjust.adapter.Subject;
import com.example.lyy.newjust.adapter.SubjectAdapter;
import com.example.lyy.newjust.db.Subjects;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class FragmentLayout extends Fragment {

    private boolean flag = true;

    private List<Subject> subjects_List;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, null);
        if (flag) {
            ListView listView = view.findViewById(R.id.subject_list_item);
            subjects_List = new ArrayList<>();
            List<Subjects> dbSubjectsList = DataSupport.where("examination_method=?", "考试").find(Subjects.class);
            if (subjects_List.size() != 0) {
                subjects_List.clear();
                for (Subjects dbSubjects : dbSubjectsList) {
                    Subject subject = new Subject(dbSubjects.getCourse_name(), dbSubjects.getCredit(), dbSubjects.getScore());
                    subjects_List.add(subject);
                }
            } else {
                for (Subjects dbSubjects : dbSubjectsList) {
                    Subject subject = new Subject(dbSubjects.getCourse_name(), dbSubjects.getCredit(), dbSubjects.getScore());
                    subjects_List.add(subject);
                }
            }
            SubjectAdapter subjectAdapter = new SubjectAdapter(getActivity(), R.layout.subject_item, subjects_List);
            listView.setAdapter(subjectAdapter);
            subjects_List.clear();
            flag = false;
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        subjects_List.clear();
    }
}
