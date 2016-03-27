package com.example.administrator.huashixingkong.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.HttpHelp;

import java.util.HashMap;

public class ReplyActivity extends ActionBarActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
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

    private void initView(){
        editText = (EditText) findViewById(R.id.reply_edit_text);
        Button button = (Button) findViewById(R.id.reply_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new ReplyThread());
                thread.start();
            }
        });

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "成功！", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
            editText.getText().clear();
        }
    };

    private HashMap<String, String> setMComment(){
        Intent intent = getIntent();
        SharedPreferences preferences = getSharedPreferences("userData", 0);
        HashMap<String,String> map = new HashMap<>();
        map.put("username", preferences.getString("username", ""));
        map.put("mood_id", intent.getStringExtra("mood_id"));
        map.put("content", editText.getText().toString().trim());
        map.put("is_reply", String.valueOf(true));
        map.put("reply_tag", intent.getStringExtra("reply_tag"));
        map.put("like_count", String.valueOf(0));
        map.put("tag", intent.getStringExtra("tag"));
        return map;
    }

    class ReplyThread implements Runnable{

        @Override
        public void run() {
            String result = null;
            try {
                result = HttpHelp.SaveMComment(setMComment());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message msg = handler.obtainMessage();
            if(result!=null){
                msg.what = 0;
                msg.obj = result;
                handler.sendMessage(msg);
            }else{
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    }

}
