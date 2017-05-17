package com.example.mobileapplication10_;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    ListView lv;
    DatePicker dp;
    ArrayList<String> data;
    ArrayList<diary> data2;
    ArrayAdapter<String> adapter;
    LinearLayout linear1,linear2;
    EditText et;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkPer();
        makeDir();

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("삭제")
                        .setMessage("삭제하시겠습니까?")
                        .setNegativeButton("취소",null)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                File file = new File(getExternalPath() + "mydiary/" + data.get(i) + ".txt");
                                file.delete();
                                data.remove(i);
                                data2.remove(i);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(),"삭제되었습니다",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
            }
        });


    }

    private void init() {
        tv = (TextView)findViewById(R.id.tvCount);
        lv = (ListView)findViewById(R.id.listview);
        data = new ArrayList<String>();
        data2 = new ArrayList<diary>();
        linear1 = (LinearLayout)findViewById(R.id.linear1);
        linear2 = (LinearLayout)findViewById(R.id.linear2);
        dp = (DatePicker)findViewById(R.id.dp);
        et = (EditText)findViewById(R.id.et);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        lv.setAdapter(adapter);
    }


    private String getNameFromDP() {
        String fileName = "";
        fileName = String.format("%d-%d-%d",dp.getYear(),dp.getMonth()+1,dp.getDayOfMonth() );
        return fileName;
    }

    private void checkPer() {//권한 확인 후 수락여부 확인
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(!(permissioninfo == PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
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
        Toast.makeText(this,"디렉토리 생성",Toast.LENGTH_SHORT).show();
        file.mkdir();
    }

    public void writeFile(String filename){
        try {
            String path =getExternalPath();
            BufferedWriter bw = new BufferedWriter(new FileWriter(path + "mydiary/" + filename+ ".txt", true));
            bw.write(et.getText().toString());
            bw.close();
            data.add(filename);
            data2.add(new diary(path + filename+".txt", filename));
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
            count++;
            tv.setText("등록된 메모 개수: "+count);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage() + ":" + getFilesDir(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void readFile() {
        try {
            String path =getExternalPath();
            BufferedReader br = new BufferedReader(new
                    FileReader(path + "aaa.txt"));
            String readStr = "";
            String str = null;
            while ((str = br.readLine()) != null) readStr += str + "\n";
            br.close();
            Toast.makeText(this, readStr.substring(0, readStr.length() - 1),
                    Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view){

        if(view.getId() == R.id.btn1){
            linear1.setVisibility(View.INVISIBLE);
            linear2.setVisibility(View.VISIBLE);
        }
        else if(view.getId() == R.id.btncancel){

        }
        else if(view.getId() == R.id.btnsave){
            Calendar cal  = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            writeFile(getNameFromDP());
            dp.init(year,month,day,null);
            linear2.setVisibility(View.INVISIBLE);
            linear1.setVisibility(View.VISIBLE);
        }
    }
}
