package com.example.administrator.huashixingkong.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.myview.DiscussView;
import com.example.administrator.huashixingkong.myview.RefreshLayout;
import com.example.administrator.huashixingkong.myview.SlideListView;
import com.example.administrator.huashixingkong.tools.HttpHelp;
import com.example.administrator.huashixingkong.tools.JsonAnalysis;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscussPageActivity extends ActionBarActivity {

    private ListView ListView;
    private Button button;
    private EditText editText;
    private TextView titleView,dateView,contentView;
    private final String []selectItem1 = {"回复","删除"};
    private final String []selectItem2 = {"回复"};
    private String name;
    private SharedPreferences preferences;
    private DiscussViewAdapter myAdapter;
    private PullToRefreshListView pullToRefreshListView;
    private ArrayList<HashMap<String,Object>> data = new ArrayList<>();
    private int start = 0;
    private HashMap<String, String> map; //用户输入评论
    private HashMap<String,Object> headMessage;//头部信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = this.getSharedPreferences("userData",0);
        name =  preferences.getString("username", "");
        headMessage = (HashMap<String, Object>) getIntent().getExtras().getSerializable("mood");
        initView();
        setEventListener();

        /*DiscussView discussView = new DiscussView(DiscussPageActivity.this,null);
        ListView = (ListView) findViewById(R.id.view_discuss_page_list);
        ListView.addHeaderView(discussView);
        DiscussViewAdapter myAdapter = new DiscussViewAdapter(this);
        //ListView.initSlideMode(SlideListView.MOD_RIGHT);
        ListView.setAdapter(myAdapter);*/

    }

    private void initView(){
        button = (Button) findViewById(R.id.view_discuss_page_button);
        editText = (EditText) findViewById(R.id.view_discuss_page_editText);
        DiscussView discussView = new DiscussView(DiscussPageActivity.this,null);
        titleView = (TextView) discussView.findViewById(R.id.view_discuss_name);
        dateView = (TextView) discussView.findViewById(R.id.view_discuss_time);
        contentView = (TextView) discussView.findViewById(R.id.view_discuss_content);

        titleView.setText(headMessage.get("username").toString());
        dateView.setText(headMessage.get("release_date").toString());
        contentView.setText(headMessage.get("content").toString());

        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.view_discuss_page_list);
        myAdapter = new DiscussViewAdapter(this);
        ListView lv = pullToRefreshListView.getRefreshableView();
        lv.addHeaderView(discussView);
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
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
    }

    private HashMap<String, String> setMComment(){
        map = new HashMap<>();
        map.put("username", name);
        map.put("mood_id", String.valueOf(headMessage.get("mood_id")));
        map.put("content", editText.getText().toString().trim());
        map.put("is_reply", String.valueOf(0));
        map.put("reply_user", null);
        map.put("like_count", String.valueOf(0));
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
                result = HttpHelp.SaveMComment(setMComment());
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
            JsonAnalysis CommentJson = new JsonAnalysis();
            String str;
            try {
                str = HttpHelp.SaveMood(start, (Integer) headMessage.get("mood_id"));
                Log.d("abc",str);
                if(str!=null) {
                    data = CommentJson.CommentAnalysis(str);
                    Log.d("abc", data.toString());
                    if (!data.isEmpty()) {
                        Log.d("abc", "ok");
                        return true;
                    } else {
                        Log.d("abc", "error");
                    }
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

    /*private ArrayList<HashMap<String,Object>> getDate(){
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for (int i=0;i<4;i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemText", "这是第"+i+"行");
            listItem.add(map);
        }
        return listItem;
    }*/

    private class DiscussViewAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 /*构造函数*/
        public DiscussViewAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
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
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.title.setText(data.get(position).get("username").toString());
            holder.content.setText(data.get(position).get("content").toString());
            holder.time.setText(data.get(position).get("release_date").toString());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DiscussPageActivity.this);
                    builder.setTitle("ddd");
                    if (data.get(position).get("username").toString().equals(name)) {
                        builder.setItems(selectItem1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        builder.setItems(selectItem2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                    builder.create().show();
                }
            });
            return convertView;
        }
    }
    public final class ViewHolder{
        public TextView title;
        public TextView time;
        public TextView content;
    }
}
