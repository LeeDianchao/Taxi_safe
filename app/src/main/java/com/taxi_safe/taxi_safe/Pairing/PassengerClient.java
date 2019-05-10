package com.taxi_safe.taxi_safe.Pairing;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PassengerClient extends Client{
    private String paircode;
    private Socket socket;
    public PassengerClient(String username){
        super(username);
    }
    //乘客输入配对码
    public boolean Pairing(String code){
        try {
            socket=new Socket(this.getServerhost(),this.getPairport());
        }catch (IOException e){
            System.out.print("服务器连接失败！");
            e.printStackTrace();
        }
        InputStreamReader isr;
        BufferedReader br;
        OutputStreamWriter osw;
        BufferedWriter bw;
        try {
            osw=new OutputStreamWriter(socket.getOutputStream());
            bw=new BufferedWriter(osw);
            JSONObject json=new JSONObject();
            json.put("username",this.getUsername());
            json.put("type","passenger");
            json.put("pairingcode",code);
            bw.write(json.toString()+"\n");
            bw.flush();
            isr=new InputStreamReader(socket.getInputStream());
            br=new BufferedReader(isr);
            String str=br.readLine();
            JSONObject back=JSONObject.parseObject(str);
            if(back.getString("result").equals("t")) {
                this.paircode=code;
                return true;
            }else if(back.getString("result").equals("f")){
                System.out.print("配对失败！");
                return false;
            }else if(back.getString("result").equals("e")){
                System.out.print("配对码输入错误！");
                return false;
            }
        }catch (IOException e){
            System.out.print("配对失败！");
            e.printStackTrace();
        }
        return false;
    }
}

