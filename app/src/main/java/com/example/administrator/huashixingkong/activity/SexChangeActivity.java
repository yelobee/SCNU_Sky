package com.example.administrator.huashixingkong.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.administrator.huashixingkong.R;

public class SexChangeActivity extends ActionBarActivity {

    private static final String []sexText = {"男","女"};
    private Spinner sex;
    private Button ascConfirm;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex_change);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sex = (Spinner) findViewById(R.id.asc_sex);
        ascConfirm = (Button) findViewById(R.id.asc_confirm);

        spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sexText);
        sex.setAdapter(spinnerAdapter);
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
