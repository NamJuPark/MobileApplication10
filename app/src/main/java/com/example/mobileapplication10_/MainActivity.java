package com.example.mobileapplication10_;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<String> data;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkPer();
        makeDir();


    }

    private void checkPer() {//권한 확인 후 수락여부 확인
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(!(permissioninfo == PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
    }

    private void init() {
        lv = (ListView)findViewById(R.id.listview);
        data = new ArrayList<String>();
    }

    public String  getExternalPath(){//외부 메모리 쓰기 위해 경로 가져오기
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)){
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        }
        else{
            sdPath = getFilesDir()+"";
        }
        return sdPath;
    }

    private void makeDir() {//외부 메모리에 디렉토리 만들기
        String path = getExternalPath();
        File file = new File(path + "mydiary");
        file.mkdir();
    }





    public void onClick(View view){

    }
}
