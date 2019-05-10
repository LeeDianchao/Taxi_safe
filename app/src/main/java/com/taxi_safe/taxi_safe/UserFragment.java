package com.taxi_safe.taxi_safe;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class UserFragment extends Fragment {

    private ImageView iv_head_icon;
    private TextView tv_user_name;
    private LinearLayout ll_head;
    private RelativeLayout rl_taxi_history;
    private RelativeLayout rl_setting;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ll_head = view.findViewById(R.id.ll_head);
        iv_head_icon = view.findViewById(R.id.iv_head_icon);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        rl_taxi_history =  view.findViewById(R.id.rl_taxi_history);
        rl_setting = view.findViewById(R.id.rl_setting);
        setLoginParams(AnalysisUtils.readLoginStatus(getActivity()));
        ll_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AnalysisUtils.readLoginStatus(getActivity())) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    //startActivity(intent);  若是登录完自动跳转到课程界面，则直接用这个方法即可，这里修改以防后面需要
                    /**
                     * 优化修改，注意，这里不是从fragment打开activity，而是从主页活动打开登陆活动
                     * 若不是，则无法在主页活动直接使用onActivityResult()
                     * **/
                    getActivity().startActivityForResult(intent, 1);
                }
            }
        });
        rl_taxi_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AnalysisUtils.readLoginStatus(getActivity())) {
                    Toast.makeText(getActivity(), "乘车记录界面尚未开发，敬请期待", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AnalysisUtils.readLoginStatus(getActivity())) {
                    /*Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivityForResult(intent, 1);*/
                    //Toast.makeText(getActivity(), "设置界面", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), SetActivity.class));
                } else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 这个方法用在onViewCreated()，每次初始化这个界面都会启动
     * 通过登录后留在此页面并且立刻刷新用户名会在MainActivity的onActivityResult中处理
     **/
    private void setLoginParams(boolean isLogin) {
        if (isLogin) {
            tv_user_name.setText(AnalysisUtils.readLoginUserName(getActivity()));
        } else {
            tv_user_name.setText("点击登录");
        }
    }

}
