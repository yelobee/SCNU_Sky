package com.example.administrator.huashixingkong.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.administrator.huashixingkong.tools.SimpleClient;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PersonalInformationActivity extends ActionBarActivity {

    /* 请求识别码 */
    private static final int REQUEST_CODE_TAKE = 0xa0;
    private static final int REQUEST_CODE_PICK = 0xa1;
    private static final int REQUEST_CODE_CUTTING = 0xa2;

    /*头像名称*/
    private String IMAGE_FILE_NAME;
    private static final String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/SCNU_SKY/";

    // 裁剪后图片的宽(X)和高(Y),300 X 300的正方形。
    private final static int output_X = 300;
    private final static int output_Y = 300;

    /*头像sdcard路径*/
    private String headImageUrl;

    private ListView listView;
    private static String title[] = {"昵称","性别","地区","兴趣","个性签名"};
    private static String content[] = {"nickname","sex","address","hobby","signature"};
    private String []selectItem = {"拍照","从相册选择"};
    private LinearLayout linearLayout;
    private ImageView headImage;
    private TextView textView;

    private String name;
    private ArrayList<HashMap<String,Object>> data;
    private ProgressDialog progressDialog,pd;
    private PersonalInformationAdapter personalInformationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        progressDialog = ProgressDialog.show(PersonalInformationActivity.this, "Loading...", "Please wait...", true, false);
        linearLayout = (LinearLayout) findViewById(R.id.head_image);
        headImage = (ImageView) findViewById(R.id.activity_personal_information_image);
        textView = (TextView) findViewById(R.id.activity_personal_information_text);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        SharedPreferences preferences = this.getSharedPreferences("userData",0);
        name =  preferences.getString("username","");
        IMAGE_FILE_NAME = name + ".jpg";
        data = new ArrayList<>();

        Thread informationThread = new Thread(new InformationThread());
        informationThread.start();

        textView.setText(name);
        setHeadImage();
        listView = (ListView) findViewById(R.id.activity_personal_information_list);
        personalInformationAdapter = new PersonalInformationAdapter(PersonalInformationActivity.this);
        listView.setAdapter(personalInformationAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetListItemOnClick();
    }

    private void setHeadImage(){
        File mFile = new File(IMAGE_PATH,IMAGE_FILE_NAME);
        //若该文件存在
        if (mFile.exists()) {
            Bitmap bitmap= BitmapFactory.decodeFile(IMAGE_PATH + IMAGE_FILE_NAME);
            headImage.setImageDrawable(new BitmapDrawable(null,bitmap));
        }
    }

    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformationActivity.this);
        builder.setTitle("设置头像");
        builder.setItems(selectItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //指定调用相机拍照后的照片存储的路径
                        if (isSdcardExisting()) {
                            isDirExisting();
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(new File(IMAGE_PATH, IMAGE_FILE_NAME)));
                            startActivityForResult(takeIntent, REQUEST_CODE_TAKE);
                        }else{
                            Toast.makeText(getApplicationContext(), "请插入sd卡", Toast.LENGTH_LONG)
                                    .show();
                        }
                        break;
                    case 1:
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如：image/jpeg 、 image/png等的类型
                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(pickIntent, REQUEST_CODE_PICK);
                        break;
                }
            }
        });
        builder.create().show();
    }

    private boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void isDirExisting() {
        File file = new File(IMAGE_PATH);
        if (!file.exists()){
            if(file.mkdir()){
                Log.d("abc","创建文件夹");
            }
        }
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
                case 3:
                    Toast.makeText(getApplicationContext(), "成功！", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), "失败！", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用户没有进行有效的设置操作，返回
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUEST_CODE_TAKE:// 调用相机拍照
                if (isSdcardExisting()) {
                    isDirExisting();
                    File temp = new File(IMAGE_PATH + IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(temp));
                }else {
                    Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪原始的图片
     */
    public void startPhotoZoom(Uri uri) {

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

        startActivityForResult(intent, REQUEST_CODE_CUTTING);
    }

    private String saveBitmap (String fileName, Bitmap bitmap){
        isDirExisting();
        File file = new File(IMAGE_PATH,fileName);
        if(file.exists()){
            file.delete();
        }
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,fileOutputStream); //图片压缩
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.i("abc", "已经保存");
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setPicToView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            headImageUrl = saveBitmap(IMAGE_FILE_NAME,photo);
            headImage.setImageDrawable(drawable);
            // 新线程后台上传服务端
            pd = ProgressDialog.show(PersonalInformationActivity.this, null, "正在上传图片，请稍候...");
            new Thread(new ImageThread()).start();
        }
    }

    class ImageThread implements Runnable{

        @Override
        public void run() {
            File file = new File(headImageUrl);
            Message msg = handler.obtainMessage();
            String result = SimpleClient.uploadFile(file);
            if(null!=result){
                if(result.equals("1")){
                    msg.what = 3;
                }else{
                    msg.what = 4;
                }
            }else{
                msg.what = 4;
            }
            handler.sendMessage(msg);
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

    private ArrayList<HashMap<String,Object>> getDate(){
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        for (int i=0;i<5;i++){
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
                if(position == 1){
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
