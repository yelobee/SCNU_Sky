package com.example.administrator.huashixingkong.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.activity.DiscussPageActivity;
import com.example.administrator.huashixingkong.tools.BitmapCache;
import com.example.administrator.huashixingkong.tools.HttpHelp;
import com.example.administrator.huashixingkong.tools.JsonAnalysis;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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

    public static final int DISCUSS_PAGE_ACTIVITY_RESULT = 0xa4;

    private static final String url = "http://110.65.86.250:8080/scnu_sky";

    private ArrayList<HashMap<String,Object>> data = new ArrayList<>();
    //private int start = 0;
    private String name = null;
    private PullToRefreshListView pullToRefreshListView;
    private DiscussViewAdapter myAdapter;
    private View view;
    private SharedPreferences sharedPreferences;

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
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
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

        if (view == null){
            view = inflater.inflate(R.layout.fragment_comment, container, false);
            sharedPreferences = getActivity().getSharedPreferences("userData", 0);
            name = sharedPreferences.getString("username", null);
            initView(view);
            setEventListener();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void initView(View view){
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.fragment_comment_list);
        myAdapter = new DiscussViewAdapter(getActivity());
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
                //pullToRefreshListView.onRefreshComplete();
                new GetDataTask().execute();
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
                Bundle bundle = new Bundle();
                bundle.putInt("position",position - 1);
                bundle.putSerializable("mood",data.get(position-1));
                intent.putExtras(bundle);
                intent.setClass(getActivity(), DiscussPageActivity.class);
                getRootFragment().startActivityForResult(intent, DISCUSS_PAGE_ACTIVITY_RESULT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==DISCUSS_PAGE_ACTIVITY_RESULT){
            if(resultCode== DiscussPageActivity.DISCUSS_PAGE_ACTIVITY_RESULT_LIKE){
                int position = getActivity().getIntent().getIntExtra("position",0);
                int i;
                HashMap<String,Object> Mp = this.data.get(position);
                i = (int)Mp.get("like_count");
                Mp.remove("like_count");
                Mp.remove("like_id");
                Mp.put("like_count", i + 1);
                Mp.put("like_id", 1);
                this.data.remove(position);
                this.data.add(position, Mp);
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 得到根Fragment
     *
     * @return
     */
    private Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;

    }

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<HashMap<String,Object>>> {

        @Override
        protected ArrayList<HashMap<String,Object>> doInBackground(Void... params) {
            String str;
            try {
                str = HttpHelp.SaveComment(name);
                if(str!=null) {
                    data = JsonAnalysis.MoodAnalysis(str);
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

    private ArrayList<HashMap<String,Object>> getDate(){
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        for (int i=0;i<4;i++){
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemText", "这是第"+i+"行");
            listItem.add(map);
        }
        return listItem;
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
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.image, android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
            String filePath = (String)data.get(position).get("head_image");
            if(null != filePath){
                mImageLoader.get(url + filePath, listener);
            }
            holder.title.setText(data.get(position).get("nickname").toString());
            holder.message.setText(data.get(position).get("content").toString());
            holder.date.setText(data.get(position).get("release_date").toString() + "发布");
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
