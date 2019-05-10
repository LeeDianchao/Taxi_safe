package com.taxi_safe.taxi_safe.sftp;

import android.util.Log;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadThread {
    private File file;
    private final static String TAG = "UploadThread ===> ";

    public volatile boolean exit = false;
    public UploadThread(File f) {
        this.file = f;
        this.exit = false;
    }

    public void set(boolean exit){
        this.exit = exit;
    }

    public void run(){
        Log.d(TAG, "run");
        boolean result = false;
        try {
            result = upload();
        }catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());

        }catch (SftpException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        if(result) {
            Log.d(TAG, "upload successful");
        }
        else {
            Log.d(TAG, "upload false");
        }
    }

    public boolean upload() throws SftpException, IOException {

        try {
            SFTPUtil sftp = new SFTPUtil("root", "zyx19980728!", "112.74.178.160", 22);
            sftp.login();
            //byte[] buff = sftp.download("/opt", "start.sh");
            //System.out.println(Arrays.toString(buff));
            File file = new File(this.file.getAbsolutePath());
            InputStream is = new FileInputStream(file);

            sftp.upload("//root", "//usr_img", this.file.getName(), is);
            sftp.logout();
        }catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
            return false;
        }catch (SftpException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
            return false;
        }
        Log.d(TAG, "upload successful");
        return true;
    }

    public boolean download() throws SftpException, IOException {
        try {
            SFTPUtil sftp = new SFTPUtil("root", "zyx19980728!", "112.74.178.160", 22);
            sftp.login();
            sftp.download("//root//usr_img", "sftptest.java", "D://sftp.java");

        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
