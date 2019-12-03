package com.example.imoocservicedownload.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.imoocservicedownload.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    public void SingleThreaded(View view) {
        Intent intent = new Intent(BaseActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void MultiThreaded(View view) {
        Intent intent = new Intent(BaseActivity.this,SecondMainActivity.class);
        startActivity(intent);
    }
}
