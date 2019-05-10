package com.taxi_safe.taxi_safe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class RegisterActivity extends AppCompatActivity {
    //下拉框设置
    private static final String[] type ={"司机","乘客"};
    private static final String[] sex ={"男","女"};
    private Spinner spinner;
    private Spinner spinner2;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;

    private TextView tv_main_title;//标题
    private TextView tv_back;//返回按钮
    private Button btn_register;//注册按钮
    //用户名，密码，再次输入的密码的控件
    private EditText et_user_identity,et_user_name_real,et_user_name,et_psw,et_psw_again;
    //身份证号，用户名，密码，再次输入的密码的控件的获取值
    private String userIdentity,userNameReal,userName,psw,pswAgain,userType,userSex;
    //网络返回结果
    private String result;

    // 创建等待框
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, type);
        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, sex);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter2);

        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        spinner2.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        spinner.setVisibility(View.VISIBLE);
        spinner2.setVisibility(View.VISIBLE);
        init();
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            userType=type[arg2];
            userSex=sex[arg2];
        }
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private void init() {
        //从main_title_bar.xml 页面布局中获取对应的UI控件
        tv_main_title=findViewById(R.id.tv_main_title);
        tv_main_title.setText("注册");
        tv_back=findViewById(R.id.tv_back);
        //从activity_register.xml 页面中获取对应的UI控件
        btn_register=findViewById(R.id.btn_register);
        et_user_identity=findViewById(R.id.et_user_identity);
        et_user_name_real=findViewById(R.id.et_user_name_real);
        et_user_name=findViewById(R.id.et_user_name);
        et_psw=findViewById(R.id.et_psw);
        et_psw_again=findViewById(R.id.et_psw_again);
        spinner=findViewById(R.id.spinner);
        spinner2=findViewById(R.id.spinner2);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回键
                RegisterActivity.this.finish();
            }
        });
        //注册按钮
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入在相应控件中的字符串
                getEditString();
                //判断输入框内容
                if(TextUtils.isEmpty(userIdentity)){
                    Toast.makeText(RegisterActivity.this, "请输入身份证号", Toast.LENGTH_SHORT).show();
                    return;
                }else if(userIdentity.length()!=18){
                    Toast.makeText(RegisterActivity.this, "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(userNameReal)){
                    Toast.makeText(RegisterActivity.this, "请输入真实姓名", Toast.LENGTH_SHORT).show();
                    return;
                }else if(userNameReal.length()>10){
                    Toast.makeText(RegisterActivity.this, "用户名最多10个汉字，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(userName)){
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }else if(userName.length()>10){
                    Toast.makeText(RegisterActivity.this, "用户名最多10个字符，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(psw)){
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(psw.length()>10){
                    Toast.makeText(RegisterActivity.this, "密码最多10个字符，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(pswAgain)){
                    Toast.makeText(RegisterActivity.this, "请再次输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!psw.equals(pswAgain)){
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不一样", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    // 提示框
                    dialog = new ProgressDialog(RegisterActivity.this);
                    dialog.setTitle("提示");
                    dialog.setMessage("正在注册，请稍后...");
                    dialog.setCancelable(false);
                    dialog.show();
                    //启动后台异步线程进行连接webService操作，并且根据返回结果在主线程中改变UI
                    RegisterActivity.QueryAddressTask queryAddressTask = new RegisterActivity.QueryAddressTask();
                    //启动后台任务
                    queryAddressTask.execute(userIdentity,userNameReal,userName,psw,userType,userSex);
                }
            }
        });
    }
    class QueryAddressTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                result = getRemoteInfo(params[0],params[1],params[2],params[3],params[4],params[5]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //将结果返回给onPostExecute方法
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if(result.equals("2")){
                Toast.makeText(RegisterActivity.this, "此账户名已经存在", Toast.LENGTH_SHORT).show();
                return;
            }else if(result.equals("1")){
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                //注册成功后把账号传递到LoginActivity.java中
                // 返回值到loginActivity显示
                Intent data = new Intent(RegisterActivity.this,LoginActivity.class);
                data.putExtra("userName", userName);
                setResult(RESULT_OK, data);
                //RESULT_OK为Activity系统常量，状态码为-1，
                // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
                RegisterActivity.this.finish();
            }else{
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }

    public String getRemoteInfo(String userIdentity,String userNameReal,String userName,String psw,String userType,String userSex) throws Exception{
        String WSDL_URI = "http://120.79.17.169:8080/Log_service/jax-ws";//wsdl 的uri
        String namespace = "http://service/";//namespace
        String methodName = "regist";//要调用的方法名称

        SoapObject request = new SoapObject(namespace, methodName);
        request.addProperty("user_identity",userIdentity);
        request.addProperty("user_name_real",userNameReal);
        request.addProperty("user_id", userName);
        request.addProperty("password",psw);
        request.addProperty("user_type",userType);
        request.addProperty("user_sex",userSex);
        //创建SoapSerializationEnvelope 对象，同时指定soap版本号(之前在wsdl中看到的)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
        envelope.dotNet = false;//由于是.net开发的webservice，所以这里要设置为true

        HttpTransportSE httpTransportSE = new HttpTransportSE(WSDL_URI);
        httpTransportSE.debug=true;
        httpTransportSE.call(namespace+methodName, envelope);//调用
        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        result = object.getProperty(0).toString();
        return result;
    }
    /**
     * 获取控件中的字符串
     */
    private void getEditString(){
        userIdentity=et_user_identity.getText().toString().trim();
        userNameReal=et_user_name_real.getText().toString().trim();
        userName=et_user_name.getText().toString().trim();
        psw=et_psw.getText().toString().trim();
        pswAgain=et_psw_again.getText().toString().trim();
    }
}
