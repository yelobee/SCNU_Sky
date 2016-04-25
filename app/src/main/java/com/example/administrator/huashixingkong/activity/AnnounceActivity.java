package com.example.administrator.huashixingkong.activity;

import android.os.Bundle;
import com.example.administrator.huashixingkong.R;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

/**
 * Created by yelobee_ on 16/4/26.
 */
public class AnnounceActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}