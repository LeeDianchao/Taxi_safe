package com.taxi_safe.taxi_safe.Pairing;

import java.io.*;
import java.net.Socket;
import com.alibaba.fastjson.JSONObject;

public class Client {
    private String username;
    private static String serverhost="120.79.17.169";
    //private static String serverhost="127.0.0.1";
    private static int sqlport=3306;
    private static int pairport=12346;
    private static int monitorport=12347;
    public Client(String username){
        this.username=username;
    }
    public String getUsername(){
        return this.username;
    }
    public String getServerhost(){
        return this.serverhost;
    }
    public int getSQLPort(){
        return this.sqlport;
    }
    public int getPairport(){
        return this.pairport;
    }
    public int getMonitorport(){
        return this.monitorport;
    }
}
