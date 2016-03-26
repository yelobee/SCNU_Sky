package com.example.administrator.huashixingkong.activity;

import android.graphics.drawable.Drawable;
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
    private EditText nickName;
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
        nickName = (EditText) findViewById(R.id.nick_name);
        registerConfirm = (Button) findViewById(R.id.register_confirm);


        userName.setCompoundDrawables(setDrawable(R.drawable.login), null, null, null);//只放左边
        passWord.setCompoundDrawables(setDrawable(R.drawable.password), null, null, null);
        checkPass.setCompoundDrawables(setDrawable(R.drawable.password), null, null, null);
        nickName.setCompoundDrawables(setDrawable(R.drawable.nickname), null, null, null);

        userName.setOnFocusChangeListener(this);
        passWord.setOnFocusChangeListener(this);
        checkPass.setOnFocusChangeListener(this);
        nickName.setOnFocusChangeListener(this);

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

    private Drawable setDrawable(int drawID){
        Drawable drawable = getResources().getDrawable(drawID);
        if (drawable != null) {
            drawable.setBounds(20, 0, 70, 50);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        }
        return drawable;
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
            try {
                result = RegisterHttp.save(userName.getText().toString().trim() ,
                        passWord.getText().toString().trim() , nickName.getText().toString().trim());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /* private boolean checkEdit(){
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
    }*/

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
                        if(userName.getText().toString().trim().length()>10){
                            userName.setText("");
                            hint = "用户名不能大于10位";
                            userName.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                            userName.setHint(hint);
                            flag = false;
                        }else{
                            flag = true;
                        }
                    }
                    break;
                case R.id.password:
                    if(passWord.getText().toString().trim().length()<6||
                            passWord.getText().toString().trim().length()>15){
                        passWord.setText("");
                        hint = "密码不能小于6或大于15个字符";
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
                case R.id.nick_name:
                    if (nickName.getText().toString().trim().equals("")) {
                        nickName.setText("");
                        hint = "昵称不能为空";
                        nickName.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                        nickName.setHint(hint);
                        flag = false;
                    }else{
                        if(nickName.getText().toString().trim().length()>10){
                            nickName.setText("");
                            hint = "昵称不能大于10位";
                            nickName.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                            nickName.setHint(hint);
                            flag = false;
                        }else{
                            flag = true;
                        }
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
