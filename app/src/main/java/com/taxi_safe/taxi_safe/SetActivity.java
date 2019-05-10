package com.taxi_safe.taxi_safe;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SetActivity extends AppCompatActivity {

    //来自main_title_bar.xml
    private TextView tv_main_title;//标题
    private TextView tv_back;//返回按钮
    private RelativeLayout title_bar;// android:id="@+id/title_bar"

    private Button Log_off;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        tv_back = findViewById(R.id.tv_back);
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("注册");
        title_bar = findViewById(R.id.include);
        title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));

        Log_off=findViewById(R.id.Log_off);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回键
                SetActivity.this.finish();
            }
        });

        Log_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录
                if (AnalysisUtils.readLoginStatus(SetActivity.this)){
                    //已登陆的话，清除登陆状态
                    AnalysisUtils.clearLoginStatus(SetActivity.this);
                }
                startActivity(new Intent(SetActivity.this, MainActivity.class));
            }
        });
    }
}
