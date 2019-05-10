package com.taxi_safe.taxi_safe;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    //来自main_title_bar.xml
    private TextView tv_main_title;//标题
    private TextView tv_back;//返回按钮
    private RelativeLayout title_bar;// android:id="@+id/title_bar"
    //来自activity_main.xml
    private RelativeLayout main_body;
    private TextView bottom_bar_text_main;
    private ImageView bottom_bar_image_main;
    private TextView bottom_bar_text_myinfo;
    private ImageView bottom_bar_image_myinfo;

    private LinearLayout main_bottom_bar;

    private RelativeLayout bottom_bar_main_btn;
    private RelativeLayout bottom_bar_myinfo_btn;

    private String User_type;
    private String userName;

    public static int CAMERA_OK = 100; //相机权限
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            boolean isLogin = data.getBooleanExtra("isLogin", false);

            //从登录活动获得isLogin==true,从设置活动获得isLogin==false，他们的请求码都是1
            //之后还可以根据请求码和结果码完成更多需求
            if (isLogin) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new MainFragment()).commit();
                clearBottomImageState();
                setSelectedStatus(0);
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new UserFragment()).commit();
                clearBottomImageState();
                setSelectedStatus(1);
            }
        }

        if (requestCode == 10  && resultCode == RESULT_OK){
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"非常感谢您的同意",Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(MainActivity.this,"当您点击我们会再次询问",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        initNavigation();
        initBody();
        initBottomBar();
        setInitStatus();

        //动态 申请相机权限
        int hasCAMERAPermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA);
        if (hasCAMERAPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_OK);
            Log.i("MainActivity", "Request permission.CAMERA");
        }
    }
    private void initNavigation() {
        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.INVISIBLE);
        tv_main_title = findViewById(R.id.tv_main_title);
        title_bar = findViewById(R.id.include3);
        title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
    }

    private void initBody() {
        main_body = findViewById(R.id.main_body);
    }

    private void initBottomBar() {
        bottom_bar_text_main = findViewById(R.id.bottom_bar_text_main);
        bottom_bar_image_main = findViewById(R.id.bottom_bar_image_main);
        bottom_bar_main_btn = findViewById(R.id.bottom_bar_main_btn);

        bottom_bar_text_myinfo = findViewById(R.id.bottom_bar_text_myinfo);
        bottom_bar_image_myinfo = findViewById(R.id.bottom_bar_image_myinfo);
        bottom_bar_myinfo_btn = findViewById(R.id.bottom_bar_myinfo_btn);
        main_bottom_bar = findViewById(R.id.main_bottom_bar);
        setListener();
    }

    private void setListener() {
        for (int i = 0; i < main_bottom_bar.getChildCount(); i++) {
            main_bottom_bar.getChildAt(i).setOnClickListener(this);
        }
    }

    private void setInitStatus() {
        clearBottomImageState();
        setSelectedStatus(0);
        getSupportFragmentManager().beginTransaction().add(R.id.main_body, new MainFragment()).commit();
    }

    private void setSelectedStatus(int index) {
        switch (index) {
            case 0:
                //mCourseBtn.setSelected(true);
                bottom_bar_image_main.setImageResource(R.drawable.main_icon_selected);
                bottom_bar_text_main.setTextColor(Color.parseColor("#0097f7"));
                tv_main_title.setText("主页");
                break;
            case 1:
                //mMyInfoBtn.setSelected(true);
                bottom_bar_image_myinfo.setImageResource(R.drawable.main_my_icon_selected);
                bottom_bar_text_myinfo.setTextColor(Color.parseColor("#0097f7"));
                title_bar.setVisibility(View.VISIBLE);
                tv_main_title.setText("我的");
                break;
        }
    }

    private void clearBottomImageState() {
        bottom_bar_text_main.setTextColor(Color.parseColor("#666666"));
        bottom_bar_text_myinfo.setTextColor(Color.parseColor("#666666"));

        bottom_bar_image_main.setImageResource(R.drawable.main_icon);
        bottom_bar_image_myinfo.setImageResource(R.drawable.main_my_icon);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_bar_main_btn:
                clearBottomImageState();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new MainFragment()).commit();
                setSelectedStatus(0);
                break;
            case R.id.bottom_bar_myinfo_btn:
                clearBottomImageState();
                setSelectedStatus(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_body, new UserFragment()).commit();
                break;
        }
    }

    protected long exitTime;//记录第一次点击的时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                if (AnalysisUtils.readLoginStatus(this)) {
                    //已登陆的话，清除登陆状态
                    AnalysisUtils.clearLoginStatus(this);
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 100:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //获取到了摄像头的权限
                    Toast.makeText(MainActivity.this,"获取到相机权限",Toast.LENGTH_SHORT).show();
                    Log.i("MainActivity", "Get permission.CAMERA");
                } else {
                    //Toast.makeText(MainActivity.this,"请手动打开相机权限",Toast.LENGTH_SHORT).show();
                    startAlertDiaLog();
                }
                break;

            default:
                break;
        }
    }

    public void startAlertDiaLog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("说明");
        alert.setMessage("需要相机权限，请手动打开相机权限");
        alert.setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //打开手机的设置页面(打开相机权限)
                startSetting();
            }
        });

        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //如果用户还不打开 只能等用户下次点击时再次询问
                Toast.makeText(MainActivity.this,"当您点击我们会再次询问",Toast.LENGTH_SHORT).show();
            }
        });
        alert.create();
        alert.show();
    }

    public void startSetting(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivityForResult(intent,10);
    }
}

