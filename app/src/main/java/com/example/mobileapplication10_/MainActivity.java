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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String selectedItem;
    TextView tv;
    ListView lv;
    DatePicker dp;
    ArrayList<String> data;
    ArrayAdapter<String> adapter;
    LinearLayout linear1,linear2;
    Button btSave;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

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
                                adapter.notifyDataSetChanged();
                                listFile();
                                Toast.makeText(getApplicationContext(),"삭제되었습니다",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = data.get(i).substring(0,9);
                readFile(selectedItem);
                btSave.setText("수정");
                linear1.setVisibility(View.INVISIBLE);
                linear2.setVisibility(View.VISIBLE);
            }
        });

    }

    private void init() {
        tv = (TextView)findViewById(R.id.tvCount);
        lv = (ListView)findViewById(R.id.listview);
        data = new ArrayList<String>();
        linear1 = (LinearLayout)findViewById(R.id.linear1);
        linear2 = (LinearLayout)findViewById(R.id.linear2);
        dp = (DatePicker)findViewById(R.id.dp);
        et = (EditText)findViewById(R.id.et);
        btSave = (Button)findViewById(R.id.btnsave);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        lv.setAdapter(adapter);
        checkPer();
        makeDir();
        listFile();
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
        file.mkdir();
        Toast.makeText(this,"디렉토리 생성",Toast.LENGTH_SHORT).show();
    }

    public void writeFile(String filename){
        try {
            String path = getExternalPath();
            BufferedWriter bw = new BufferedWriter(new FileWriter(path + "mydiary/" + filename+ ".txt", true));
            bw.write(et.getText().toString());
            bw.close();
            fileList();
            Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage() + ":" + getFilesDir(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void readFile(String filename) {
        try {
            String path =getExternalPath();
            BufferedReader br = new BufferedReader(new FileReader(path+ "mydiary/" + filename + ".txt"));
            String readStr = "";
            String str = null;
            while ((str = br.readLine()) != null) readStr += str + "\n";
            br.close();
            et.setText(readStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view){

        if(view.getId() == R.id.btn1){//일기 등록 시(화면 변경)
            linear1.setVisibility(View.INVISIBLE);
            linear2.setVisibility(View.VISIBLE);
            btSave.setText("저장");
            et.setText(null);
        }
        else if(view.getId() == R.id.btncancel){//취소 시(화면 변경)
            linear2.setVisibility(View.INVISIBLE);
            linear1.setVisibility(View.VISIBLE);
        }
        else if(view.getId() == R.id.btnsave){//저장 OR 수정
            if(btSave.getText().equals("저장")){//저장
                if(checkList(getNameFromDP())){ //원래 리스트에 해당 날짜 존재하는지 아닌지
                    linear1.setVisibility(View.GONE);
                    linear2.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"이미 존재합니다",Toast.LENGTH_SHORT).show();
                    readFile(getNameFromDP());
                    initDP();
                    btSave.setText("수정");
                    return;
                }
                else{
                    writeFile(getNameFromDP());
                }
            }
            else if(btSave.getText().equals("수정")){
                File file = new File(getExternalPath() + "mydiary/" + selectedItem + ".txt");
                file.delete();
                writeFile(getNameFromDP());
                initDP();
                btSave.setText("저장");
            }
            listFile();
            linear2.setVisibility(View.INVISIBLE);
            linear1.setVisibility(View.VISIBLE);
            et.setText(null);
        }
    }

    private boolean checkList(String nameFromDP) {
        boolean check = false;
        for(int i = 0; i < data.size(); i++){
            if(data.get(i).equals(nameFromDP)) check = true;
        }
        return check;
    }

    private void listFile(){
        String path = getExternalPath();
        data.clear();
        File[] files = new File(path + "mydiary").listFiles();
        String str = "";
        if(files != null){
            for(File f:files){
                str = f.getName().substring(0,9);
                data.add(str);
            }
        }
        Collections.sort(data,comparator);
        adapter.notifyDataSetChanged();
        tv.setText("등록된 메모 개수: " + data.size());
    }

    Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
            return s.compareTo(t1);
        }
    };
    public void sorting(){
        Collections.sort(data,comparator);
    }

    private void initDP(){
        Calendar cal  = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        dp.init(year,month,day,null);
    }


}
