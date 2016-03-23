package com.example.administrator.huashixingkong.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.activity.ActActivity;
import com.example.administrator.huashixingkong.activity.DiscussPageActivity;
import com.example.administrator.huashixingkong.myview.RefreshLayout;
import com.example.administrator.huashixingkong.tools.HttpHelp;
import com.example.administrator.huashixingkong.tools.JsonAnalysis;
import com.example.administrator.huashixingkong.tools.MyHttp;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class DiscussViewFragment extends Fragment {

    private ListView listView;
    private RefreshLayout myRefreshListView;
    private ArrayList<HashMap<String,Object>> data = new ArrayList<>();
    private ArrayList<HashMap<String,Object>> list = new ArrayList<>();
    private int start = 0;
    private PullToRefreshListView pullToRefreshListView;
    private DiscussViewAdapter myAdapter;
    private View view;

    static DiscussViewFragment newInstance(String s){
        DiscussViewFragment discussViewFragment = new DiscussViewFragment();

        return discussViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_discuss_view,container,false);
            initView(view);
            setEventListener();
        }
       /* Log.d("abc", getActivity().toString());
        listView = (ListView) view.findViewById(R.id.fragment_discuss_list);
        myRefreshListView = (RefreshLayout) view.findViewById(R.id.swipe_layout);

        myAdapter = new DiscussViewAdapter(getActivity());

        myRefreshListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Toast.makeText(getActivity(), "refresh", Toast.LENGTH_SHORT).show();
                myRefreshListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        myRefreshListView.setRefreshing(false);
                        Thread discussThread = new Thread(new DiscussThread());
                        discussThread.start();
                    }
                }, 1000);
            }
        });


        myRefreshListView.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                //Toast.makeText(getActivity(), "load", Toast.LENGTH_SHORT).show();

                myRefreshListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        myRefreshListView.setLoading(false);
                    }
                }, 1500);

            }

        });

        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.setClass(getActivity(), DiscussPageActivity.class);
                startActivity(intent);
            }
        });

        myRefreshListView.setColorSchemeColors(android.R.color.black, android.R.color.white,
                android.R.color.holo_blue_bright, android.R.color.holo_red_dark);*/


        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void initView(View view){
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.fragment_discuss_list);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                pullToRefreshListView.setRefreshing();
            }
        }, 3000);
        myAdapter = new DiscussViewAdapter(getActivity());
        pullToRefreshListView.setAdapter(myAdapter);

        //设置pull-to-refresh模式为Mode.Both
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

    }



    private void setEventListener(){
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新触发的事件
                Log.d("abcd","autorefresh");
                pullToRefreshListView.onRefreshComplete();
                //new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上提加载触发的事件
                new GetDataTask().execute();
            }
        });
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("activity_id", (Integer) data.get(position).get("activity_id"));
                intent.setClass(getActivity(), ActActivity.class);
                startActivity(intent);
            }
        });
    }

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<HashMap<String,Object>>> {

        @Override
        protected ArrayList<HashMap<String,Object>> doInBackground(Void... params) {
            JsonAnalysis ActivityJson = new JsonAnalysis();
            String str;
            try {
                str = HttpHelp.SaveActivity(String.valueOf(start));
                if(str!=null) {
                    data = ActivityJson.ActivityAnalysis(str);
                    Log.d("abc", data.toString());
                    if (!data.isEmpty()) {
                        Log.d("abc", "ok");
                        return data;
                    } else {
                        Log.d("abc", "error");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String,Object>> result) {
            // 调用刷新完成

            myAdapter.notifyDataSetChanged();
            pullToRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    /*Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getActivity().getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
                    //data = (ArrayList<HashMap<String, Object>>) msg.obj;
                    if (list.isEmpty()){
                        list = data;
                    }else{
                        for (int i=0;i<data.size();i++){
                            list.add(data.get(i));
                        }
                    }
                    start = start + data.size();
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    Toast.makeText(getActivity().getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    class DiscussThread implements Runnable{

        @Override
        public void run() {
            JsonAnalysis ActivityJson = new JsonAnalysis();
            String str;
            try {
                str = HttpHelp.SaveActivity(String.valueOf(start));
                if(str!=null) {
                    data = ActivityJson.ActivityAnalysis(str);
                    Log.d("abc", data.toString());
                    Message msg = handler.obtainMessage();
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<HashMap<String,Object>> getDate(){
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fragment_activity_item_view,
                        null);
                holder = new ViewHolder();
                    /*得到各个控件的对象*/
                holder.title = (TextView) convertView.findViewById(R.id.fragment_activity_title);
                holder.message = (TextView) convertView.findViewById(R.id.fragment_activity_message);
                holder.date = (TextView) convertView.findViewById(R.id.fragment_activity_date);
                convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            //holder.title.setText(getDate().get(position).get("ItemText").toString());
            holder.title.setText(data.get(position).get("title").toString());
            holder.message.setText(data.get(position).get("content").toString());
            holder.date.setText(data.get(position).get("release_date").toString()+"发布");
            return convertView;
        }
    }
    public final class ViewHolder{
        public TextView title;
        public TextView message;
        public TextView date;
    }
}
