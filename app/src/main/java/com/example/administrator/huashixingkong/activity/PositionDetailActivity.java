package com.example.administrator.huashixingkong.activity;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.HttpHelp;
import com.example.administrator.huashixingkong.tools.JsonAnalysis;;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class PositionDetailActivity extends ActionBarActivity {

	private int position_id;
	private String position_name;
	private String position_detail;

	private TextView tv_detail;
	private TextView tv_name;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		progressDialog = ProgressDialog.show(PositionDetailActivity.this, "Loading...", "Please wait...", true, false);

		position_id = getIntent().getIntExtra("position_id", 0);

		tv_name=(TextView)findViewById(R.id.building_title);
		tv_detail = (TextView) findViewById(R.id.desc_collapse_tv);
		tv_detail.setMovementMethod(ScrollingMovementMethod.getInstance());

		new Thread(new detailMessage()).start();
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case 0:
					tv_name.setText(position_name);
					tv_detail.setText(position_detail);
					progressDialog.dismiss();
					break;
				case 1:
					progressDialog.dismiss();
					Toast.makeText(PositionDetailActivity.this, "无法显示地点详情", Toast.LENGTH_SHORT).show();
					finish();
					break;

			}
		}
	};



	class detailMessage implements Runnable{

		@Override
		public void run() {
			String result = null;
			try {
				result = HttpHelp.SaveDetail(position_id);//请求服务器返回json字符
				//可以用JsonAnalysis转换MAP
				ArrayList<HashMap<String,Object>> position_detail_list= JsonAnalysis.PositionDetailAnalysis(result);
				position_name= (String) position_detail_list.get(0).get("position_name");
				position_detail= ToDBC((String) position_detail_list.get(0).get("position_detail"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message msg = handler.obtainMessage();
			if(result!=null){
				//Toast.makeText(LoginActivity.this, "ok", Toast.LENGTH_SHORT).show();
				Log.d("detailMessage", "ok");
				msg.what = 0;
				msg.obj = result;
				handler.sendMessage(msg);
			}else{
				//Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
				msg.what = 1;
				handler.sendMessage(msg);
				Log.d("detailMessage","error");
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

	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i< c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}if (c[i]> 65280&& c[i]< 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

}
