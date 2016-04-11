package com.example.administrator.huashixingkong.activity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.MyHttp;

public class UpdatePasswordActivity extends ActionBarActivity implements View.OnFocusChangeListener{

    private Button button;
    private EditText oldPass;
    private EditText newPass;
    private EditText checkPass;
    private Boolean flag = false;
    private String name;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button = (Button) findViewById(R.id.change_password_confirm);
        oldPass = (EditText) findViewById(R.id.old_password);
        newPass = (EditText) findViewById(R.id.new_password);
        checkPass = (EditText) findViewById(R.id.check_pass);

        sharedPreferences = this.getSharedPreferences("userData",0);
        name = sharedPreferences.getString("username", "");

        oldPass.setCompoundDrawables(setDrawable(R.drawable.password), null, null, null);
        newPass.setCompoundDrawables(setDrawable(R.drawable.password), null, null, null);
        checkPass.setCompoundDrawables(setDrawable(R.drawable.password), null, null, null);

        oldPass.setOnFocusChangeListener(this);
        newPass.setOnFocusChangeListener(this);
        checkPass.setOnFocusChangeListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag){
                    return;
                }
                Thread updatePassThread = new Thread(new UpdatePassThread());
                updatePassThread.start();
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
                        oldPass.setText("");
                        oldPass.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                        oldPass.setHint(result);
                    }
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    class UpdatePassThread implements Runnable{

        @Override
        public void run() {
            String result = null;
            try {
                result = MyHttp.PassSave(name, oldPass.getText().toString().trim()
                        , newPass.getText().toString().trim());
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
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String hint = "";
        if(!hasFocus) {
            switch (v.getId()) {
                case R.id.old_password:
                    if (oldPass.getText().toString().trim().equals("")) {
                        oldPass.setText("");
                        hint = "旧密码不能为空";
                        oldPass.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                        oldPass.setHint(hint);
                        flag = false;
                    }else{
                        flag = true;
                    }
                    break;
                case R.id.password:
                    if(newPass.getText().toString().trim().length()<6){
                        newPass.setText("");
                        hint = "密码不能小于6个字符";
                        newPass.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                        newPass.setHint(hint);
                        flag = false;
                    }else{
                        flag = true;
                    }
                    break;
                case R.id.check_pass:
                    if(!checkPass.getText().toString().trim().equals(newPass.getText().toString().trim())){
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
                case R.id.old_password:
                    oldPass.setHint("");
                    break;
                case R.id.new_password:
                    newPass.setHint("");
                    break;
                case R.id.check_pass:
                    checkPass.setHint("");
                    break;
            }
        }
    }
}
