package com.taxi_safe.taxi_safe.Pairing;

import com.alibaba.fastjson.JSONObject;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class DriverClient extends Client {
    private String paircode;
    private Socket socket;

    public DriverClient(String username) {
        super(username);
    }

    public boolean Pairing() {
        try {
            socket = new Socket(this.getServerhost(), this.getPairport());
        } catch (IOException e) {
            System.out.print("服务器连接失败!");
            e.printStackTrace();
        }
        InputStreamReader isr;
        BufferedReader br;
        OutputStreamWriter osw;
        BufferedWriter bw;
        try {
            osw = new OutputStreamWriter(socket.getOutputStream());
            bw = new BufferedWriter(osw);
            JSONObject json = new JSONObject();
            json.put("username", this.getUsername());
            json.put("type", "driver");
            bw.write(json.toString() + "\n");
            bw.flush();
            isr = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(isr);
            String str = br.readLine();
            JSONObject back = JSONObject.parseObject(str);
            if (back.getString("result").equals("t")) {
                this.paircode = back.getString("pairingcode");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPaired(String paircode) {
        String result = null;
        try{
            String WSDL_URI = "http://120.79.17.169:8080/Log_service/jax-ws";//wsdl 的uri
            String namespace = "http://service/";//namespace
            String methodName = "isPair";//要调用的方法名称

            SoapObject request = new SoapObject(namespace, methodName);
            request.addProperty("code", paircode);
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
        }catch (Exception e){
            e.printStackTrace();
        }
        if(result.equals("0")){
            return true;
        }else {
            return false;
        }
    }

    public String getPaircode() {
        return this.paircode;
    }

}
