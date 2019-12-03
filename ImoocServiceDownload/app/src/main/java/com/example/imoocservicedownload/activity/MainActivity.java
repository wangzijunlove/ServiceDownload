package com.example.imoocservicedownload.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imoocservicedownload.R;
import com.example.imoocservicedownload.entities.FileInfo;
import com.example.imoocservicedownload.services.DownloadService;

public class MainActivity extends AppCompatActivity {

    private TextView mTvFileName;
    private ProgressBar mPbProgress;
    private Button mBtStop,mBtStart;
    private boolean isDownlading = false;
    private String DownloadUrl = "https://dl.google.com/android/repository/android-ndk-r19c-linux-x86_64.zip?hl=zh-cn";
    private String url = "http://www.imooc.com/mobile/imooc.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化组件
        mTvFileName = findViewById(R.id.tvFileName);
        mPbProgress = findViewById(R.id.pbProgress);
        mPbProgress.setMax(100);
        mBtStart = findViewById(R.id.btStart);
        mBtStop = findViewById(R.id.btStop);
        //权限申请
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        //创建文件信息对象
        final FileInfo fileInfo = new FileInfo(0,DownloadUrl,
                "kugou_v7.6.85.17344_setup.1427079848.exe",0,0);

        mBtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //通过intent传递参数给Service
                if(!isDownlading) {
                    Intent intent = new Intent(MainActivity.this, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra("fileInfo", fileInfo);
                    startService(intent);
                    isDownlading = true;
                }
            }
        });

        mBtStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_STOP);
                intent.putExtra("fileInfo",fileInfo);
                startService(intent);
                isDownlading = false;
            }
        });

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATA);
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
            if(DownloadService.ACTION_UPDATA.equals(intent.getAction())){
                int finished = intent.getIntExtra("finished",0);
                Log.e("进度", "onReceive: "+finished );
                mPbProgress.setProgress(finished);
                mTvFileName.setText(new StringBuffer().append(finished).append("%"));
                if(finished == 100){
                    isDownlading = false;
                }
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
