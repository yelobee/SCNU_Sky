package com.example.administrator.huashixingkong.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.administrator.huashixingkong.activity.DiscussPageActivity;
import com.example.administrator.huashixingkong.myview.RefreshLayout;
import com.example.administrator.huashixingkong.tools.HttpHelp;
import com.example.administrator.huashixingkong.tools.JsonAnalysis;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listView;
    private RefreshLayout myRefreshListView;
    private ArrayList<HashMap<String,Object>> data;
    private int start = 0;
    private String name = null;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        Log.d("abc", getActivity().toString());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userData", 0);
        name = sharedPreferences.getString("username",null);

        listView = (ListView) view.findViewById(R.id.fragment_comment_list);
        myRefreshListView = (RefreshLayout) view.findViewById(R.id.swipe_layout);

        DiscussViewAdapter myAdapter = new DiscussViewAdapter(getActivity());
        
        myRefreshListView.setColorSchemeColors(android.R.color.black, android.R.color.white,
                android.R.color.holo_blue_bright, android.R.color.holo_red_dark);

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
        return view;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(getActivity().getApplicationContext(), "成功！", Toast.LENGTH_SHORT).show();
                    //data = (ArrayList<HashMap<String, Object>>) msg.obj;
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
            JsonAnalysis CommentJson = new JsonAnalysis();
            String str;
            try {
                str = HttpHelp.SaveComment(String.valueOf(start), name);
                if(str!=null) {
                    data = CommentJson.CommentAnalysis(str);
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
    }
    private class DiscussViewAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 /*构造函数*/
        public DiscussViewAdapter(Context context) {
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
                convertView = mInflater.inflate(R.layout.fragment_discuss_item_view,
                        null);
                holder = new ViewHolder();
                    /*得到各个控件的对象*/
                holder.image = (ImageView) convertView.findViewById(R.id.fragment_discuss_image);
                holder.title = (TextView) convertView.findViewById(R.id.fragment_discuss_title);
                holder.message = (TextView) convertView.findViewById(R.id.fragment_discuss_message);
                holder.date = (TextView) convertView.findViewById(R.id.fragment_discuss_date);
                convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.image.setImageResource(R.drawable.abc);
            holder.title.setText(getDate().get(position).get("ItemText").toString());
            return convertView;
        }
    }
    public final class ViewHolder{
        public ImageView image;
        public TextView title;
        public TextView message;
        public TextView date;
    }

}
