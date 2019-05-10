package com.taxi_safe.taxi_safe;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.taxi_safe.taxi_safe.Pairing.DriverClient;
import com.taxi_safe.taxi_safe.Pairing.InfoPtl;
import com.taxi_safe.taxi_safe.sftp.UploadThread;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.taxi_safe.taxi_safe", appContext.getPackageName());
    }

    /**
     * 获取某个文件夹下的所有文件
     *
     * @param fileNameList 存放文件名称的list
     * @param path 文件夹的路径
     * @return
     */
    public static void getAllFileName(String path, ArrayList<String> fileNameList) {
        //ArrayList<String> files = new ArrayList<String>();
        boolean flag = false;
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                //System.out.println("文     件：" + tempList[i]);
                //fileNameList.add(tempList[i].toString());
                fileNameList.add(path + "/" + tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
                //System.out.println("文件夹：" + tempList[i]);
                getAllFileName(tempList[i].getAbsolutePath(),fileNameList);
            }
        }
        return;
    }

    //文件上传
    @Test
    public void uploadImageTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        try{
            ArrayList<String> fileNameList = new ArrayList<String>();
            getAllFileName(appContext.getExternalCacheDir().toString(), fileNameList);
            for(int i = 0; i < fileNameList.size(); i++){
                File up = new File(fileNameList.get(0));
                UploadThread uploadThread = new UploadThread(up);
                boolean re = uploadThread.upload();
                assertEquals(true, re);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //配对码获取
    @Test
    public void paircodeTest(){
        DriverClient driverClient = new DriverClient("3");
        driverClient.Pairing();
        String Code = driverClient.getPaircode();
        boolean cc = false;
        if(Code.length() == 6){
            cc = true;
        }
        assertEquals(true, cc);
    }

    //删除配对码
    @Test
    public void deletepaircodeTest(){
        InfoPtl infoPtl = new InfoPtl();
        boolean re = infoPtl.deleteRecord("495370");
        assertEquals(true, re);
    }

    //更新表情标签
    @Test
    public void updateEmotionTest(){
        InfoPtl infoPtl = new InfoPtl();
        boolean re = infoPtl.updateEmotion("720595", "3");
        assertEquals(true, re);
    }

    //查看是否配对
    @Test
    public void isPairTest(){
        InfoPtl infoPtl = new InfoPtl();
        boolean re = infoPtl.isPaired("720595");
        assertEquals(false, re);
    }
}
