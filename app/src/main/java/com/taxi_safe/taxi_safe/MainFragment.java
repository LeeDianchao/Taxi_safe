package com.taxi_safe.taxi_safe;

import com.taxi_safe.taxi_safe.Pairing.DriverClient;
import com.taxi_safe.taxi_safe.Pairing.InfoPtl;
import com.taxi_safe.taxi_safe.Pairing.PassengerClient;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainFragment extends Fragment {

    private String userName;
    private String userType;
    private ImageView driver;
    private ImageView passenger;

    // 创建等待框
    private ProgressDialog dialogPaircode;
    private ProgressDialog dialogPair;
    //匹配码
    private String Code;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLoginParams(AnalysisUtils.readLoginStatus(getActivity()));
        driver=view.findViewById(R.id.driver);
        passenger=view.findViewById(R.id.passenger);


        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AnalysisUtils.readLoginStatus(getActivity())&&userType.equals("2")) {

                    // 提示框
                    dialogPaircode = new ProgressDialog(getActivity());
                    dialogPaircode.setTitle("提示");
                    dialogPaircode.setMessage("正在生成匹配码，请稍后...");
                    dialogPaircode.setCancelable(false);
                    dialogPaircode.show();
                    //启动后台异步线程进行连接webService操作，并且根据返回结果在主线程中改变UI
                    MainFragment.PairCodeQueryAddressTask pairCodeQueryAddressTask = new MainFragment.PairCodeQueryAddressTask();
                    //启动后台任务
                    pairCodeQueryAddressTask.execute(userName);

                    //Toast.makeText(getActivity(), "司机界面", Toast.LENGTH_SHORT).show();
                }else if (AnalysisUtils.readLoginStatus(getActivity())&&userType.equals("1")) {
                    Toast.makeText(getActivity(), "该界面尽司机可用", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AnalysisUtils.readLoginStatus(getActivity())) {
                    final EditText editText = new EditText(getActivity());
                    AlertDialog.Builder inputDialog =
                            new AlertDialog.Builder(getActivity());
                    inputDialog.setTitle("请输入匹配码").setView(editText);
                    inputDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getActivity(),editText.getText().toString(),Toast.LENGTH_SHORT).show();
                                    PassengerClient passengerClient = new PassengerClient(userName);
                                    Code = editText.getText().toString();
                                    if(Code.length()==0){
                                        return;
                                    }
                                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                                    boolean result = passengerClient.Pairing(editText.getText().toString());

                                    if(result){
                                        Toast.makeText(getActivity(),"配对成功",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(getActivity(), Monitor.class);
                                        intent.putExtra("paircode", Code);
                                        intent.putExtra("username", userName);
                                        intent.putExtra("usertype", userType);
                                        startActivityForResult(intent, 111);
                                    }else {
                                        Toast.makeText(getActivity(),"配对失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).show();
                    //Toast.makeText(getActivity(), "乘客界面", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "您还未登录，请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setLoginParams(boolean isLogin) {
        if (isLogin) {
            userName=AnalysisUtils.readLoginUserName(getActivity());
            userType=AnalysisUtils.readLoginUserType(getActivity());

        }
    }

    class PairCodeQueryAddressTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Code = getRemoteInfo(params[0]);
                //Code="11111";
            } catch (Exception e) {
                e.printStackTrace();
            }
            //将结果返回给onPostExecute方法
            return Code;
        }
        @Override
        protected void onPostExecute(String result) {
            dialogPaircode.dismiss();
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("匹配码")
                    .setMessage(Code)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Toast.makeText(getActivity(), "这是确定按钮", Toast.LENGTH_SHORT).show();
                            // 提示框
                            dialogPair = new ProgressDialog(getActivity());
                            dialogPair.setTitle("提示");
                            dialogPair.setMessage("等待乘客配对，请稍后...");
                            dialogPair.setCancelable(false);
                            dialogPair.show();
                            //启动后台异步线程进行连接webService操作，并且根据返回结果在主线程中改变UI
                            MainFragment.PairQueryAddressTask queryAddressTask = new MainFragment.PairQueryAddressTask();
                            //启动后台任务
                            queryAddressTask.execute(Code);

                        }
                    })
                    .create();
            alertDialog.show();

        }
    }

    //生成匹配码
    public String getRemoteInfo(String userName) throws Exception{

        DriverClient driverClient = new DriverClient(userName);
        driverClient.Pairing();
        Code = driverClient.getPaircode();

        //Code = "1245";
        return Code;
    }

    //等待乘客配对
    class PairQueryAddressTask extends AsyncTask<String, Integer, String> {
        String re = null;
        @Override
        protected String doInBackground(String... params) {
            try {
                re = getIsPair(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //将结果返回给onPostExecute方法
            if(re.equals("0")){
                re = "true";
            }else {
                re = "false";
            }
            return re;
        }
        @Override
        protected void onPostExecute(String result) {
            dialogPair.dismiss();
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage(re)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Toast.makeText(getActivity(), "这是确定按钮", Toast.LENGTH_SHORT).show();
                            if(re.equals("true")){
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), OpenCvActivity.class);
                                intent.putExtra("CAMERA_INDEX", "98");//98:前置   99:后置
                                intent.putExtra("paircode", Code);
                                intent.putExtra("username", userName);
                                intent.putExtra("usertype", userType);
                                startActivityForResult(intent, 222);
                            }
                        }
                    })
                    .create();
            alertDialog.show();

        }
    }

    //查看是否配对
    public String getIsPair(String paircode) throws Exception{

        boolean re = false;
        int i = 0;
        InfoPtl infoPtl = new InfoPtl();
        while (!re){
            re = infoPtl.isPaired(paircode);
            i++;
            if(i>20){
                infoPtl.deleteRecord(paircode);
                return "-1";
            }
            if(re == true) {
                return "0";
            }
            else{
                Thread.sleep(1000);
            }
        }
        return "-1";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
