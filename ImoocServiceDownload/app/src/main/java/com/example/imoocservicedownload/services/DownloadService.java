package com.example.imoocservicedownload.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.imoocservicedownload.entities.FileInfo;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by archermind on 11/29/19.
 * Wzj
 * content
 */
public class DownloadService extends Service {

    private final String TAG = "DownloadService";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATA = "ACTION_UPDATA";
    public static final String ACTION_FINISH = "ACTION_FINISH";
    public static final int MSG_INIT = 0;
    private DownloadTask mTask = null;

    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/aDownloads/";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //从intent接受数据 获得Avtivity传来的参数
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.e(TAG, "onStartCommand:start " + fileInfo.toString());
            //启动初始化线程
            new InitThread(fileInfo).start();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.e(TAG, "onStartCommand:stop " + fileInfo.toString());
            if(mTask != null){
                mTask.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) message.obj;
                    Log.e(TAG, "handleMessage: " + fileInfo);
                    //启动下载任务
                    mTask = new DownloadTask(DownloadService.this,fileInfo);
                    mTask.download();
                    break;
            }
            return false;
        }
    });

    /**
     * 初始化子线程
     */
    class InitThread extends Thread {
        private FileInfo mFileInfo = null;
        HttpURLConnection coon = null;
        RandomAccessFile raf = null;
        int length = -1;

        public InitThread(FileInfo mFileInfo) {
            this.mFileInfo = mFileInfo;
        }

        public void run() {
            try {
                //链接网络文件
                URL url = new URL(mFileInfo.getUrl());
                coon = (HttpURLConnection) url.openConnection();
                coon.setConnectTimeout(3000);
                coon.setRequestMethod("GET");
                if (coon.getResponseCode() == 200) {
                    //获取文件长度
                    length = coon.getContentLength();
                }
                if (length <= 0) {
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                //在本地创建文件
                File file = new File(dir, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                Log.e(TAG, "run: " + raf);
                //设置文件长度
                raf.setLength(length);
                mFileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    coon.disconnect();
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
