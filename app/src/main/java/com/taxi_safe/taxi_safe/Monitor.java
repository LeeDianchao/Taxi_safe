package com.taxi_safe.taxi_safe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.taxi_safe.taxi_safe.Pairing.InfoPtl;
import com.taxi_safe.taxi_safe.sftp.GetExpressionTag;

public class Monitor extends AppCompatActivity {

    private static String userName;
    private static String userType;
    private static String paircode;

    private ImageView circleImageView;
    private Button goback;
    private TextView emotiontext;
    private static InfoPtl infoPtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        infoPtl = new InfoPtl();

        circleImageView = findViewById(R.id.circleImageView);
        emotiontext = findViewById(R.id.emotiontext);

        Intent getIntent = getIntent();
        userName = getIntent.getStringExtra("username");
        userType = getIntent.getStringExtra("usertype");
        paircode = getIntent.getStringExtra("paircode");

        goback = (Button) findViewById(R.id.goback_close);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoPtl infoPtl = new InfoPtl();
                infoPtl.deleteRecord(paircode); //删除当前配对记录

                Intent intent = new Intent();
                setResult(111, intent);
                finish();//结束当前activity
            }
        });

        circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p0));
        emotiontext.setText("nature");
        Thread getTag = new Thread(){

            @Override
            public void run(){
                while (true){
                    try{
                        Thread.sleep(1000);
                        String s = infoPtl.getEmotion(paircode);
                        if(s.equals("0")){
                            emotiontext.setText("nature");
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p0));
                        }
                        else if(s.equals("1")){
                            emotiontext.setText("happy");
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p1));
                        }
                        else if(s.equals("2")){
                            emotiontext.setText("sad");
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p2));
                        }
                        else if(s.equals("3")){
                            emotiontext.setText("surprise");
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p3));
                        }
                        else if(s.equals("4")){
                            emotiontext.setText("fear");
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p4));
                        }
                        else if(s.equals("5")){
                            emotiontext.setText("disgust");
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p5));
                        }
                        else if(s.equals("6")){
                            emotiontext.setText("anger");
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p6));
                        }
                        else if(s.equals("7")){
                            emotiontext.setText("contempt");
                            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.p7));
                        }
                        Thread.sleep(1000);
                    }catch (Exception e) {
                        Log.w("Get Expression Tag error", e.toString());
                    }
                }
            }
        };
        getTag.start();
    }
}
