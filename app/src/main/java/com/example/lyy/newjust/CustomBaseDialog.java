package com.example.lyy.newjust;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.animation.SlideEnter.SlideBottomEnter;
import com.flyco.dialog.widget.base.BaseDialog;

/**
 * Created by lyy on 2017/10/15.
 */

public class CustomBaseDialog extends BaseDialog<CustomBaseDialog> {
    private Context context;
    private TextView tv_general;
    private TextView tv_love;
    private ImageView iv_dialog;

    private String general_Info, love_Info, iv_url;

    public CustomBaseDialog(Context context, String general_Info, String love_Info, String iv_url) {
        super(context);
        this.context = context;
        this.love_Info = love_Info;
        this.iv_url = iv_url;
        this.general_Info = general_Info;
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new SlideBottomEnter());

        // dismissAnim(this, new ZoomOutExit());
        View view = View.inflate(context, R.layout.dialog_customize, null);
        tv_general = view.findViewById(R.id.tv_general);
        tv_love = view.findViewById(R.id.tv_love);
        iv_dialog = view.findViewById(R.id.iv_dialog);

        tv_general.setText(general_Info);
        tv_love.setText(love_Info);
        Glide.with(context).load(iv_url).into(iv_dialog);
        return view;
    }

    @Override
    public void setUiBeforShow() {
        tv_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tv_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
