package com.example.administrator.huashixingkong.tools;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/7.
 */
public class UserJson {
    public static ArrayList<HashMap<String,Object>> Analysis(String jsonStr)
            throws JSONException {
        /******************* 解析 ***********************/
        JSONArray jsonArray = null;
        // 初始化list数组对象
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("user_id", jsonObject.getInt("user_id"));
            map.put("username", jsonObject.getString("username"));
            map.put("sex", jsonObject.getString("sex"));
            map.put("head_image", jsonObject.getString("head_image"));
            map.put("address", jsonObject.getString("address"));
            map.put("hobby", jsonObject.getString("hobby"));
            map.put("signature", jsonObject.getString("signature"));
            list.add(map);
        }
        Log.d("abc",list.toString());
        return list;
    }
}
