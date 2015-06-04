package com.example.cnwesleywang.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by cnwesleywang on 15/6/3.
 */
public class PhotoFtpClient {
    Context context;

    public PhotoFtpClient(Context c) {
        this.context=c;
    }

    int Total=0,Succ=0;


    public String go() {
        FTPClient ftpClient = new FTPClient();
        String ip="lily.newim.net";

        Total = 0;
        ArrayList<String> files = new ArrayList<String>();
        for (int i=0;i<2;i++){
            ContentResolver resolver = context.getContentResolver();
            Cursor c;
            if (i==0) {
                String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA};
                c = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            }
            else {
                String[] projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA};
                c = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            }
            if (c!=null) {
                Total+=c.getCount();
                if (c.moveToFirst()) {
                    do {
                        String path;
                        if (i==0) {
                            path=c.getString(c.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                        }
                        else{
                            path=c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA));
                        }
                        String[] remotes = path.split("/");
                        String remotename = "ftproot/"+remotes[remotes.length - 1];
                        Log.d("MyGlassWare", "Picture path: " + path + " " + remotename);

                        try {
                            ftpClient.connect(InetAddress.getByName(ip), 21);
                            if (!ftpClient.login("glassftp", "wq1977@2")) {
                                ftpClient.logout();
                                return "Login to server fail!";
                            }
                            int reply = ftpClient.getReplyCode();
                            if (!FTPReply.isPositiveCompletion(reply)) {
                                ftpClient.disconnect();
                                return "unknown ftp fail!";
                            }
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                            BufferedInputStream buffIn = null;
                            buffIn = new BufferedInputStream(new FileInputStream(path));
                            ftpClient.storeFile(remotename, buffIn);
                            buffIn.close();
                            ftpClient.logout();
                            ftpClient.disconnect();
                            Succ++;
                            //this.publishProgress(String.format("成功传输了%d/%d张照片", Succ, Total));
                            files.add(path);
                        } catch (IOException e) {
                            Log.d("", "exception:", e);
                            return "Unknown exception!!!";
                        }
                    }
                    while (c.moveToNext());
                    c.close();
                }
            }

            for (String path:files){
                File file = new File(path);
                if (file.delete()){
                    Log.d("","delete "+path+" succ");
                }
                else{
                    Log.d("","delete "+path+" fail!");
                }
                MediaScannerConnection.scanFile(
                        context,
                        new String[]{path},
                        null, null);
            }
        }
        return "";
    }

}
