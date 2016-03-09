package com.example.administrator.huashixingkong.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.administrator.huashixingkong.tools.LoginHttpURLConnection;
import com.example.administrator.huashixingkong.tools.UserJson;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    private Button loginButton;
    private Button registerButton;
    private EditText textUserNumber = null;
    private EditText textPassword = null;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textUserNumber = (EditText) findViewById(R.id.userNumber);
        textPassword = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);

        preferences = getSharedPreferences("userData", Context.MODE_PRIVATE);


        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "登录成功!！", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent();
                    loginIntent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                   /* Intent loginIntent2 = new Intent();
                    loginIntent2.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(loginIntent2);*/
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.login:
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username",textUserNumber.getText().toString());
                editor.putString("password",textPassword.getText().toString());
                editor.commit();
                Thread loginThread = new Thread(new LoginThread());
                loginThread.start();
                break;
            case R.id.register:
                Intent registerIntent = new Intent();
                registerIntent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);break;
            default:break;
        }
    }

    class LoginThread implements Runnable{

        @Override
        public void run() {
            String result;
            String name;
            String password;
            try {
                name = textUserNumber.getText().toString();
                name = new String(name.getBytes("ISO8859-1"), "UTF-8");
                password = textPassword.getText().toString();
                password = new String(password.getBytes("ISO8859-1"), "UTF-8");

                result = LoginHttpURLConnection.save(name, password);
                Log.d("abc",result);
                Message msg = handler.obtainMessage();
                if(result.equals("0")){
                    Log.d("abc","ok");
                    msg.what = 0;
                    handler.sendMessage(msg);
                }else{
                    Log.d("abc","error");
                    msg.what = 1;
                    handler.sendMessage(msg);
                }

            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

}
