package com.example.administrator.huashixingkong.activity;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.FoldingTextView;;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;;
import android.view.MenuItem;
import android.widget.TextView.BufferType;

public class PositionDetailActivity extends ActionBarActivity {
	
	private String s="ahgoanboanoaghasg";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		FoldingTextView tv=(FoldingTextView) findViewById(R.id.desc_collapse_tv);
		tv.setDesc(s,BufferType.NORMAL);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
}
