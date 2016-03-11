package com.example.administrator.huashixingkong.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.MyHttp;


public class ModifyActivity extends ActionBarActivity {

    private EditText editText;
    private Button button;
    private String name,title,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        title = intent.getStringExtra("title");
        content = intent.getCharSequenceExtra("content").toString();
        Log.d("abcde","name:"+ name +"title:"+ title +"content:"+content);

        editText = (EditText) findViewById(R.id.modify);
        editText.setText(content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button = (Button) findViewById(R.id.modify_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread modifyThread = new Thread(new ModifyThread());
                modifyThread.start();
            }
        });
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

    class ModifyThread implements Runnable{

        @Override
        public void run() {
            String str;
            str = MyHttp.ModifySave(name,title,editText.getText().toString());
            if(str!=null) {
                Message msg = handler.obtainMessage();
                if (str=="0") {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
