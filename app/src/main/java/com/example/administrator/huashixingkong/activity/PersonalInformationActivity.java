package com.example.administrator.huashixingkong.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.MyHttp;
import com.example.administrator.huashixingkong.tools.JsonAnalysis;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class PersonalInformationActivity extends ActionBarActivity {

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_RESULT_REQUEST = 0xa1;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;

    private ListView listView;
    private static String title[] = {"性别","地区","兴趣","个性签名"};
    private static String content[] = {"sex","address","hobby","signature"};
    private LinearLayout linearLayout;
    private ImageView headImage = null;
    private TextView textView;

    private String name;
    private ArrayList<HashMap<String,Object>> data;
    private ProgressDialog progressDialog;
    private PersonalInformationAdapter personalInformationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        progressDialog = ProgressDialog.show(PersonalInformationActivity.this, "Loading...", "Please wait...", true, false);
        linearLayout = (LinearLayout) findViewById(R.id.head_image);
        textView = (TextView) findViewById(R.id.activity_personal_information_text);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseHeadImageFromGallery();
            }
        });

        SharedPreferences preferences = this.getSharedPreferences("userData",0);
        name =  preferences.getString("username","");
        data = new ArrayList<>();

        Thread informationThread = new Thread(new InformationThread());
        informationThread.start();

        textView.setText(name);
        listView = (ListView) findViewById(R.id.activity_personal_information_list);
        personalInformationAdapter = new PersonalInformationAdapter(PersonalInformationActivity.this);
        listView.setAdapter(personalInformationAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetListItemOnClick();
    }

    @Override
    protected void onRestart() {
        Log.d("abc","信息重新进入");
        progressDialog.show();
        Thread informationThread = new Thread(new InformationThread());
        informationThread.start();
        super.onRestart();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //关闭ProgressDialog
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "成功！", Toast.LENGTH_SHORT).show();
                    personalInformationAdapter.notifyDataSetChanged();
                    //data = (ArrayList<HashMap<String, Object>>) msg.obj;

                    break;
                case 1:
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    class InformationThread implements Runnable{

        @Override
        public void run() {
            String str;
            try {
                str = MyHttp.save(name);
                Message msg = handler.obtainMessage();
                if(str!=null) {
                    data = JsonAnalysis.UserAnalysis(str);
                    Log.d("abc", data.toString());
                    if (!data.isEmpty()) {
                        Log.d("abc", "ok");
                        msg.what = 0;
                        msg.obj = data;
                        handler.sendMessage(msg);
                    } else {
                        Log.d("abc", "error");
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }else{
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用户没有进行有效的设置操作，返回
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(data.getData());
                break;

            case CODE_RESULT_REQUEST:
                if (data != null) {
                    setImageToHeadView(data);
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            headImage.setImageBitmap(photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_information, menu);
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

    private ArrayList<HashMap<String,Object>> getDate(){
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        for (int i=0;i<4;i++){
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemTitle", title[i]);
            listItem.add(map);
        }
        return listItem;
    }

    private void SetListItemOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("name",name);
                intent.putExtra("title",content[position]);
                intent.putExtra("content",((TextView)view.findViewById(R.id.personal_information_message)).getText());
                if(position == 0){
                    intent.setClass(PersonalInformationActivity.this, SexChangeActivity.class);
                }else{
                    intent.setClass(PersonalInformationActivity.this, ModifyActivity.class);
                }
                startActivity(intent);
            }
        });
    }

    private class PersonalInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 /*构造函数*/
        public PersonalInformationAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return getDate().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_personal_information_item_view,
                        null);
                holder = new ViewHolder();
                    /*得到各个控件的对象*/
                holder.title = (TextView) convertView.findViewById(R.id.personal_information_title);
                holder.message = (TextView) convertView.findViewById(R.id.personal_information_message);
                convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.title.setText(getDate().get(position).get("ItemTitle").toString());

            if (!data.isEmpty()){
                holder.message.setText(data.get(0).get(content[position]).toString());
            }

            return convertView;
        }
    }
    public final class ViewHolder{
        public TextView title;
        public TextView message;
    }
}
