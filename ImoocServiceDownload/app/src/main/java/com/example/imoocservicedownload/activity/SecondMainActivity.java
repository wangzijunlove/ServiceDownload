package com.example.imoocservicedownload.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.imoocservicedownload.R;
import com.example.imoocservicedownload.adapter.FileListAdapter;
import com.example.imoocservicedownload.entities.FileInfo;
import com.example.imoocservicedownload.services.DownloadService2;

import java.util.ArrayList;
import java.util.List;

public class SecondMainActivity extends AppCompatActivity {

    private ListView mLvFile;
    private List<FileInfo> mFileList;
    private FileListAdapter myAdapter;
    private String DownloadUrl1 = "https://dl.google.com/android/repository/android-ndk-r19c-linux-x86_64.zip?hl=zh-cn";
    private String DownloadUrl2 = "https://codeload.github.com/wangzijunlove/SomeThreadDownload/zip/master";
    private String DownloadUrl3 = "https://codeload.github.com/nickgao1986/MPChartSampleUsingSourceCode/zip/master";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);
        mLvFile = findViewById(R.id.lvFile);
        //创建文件集合
        mFileList = new ArrayList<>();

        //创建文件信息对象
        final FileInfo fileInfo0 = new FileInfo(0,DownloadUrl1,
                "kugou_v7.6.85.17344_setup.1427079848.exe",0,0);
        final FileInfo fileInfo1 = new FileInfo(1,DownloadUrl1,
                "SomeThreadDownload",0,0);
        final FileInfo fileInfo2 = new FileInfo(2,DownloadUrl1,
                "MPChartSampleUsingSourceCode",0,0);
        mFileList.add(fileInfo0);
        mFileList.add(fileInfo1);
        mFileList.add(fileInfo2);

        myAdapter = new FileListAdapter(this,mFileList);

        mLvFile.setAdapter(myAdapter);

//注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService2.ACTION_UPDATA);
        filter.addAction(DownloadService2.ACTION_FINISH);
        registerReceiver(mReceiver,filter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadService2.ACTION_UPDATA.equals(intent.getAction())){
                int finished = intent.getIntExtra("finished",0);
                int id = intent.getIntExtra("id",-1);
                myAdapter.updateProgress(id,finished);
            }else if(DownloadService2.ACTION_FINISH.equals(intent.getAction())){
                //更新进度条0
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                myAdapter.updateProgress(fileInfo.getId(),0);
                Toast.makeText(SecondMainActivity.this,mFileList.get(fileInfo.getId()).getFileName()+"下载完成",Toast.LENGTH_LONG).show();
            }
        }
    };




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
