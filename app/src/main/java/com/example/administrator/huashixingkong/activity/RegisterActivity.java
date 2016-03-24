package com.example.administrator.huashixingkong.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.RegisterHttp;


public class RegisterActivity extends ActionBarActivity implements View.OnFocusChangeListener{

    private static final String []sexText = {"男","女"};
    private EditText userName;
    private EditText passWord;
    private EditText checkPass;
    private Spinner sex;
    private Button registerConfirm;
    private ArrayAdapter<String> spinnerAdapter;
    private boolean flag = false; //判断数据是否符合要求标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.user_name);
        passWord = (EditText) findViewById(R.id.password);
        checkPass = (EditText) findViewById(R.id.check_pass);
        sex = (Spinner) findViewById(R.id.sex);
        registerConfirm = (Button) findViewById(R.id.register_confirm);

        spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sexText);
        sex.setAdapter(spinnerAdapter);

        userName.setOnFocusChangeListener(this);
        passWord.setOnFocusChangeListener(this);
        checkPass.setOnFocusChangeListener(this);

        registerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag){
                    return;
                }
                Thread registerThread = new Thread(new RegisterThread());
                registerThread.start();
            }
        });

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    String result = msg.obj.toString();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    //data = (ArrayList<HashMap<String, Object>>) msg.obj;
                    if (result.equals("success")){
                        finish();
                    }else{
                        userName.setText("");
                        userName.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                        userName.setHint(result);
                    }
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    class RegisterThread implements Runnable{

        @Override
        public void run() {
            String result = null;
            String name;
            String pass;
            String sexText;
            try {
                name = userName.getText().toString().trim();
                pass = passWord.getText().toString().trim();
                sexText = (String) sex.getSelectedItem();
                result = RegisterHttp.save(name , pass , sexText);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message msg = handler.obtainMessage();
            if(result!=null){
                //Toast.makeText(LoginActivity.this, "ok", Toast.LENGTH_SHORT).show();
                Log.d("abc", "ok");
                msg.what = 0;
                msg.obj = result;
                handler.sendMessage(msg);
            }else{
                //Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                msg.what = 1;
                handler.sendMessage(msg);
                Log.d("abc","error");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    private boolean checkEdit(){
        if(userName.getText().toString().trim().equals("")){
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        }else if(passWord.getText().toString().trim().equals("")){
            Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
        }else if(!checkPass.getText().toString().trim().equals(passWord.getText().toString().trim())){
            Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
        }else {
            return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String hint = "";
        if(!hasFocus) {
            switch (v.getId()) {
                case R.id.user_name:
                    if (userName.getText().toString().trim().equals("")) {
                        userName.setText("");
                        hint = "用户名不能为空";
                        userName.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                        userName.setHint(hint);
                        flag = false;
                    }else{
                        flag = true;
                    }
                    break;
                case R.id.password:
                    if(passWord.getText().toString().trim().length()<6){
                        passWord.setText("");
                        hint = "密码不能小于6个字符";
                        passWord.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                        passWord.setHint(hint);
                        flag = false;
                    }else{
                        flag = true;
                    }
                    break;
                case R.id.check_pass:
                    if(!checkPass.getText().toString().trim().equals(passWord.getText().toString().trim())){
                        checkPass.setText("");
                        hint = "两次密码输入不一致";
                        checkPass.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                        checkPass.setHint(hint);
                        flag = false;
                    }else{
                        flag = true;
                    }
                    break;
            }
        }else{
            switch (v.getId()) {
                case R.id.user_name:
                    userName.setHint("");
                    break;
                case R.id.password:
                    passWord.setHint("");
                    break;
                case R.id.check_pass:
                    checkPass.setHint("");
                    break;
            }
        }
    }
}
