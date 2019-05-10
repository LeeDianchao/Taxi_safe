package com.taxi_safe.taxi_safe.sftp;

import android.util.Log;
import java.io.File;
import java.util.HashMap;

public class GetExpressionTag {
    private String filename;
    private String response;
    private String expressiontag;

    public static HashMap<String, String> ExpressionTag = new HashMap<>();

    public GetExpressionTag(String filename) {
        if(filename == ""){
            this.filename = null;
            this.response = null;
            this.expressiontag = null;
        }else {
            this.filename = filename;
            //表情标签
            ExpressionTag.put("0", "Nature");ExpressionTag.put("1", "Happy");
            ExpressionTag.put("2", "Sad");ExpressionTag.put("3", "Surprise");
            ExpressionTag.put("4", "Fear");ExpressionTag.put("5", "Disgust");
            ExpressionTag.put("6", "Anger");ExpressionTag.put("7", "Contempt");
        }
    }

    public void setFilename(String filename){
        this.filename = filename;
    }

    public void setResponse(){
        if(this.filename != ""){
            SOCKET socket=new SOCKET("112.74.178.160",12345);
            Object res=socket.send(this.filename);
            this.response = res.toString();
            this.expressiontag = ExpressionTag.get(this.response);
        }
    }

    public String getResponse(){
        return this.response;
    }

    public String getExpressionTag(){
        return this.expressiontag;
    }

    public void run(){
        setResponse();
    }
}
