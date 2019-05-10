package com.taxi_safe.taxi_safe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class FindPwdActivity extends AppCompatActivity {

    private TextView tv_back;
    private TextView tv_main_title;
    private RelativeLayout rl_title_bar;
    private TextView tv_user_name;
    private EditText et_user_name;
    private TextView tv_user_identity;
    private EditText et_user_identity;
    private TextView tv_reset_pwd;
    private EditText et_reset_pwd;
    private TextView tv_reset_pwd_again;
    private EditText et_reset_pwd_again;
    private Button btn_validate;

    private  String userName,userIdentity,psw,psw_again;
    // 创建等待框
    private ProgressDialog dialog;
    //接口服务返回值
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        init();
    }
    private void init() {
        tv_back = findViewById(R.id.tv_back);
        tv_main_title = findViewById(R.id.tv_main_title);
        rl_title_bar = findViewById(R.id.title_bar);
        tv_user_name = findViewById(R.id.tv_user_name);
        et_user_name = findViewById(R.id.et_user_name);
        tv_user_identity=findViewById(R.id.tv_user_identity);
        et_user_identity = findViewById(R.id.et_user_identity);
        tv_reset_pwd = findViewById(R.id.tv_reset_pwd);
        et_reset_pwd =  findViewById(R.id.et_reset_pwd);
        tv_reset_pwd_again = findViewById(R.id.tv_reset_pwd_again);
        et_reset_pwd_again =  findViewById(R.id.et_reset_pwd_again);
        btn_validate = findViewById(R.id.btn_validate);

        tv_main_title.setText("找回密码");
        tv_user_name.setVisibility(View.VISIBLE);
        et_user_name.setVisibility(View.VISIBLE);

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindPwdActivity.this.finish();
            }
        });
        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    private void submit() {
        userName = et_user_name.getText().toString().trim();
        userIdentity = et_user_identity.getText().toString().trim();
        psw = et_reset_pwd.getText().toString().trim();
        psw_again = et_reset_pwd_again.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(FindPwdActivity.this, "请输入您的用户名", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(userIdentity)) {
            Toast.makeText(FindPwdActivity.this, "请输入要验证的身份证号", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(psw)) {
            Toast.makeText(FindPwdActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(psw_again)) {
            Toast.makeText(FindPwdActivity.this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
            return;
        } else if (!psw.equals(psw_again)) {
            Toast.makeText(FindPwdActivity.this, "两次输入的密码不一样", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // 提示框
            dialog = new ProgressDialog(FindPwdActivity.this);
            dialog.setTitle("提示");
            dialog.setMessage("正在修改密码，请稍后...");
            dialog.setCancelable(false);
            dialog.show();
            //启动后台异步线程进行连接webService操作，并且根据返回结果在主线程中改变UI
            FindPwdActivity.QueryAddressTask queryAddressTask = new FindPwdActivity.QueryAddressTask();
            //启动后台任务
            queryAddressTask.execute(userIdentity, userName, psw);
        }
    }
        class QueryAddressTask extends AsyncTask<String, Integer, String> {
            @Override
            protected String doInBackground(String... params) {
                try {
                    result = getRemoteInfo(params[0],params[1],params[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //将结果返回给onPostExecute方法
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                dialog.dismiss();
                if(result.equals("3")){
                    Toast.makeText(FindPwdActivity.this, "此用户名不存在", Toast.LENGTH_SHORT).show();
                    return;
                }else if(result.equals("2")){
                    Toast.makeText(FindPwdActivity.this, "你输入的身份证号与用户名不对应", Toast.LENGTH_SHORT).show();
                    return;
                }else if(result.equals("1")){
                    Toast.makeText(FindPwdActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    //密码修改成功后把账号传递到LoginActivity.java中
                    // 返回值到loginActivity显示
                    Intent data = new Intent();
                    data.putExtra("userName", userName);
                    setResult(RESULT_OK, data);
                    //RESULT_OK为Activity系统常量，状态码为-1，
                    // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
                    FindPwdActivity.this.finish();
                }else{
                    Toast.makeText(FindPwdActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }
        public String getRemoteInfo(String userIdentity,String userName,String psw) throws Exception{
        String WSDL_URI = "http://120.79.17.169:8080/Log_service/jax-ws";//wsdl 的uri
        String namespace = "http://service/";//namespace
        String methodName = "find_password";//要调用的方法名称

        SoapObject request = new SoapObject(namespace, methodName);
        request.addProperty("user_identity",userIdentity);
        request.addProperty("user_id", userName);
        request.addProperty("password",psw);
        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.dotNet = false;//由于不是.net开发的webservice，所以这里要设置为false

        HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
        httpTransportSE.debug=true;
        httpTransportSE.call(namespace+methodName, envelope);//调用
        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        result = object.getProperty(0).toString();
        return result;
    }

}
