package com.example.administrator.huashixingkong.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.tools.BitmapCache;
import com.example.administrator.huashixingkong.tools.DeleteThread;
import com.example.administrator.huashixingkong.tools.HttpHelp;
import com.example.administrator.huashixingkong.tools.JsonAnalysis;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class ActActivity extends ActionBarActivity {

    private static final String url = "http://110.65.86.250:8080/scnu_sky";

    private Button button;
    private EditText editText;
    private final String []selectItem1 = {"删除"};
    private final String []selectItem2 = {"回复"};
    private String name;
    private DiscussViewAdapter myAdapter;
    private PullToRefreshListView pullToRefreshListView;
    private ArrayList<HashMap<String,Object>> data = new ArrayList<>();
    //private int start = 0;
    private HashMap<String, String> map; //用户输入评论
    private HashMap<String,Object> headMessage;//头部信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = this.getSharedPreferences("userData", 0);
        name =  preferences.getString("username", "");
        headMessage = (HashMap<String, Object>) getIntent().getExtras().getSerializable("activity");
        initView();
        setEventListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        button = (Button) findViewById(R.id.activity_act_button);
        editText = (EditText) findViewById(R.id.activity_act_editText);
        pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.activity_act_list);
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView dateView = (TextView) findViewById(R.id.date);
        TextView contentView = (TextView) findViewById(R.id.content);

        titleView.setText(headMessage.get("title").toString());
        dateView.setText(headMessage.get("begin_time").toString() + "~" + headMessage.get("end_time").toString());
        contentView.setText(headMessage.get("content").toString());

        myAdapter = new DiscussViewAdapter(this);
        pullToRefreshListView.setAdapter(myAdapter);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                pullToRefreshListView.setRefreshing();
            }
        }, 100);
        //设置pull-to-refresh模式为Mode.Both
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

    }

    private void setEventListener(){
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新触发的事件
                new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上提加载触发的事件
                //new GetDataTask().execute();
            }
        });
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread messageThread = new Thread(new MessageThread());
                messageThread.start();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
    }

    private HashMap<String, String> setAComment(){
        map = new HashMap<>();
        map.put("username", name);
        map.put("activity_id", String.valueOf(headMessage.get("activity_id")));
        map.put("content", editText.getText().toString().trim());
        map.put("is_reply", String.valueOf(0));
        map.put("reply_tag", String.valueOf(0));
        map.put("like_count", String.valueOf(0));
        if(data.size()!=0){
            map.put("tag", data.get(0).get("tag").toString());
        }else{
            map.put("tag", String.valueOf(1));
        }
        return map;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "成功！", Toast.LENGTH_SHORT).show();
                    //data = (ArrayList<HashMap<String, Object>>) msg.obj;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullToRefreshListView.setRefreshing();
                        }
                    }, 100);
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();

                    break;
            }
            editText.getText().clear();
        }
    };

    class MessageThread implements Runnable{

        @Override
        public void run() {
            String result = null;
            try {
                result = HttpHelp.SaveAComment(setAComment());
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

    private class GetDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String str;
            try {
                str = HttpHelp.GetAComment((Integer)headMessage.get("activity_id"));
                Log.d("abc",str);
                if(str!=null) {
                    data = JsonAnalysis.ACommentAnalysis(str);
                    Log.d("abc", data.toString());
                    if (!data.isEmpty()) {
                        Log.d("abc", "ok");
                    } else {
                        Log.d("abc", "error");
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // 调用刷新完成
            if(result) {
                myAdapter.notifyDataSetChanged();
            }
            pullToRefreshListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    private class DiscussViewAdapter extends BaseAdapter {
        private ImageLoader mImageLoader;
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 /*构造函数*/
        public DiscussViewAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            RequestQueue mQueue = Volley.newRequestQueue(context);
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        @Override
        public int getCount() {
            return data.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_discuss_item_view2,
                        null);
                holder = new ViewHolder();
                    /*得到各个控件的对象*/
                holder.headImage = (ImageView) convertView.findViewById(R.id.head_image);
                holder.like = (ImageView) convertView.findViewById(R.id.like_button);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.tag = (TextView) convertView.findViewById(R.id.tag);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.content = (TextView) convertView.findViewById(R.id.content);
                holder.likeCount = (TextView) convertView.findViewById(R.id.like_count);
                convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.content.setText(data.get(position).get("content").toString());
            holder.time.setText(data.get(position).get("release_date").toString());
            holder.likeCount.setText(data.get(position).get("like_count").toString());
            String tag = data.get(position).get("tag").toString()+"楼";
            holder.tag.setText(tag);

            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.headImage, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
            String filePath = (String)data.get(position).get("head_image");
            if(null != filePath){
                mImageLoader.get(url + filePath, listener);
            }

            if(data.get(position).get("is_reply").toString().equals("1")){
                String replyStr = data.get(position).get("nickname").toString()+
                        "回复"+data.get(position).get("reply_tag").toString()+"楼";
                holder.title.setText(replyStr);
            }else{
                holder.title.setText(data.get(position).get("nickname").toString());
            }

            if(data.get(position).get("like_id").equals("null")){
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String result = null;
                                try {
                                    result = HttpHelp.SaveLike(name,"a_comment_id",(int)data.get(position).get("a_comment_id"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Message msg = handler.obtainMessage();
                                if(result!=null){
                                    msg.what = 0;
                                    msg.obj = result;
                                }else{
                                    msg.what = 1;
                                }
                                handler.sendMessage(msg);
                            }
                        }).start();
                        v.setClickable(false);
                    }
                });
            }else{
                Drawable drawable = getResources().getDrawable(R.drawable.like);
                holder.like.setImageDrawable(drawable);
                holder.like.setClickable(false);
            }

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ActActivity.this);
                    builder.setTitle("ddd");
                    if (data.get(position).get("username").toString().equals(name)) {
                        builder.setItems(selectItem1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Thread thread = new Thread(new DeleteThread(1,
                                        (int) data.get(position).get("a_comment_id"), handler));
                                thread.start();
                            }
                        });

                    } else {
                        builder.setItems(selectItem2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra("mood_id", data.get(position).get("mood_id").toString());
                                intent.putExtra("reply_tag", data.get(position).get("tag").toString());
                                intent.putExtra("tag", data.get(0).get("tag").toString());
                                intent.setClass(ActActivity.this, ReplyActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                    }

                    builder.create().show();
                    return false;
                }
            });
            return convertView;
        }
    }
    public final class ViewHolder{
        public ImageView headImage;
        public ImageView like;
        public TextView title;
        public TextView tag;
        public TextView time;
        public TextView content;
        public TextView likeCount;
    }

}
