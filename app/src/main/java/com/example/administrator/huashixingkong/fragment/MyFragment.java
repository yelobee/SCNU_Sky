package com.example.administrator.huashixingkong.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.activity.LoginActivity;
import com.example.administrator.huashixingkong.activity.PersonalInformationActivity;
import com.example.administrator.huashixingkong.activity.SettingActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class MyFragment extends Fragment {
    private ListView listView;
    private RelativeLayout relativeLayout;
    private String []title = {"设置","关于软件","退出登录"};
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,container,false);
        listView = (ListView) view.findViewById(R.id.fragment_my_list);
        MyAdapter myAdapter = new MyAdapter(getActivity());
        listView.setAdapter(myAdapter);

        relativeLayout = (RelativeLayout)view.findViewById(R.id.fragment_my_personal_information);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PersonalInformationActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), SettingActivity.class);
                    startActivity(intent);
                }
                if (position == 2){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }
    private ArrayList<HashMap<String,Object>> getDate(){
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        for (int i=0;i<3;i++){
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemText", title[i]);
            listItem.add(map);
        }
        return listItem;
    }
    private class MyAdapter extends BaseAdapter{
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局 /*构造函数*/
        public MyAdapter(Context context) {
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
            //Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fragment_my_item_view,
                        null);
                holder = new ViewHolder();
                    /*得到各个控件的对象*/
                holder.image = (ImageView) convertView.findViewById(R.id.fragment_my_list_image);
                holder.text = (TextView) convertView.findViewById(R.id.fragment_my_list_text);
                convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.image.setImageResource(R.drawable.abc);
            holder.text.setText(getDate().get(position).get("ItemText").toString());
            return convertView;
        }
    }
    public final class ViewHolder{
        public ImageView image;
        public TextView text;
    }
}
