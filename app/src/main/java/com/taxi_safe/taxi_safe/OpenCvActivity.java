package com.taxi_safe.taxi_safe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.taxi_safe.taxi_safe.Pairing.InfoPtl;
import com.taxi_safe.taxi_safe.sftp.UploadThread;
import com.taxi_safe.taxi_safe.sftp.GetExpressionTag;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class OpenCvActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    JavaCameraView openCvCameraView;
    private CascadeClassifier cascadeClassifier;
    private Mat grayscaleImage;
    private int absoluteFaceSize;

    public int CAMERA_ANY   = -1;//默认摄像头（后置）
    public int CAMERA_BACK  = 99;//后置摄像头
    public int CAMERA_FRONT = 98;//前置摄像头
    public static int CAMERA_INDEX;

    private Button goback;
    private TextView textView;

    private static File outputImage = null;//当前文件
    private static File uploadImage = null;//已上传文件

    public UploadThread uploadThread;
    public GetExpressionTag getResponse = new GetExpressionTag("");
    private static boolean threadexit = false;
    private static String ExpressionTag = null;

    private static String userName;
    private static String userType;
    private static String paircode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv);
        Intent getIntent = getIntent();
        String index = getIntent.getStringExtra("CAMERA_INDEX");
        userName = getIntent.getStringExtra("username");
        userType = getIntent.getStringExtra("usertype");
        paircode = getIntent.getStringExtra("paircode");

        openCvCameraView = (JavaCameraView) findViewById(R.id.jcv);
        textView = (TextView)findViewById(R.id.textView);

        CAMERA_INDEX = Integer.valueOf(index).intValue();
        int hasCAMERAPermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA);
        if (hasCAMERAPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            Log.i("MainActivity", "Request permission.CAMERA");
        }else {
            if(CAMERA_INDEX == CAMERA_FRONT){
                openCvCameraView.setCameraIndex(CAMERA_FRONT);
            }
            else {
                openCvCameraView.setCameraIndex(CAMERA_BACK);
            }
            openCvCameraView.setCvCameraViewListener(this);
        }

        goback = (Button) findViewById(R.id.goback);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(OpenCvActivity.this,"切换摄像头",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                if(CAMERA_INDEX == CAMERA_FRONT){
                    //setResult(CAMERA_BACK, intent);
                    setResult(222, intent);
                }
                if(CAMERA_INDEX == CAMERA_BACK){
                    //setResult(CAMERA_FRONT, intent);
                    setResult(222, intent);
                }
                finish();//结束当前activity

            }
        });
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        //上传图片
        Thread upload = new Thread(){
            @Override
            public void run(){
                int i = 0;
                while (!threadexit){
                    try{
                        if(outputImage != null){
                            if(outputImage.length()!=0){
                                uploadImage = outputImage;
                                uploadThread = new UploadThread(outputImage);
                                uploadThread.upload();
                                Thread.sleep(5000);
                            }else {
                                outputImage.delete();
                            }
                        }
                        //textView.setText(String.valueOf(i));
                        //i++;
                    }catch (Exception e) {
                        Log.w("Upload error", e.toString());
                    }
                }
            }
        };
        upload.start();

        //上传图片名称
        Thread getTag = new Thread(){
            @Override
            public void run(){
                int i = 0;
                while (!threadexit){
                    try{
                        if(uploadImage != null){
                            getResponse = new GetExpressionTag(uploadImage.getName());
                            getResponse.setResponse();
                            ExpressionTag = getResponse.getExpressionTag();

                        }
                        else textView.setText(String.valueOf(i));
                        i++;
                        Thread.sleep(3000);
                    }catch (Exception e) {
                        Log.w("Get Expression Tag error", e.toString());
                    }
                }
            }
        };
        getTag.start();

        //标签
        Thread tagthread = new Thread(){
            @Override
            public void run(){
                int i = 0;
                while (!threadexit){
                    try{
                        if(ExpressionTag != null) {
                            textView.setText(ExpressionTag);
                            InfoPtl infoPtl = new InfoPtl();
                            infoPtl.updateEmotion(paircode, getResponse.getResponse());
                        }
//                        else {
//                            textView.setText(String.valueOf(i));
//                        }
//                        i++;
                        Thread.sleep(1000);
                    }catch (Exception e) {
                        Log.w("Show tag error", e.toString());
                    }
                }
            }
        };
        tagthread.start();

    }

    public void openCameraFront(){
        openCvCameraView = (JavaCameraView) findViewById(R.id.jcv);
        openCvCameraView.setCameraIndex(CAMERA_FRONT);
        openCvCameraView.setCvCameraViewListener(this);
    }

    public void openCameraBack(){
        openCvCameraView = (JavaCameraView) findViewById(R.id.jcv);
        openCvCameraView.setCameraIndex(CAMERA_BACK);
        openCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e("log_wons", "OpenCV init error");
        }
        initializeOpenCVDependencies();
    }

    private void initializeOpenCVDependencies() {
        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            // Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

        // And we are ready to go
        openCvCameraView.enableView();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);

        // The faces will be a 20% of the height of the screen
        absoluteFaceSize = (int) (height * 0.2);
    }

    @Override
    public void onCameraViewStopped() {

    }

    int faceSerialCount = 0;

    @Override
    public Mat onCameraFrame(Mat aInputFrame) {
        Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);

        Time time  =  new Time();
        time.setToNow();
        String str_time = time.format("%Y%m%d_%H%M%S");
        String pic_name = userName + "_" + str_time + ".jpg";
        //应用缓存目录
        outputImage = new File(getExternalCacheDir(), pic_name);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
        String path = outputImage.getAbsolutePath();
        openCvCameraView.takePhoto(path, 224, 224);//图像大小为224X224

        return aInputFrame;
    }
}
