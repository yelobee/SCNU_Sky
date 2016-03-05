package com.example.administrator.huashixingkong.activity;

import android.content.Intent;
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

import java.io.UnsupportedEncodingException;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    private Button loginButton;
    private Button registerButton;
    private EditText textUserNumber = null;
    private EditText textPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textUserNumber = (EditText) findViewById(R.id.userNumber);
        textPassword = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.login);
        registerButton = (Button) findViewById(R.id.register);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }



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
                new Thread(){
                    public void run(){
                        boolean result = false;
                        String name;
                        String password;
                        try {
                            name = textUserNumber.getText().toString();
                            name = new String(name.getBytes("ISO8859-1"), "UTF-8");
                            password = textPassword.getText().toString();
                            password = new String(password.getBytes("ISO8859-1"), "UTF-8");
                            result = LoginHttpURLConnection.save(name, password);
                        } catch (UnsupportedEncodingException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        if(result){
                            //Toast.makeText(LoginActivity.this, "ok", Toast.LENGTH_SHORT).show();
                            Log.d("abc","ok");
                        }else{
                            //Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                            Log.d("abc","error");
                        }
                    }
                }.start();
                Intent loginIntent = new Intent();
                loginIntent.setClass(LoginActivity.this,MainActivity.class);
                startActivity(loginIntent);break;
            case R.id.register:
                Intent registerIntent = new Intent();
                registerIntent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);break;
            default:break;
        }
    }
}
