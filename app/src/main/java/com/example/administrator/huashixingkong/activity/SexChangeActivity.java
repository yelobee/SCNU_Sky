package com.example.administrator.huashixingkong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.MyHttp;

public class SexChangeActivity extends ActionBarActivity {

    private static final String []sexText = {"男","女"};
    private Spinner sex;
    private Button ascConfirm;
    private ArrayAdapter<String> spinnerAdapter;
    private String name,title,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex_change);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        title = intent.getStringExtra("title");
        content = intent.getCharSequenceExtra("content").toString();
        Log.d("abcde", "name:" + name + "title:" + title + "content:" + content);

        sex = (Spinner) findViewById(R.id.asc_sex);
        ascConfirm = (Button) findViewById(R.id.asc_confirm);

        ascConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread sexChangeThread = new Thread(new SexChangeThread());
                sexChangeThread.start();
            }
        });

        spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,sexText);
        sex.setAdapter(spinnerAdapter);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "成功！", Toast.LENGTH_SHORT).show();
                    //data = (ArrayList<HashMap<String, Object>>) msg.obj;
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    class SexChangeThread implements Runnable{

        @Override
        public void run() {
            String str;
            Log.d("abcd",sex.getSelectedItem().toString());
            str = MyHttp.ModifySave(name, title,sex.getSelectedItem().toString());
            if(str!=null) {
                Message msg = handler.obtainMessage();
                if (str.equals("success")) {
                    Log.d("abc", "ok");
                    msg.what = 0;
                    handler.sendMessage(msg);
                } else {
                    Log.d("abc", "error");
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
