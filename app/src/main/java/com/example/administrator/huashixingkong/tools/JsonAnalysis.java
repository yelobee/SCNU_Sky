package com.example.administrator.huashixingkong.tools;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class JsonAnalysis {

    public static ArrayList<HashMap<String,Object>> UserAnalysis(String jsonStr)
            throws JSONException {
        /******************* 解析 ***********************/
        JSONArray jsonArray;
        // 初始化list数组对象
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<>();
            map.put("username", jsonObject.getString("username"));
            map.put("nickname", jsonObject.getString("nickname"));
            map.put("sex", jsonObject.getString("sex"));
            map.put("head_image", jsonObject.getString("head_image"));
            map.put("address", jsonObject.getString("address"));
            map.put("hobby", jsonObject.getString("hobby"));
            map.put("signature", jsonObject.getString("signature"));
            list.add(map);
        }
        //Log.d("abc",list.toString());
        return list;
    }

    public static ArrayList<HashMap<String,Object>> ActivityAnalysis(String jsonStr)
            throws JSONException {
        /******************* 解析 ***********************/
        JSONArray jsonArray;
        // 初始化list数组对象
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<>();
            map.put("activity_id", jsonObject.getInt("activity_id"));
            map.put("title", jsonObject.getString("title"));
            map.put("content", jsonObject.getString("content"));
            map.put("begin_time", jsonObject.getString("begin_time"));
            map.put("end_time", jsonObject.getString("end_time"));
            map.put("release_date", jsonObject.getString("release_date"));
            list.add(map);
        }
        //Log.d("abc",list.toString());
        return list;
    }

    public static ArrayList<HashMap<String,Object>> CommentAnalysis(String jsonStr)
            throws JSONException {
        /******************* 解析 ***********************/
        JSONArray jsonArray;
        // 初始化list数组对象
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<>();
            map.put("nickname",jsonObject.getString("nickname"));
            map.put("m_comment_id", jsonObject.getInt("m_comment_id"));
            map.put("username", jsonObject.getString("username"));
            map.put("mood_id", jsonObject.getInt("mood_id"));
            map.put("content", jsonObject.getString("content"));
            map.put("release_date", jsonObject.getString("release_date"));
            map.put("is_reply", jsonObject.getInt("is_reply"));
            map.put("reply_tag", jsonObject.getInt("reply_tag"));
            map.put("like_count", jsonObject.getInt("like_count"));
            map.put("tag", jsonObject.getInt("tag"));
            map.put("like_id", jsonObject.getString("like_id"));
            map.put("head_image",jsonObject.getString("head_image"));
            list.add(map);
        }
        //Log.d("abc",list.toString());
        return list;
    }

    public static ArrayList<HashMap<String,Object>> MoodAnalysis(String jsonStr)
            throws JSONException {
        /******************* 解析 ***********************/
        JSONArray jsonArray;
        // 初始化list数组对象
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<>();
            map.put("nickname", jsonObject.getString("nickname"));
            map.put("mood_id", jsonObject.getInt("mood_id"));
            map.put("username", jsonObject.getString("username"));
            map.put("content", jsonObject.getString("content"));
            map.put("release_date", jsonObject.getString("release_date"));
            map.put("like_count", jsonObject.getInt("like_count"));
            map.put("head_image",jsonObject.getString("head_image"));
            list.add(map);
        }
        //Log.d("abc",list.toString());
        return list;
    }

    public static ArrayList<HashMap<String,Object>> ACommentAnalysis(String jsonStr)
            throws JSONException {
        /******************* 解析 ***********************/
        JSONArray jsonArray;
        // 初始化list数组对象
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        jsonArray = new JSONArray(jsonStr);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            // 初始化map数组对象
            HashMap<String, Object> map = new HashMap<>();
            map.put("nickname",jsonObject.getString("nickname"));
            map.put("a_comment_id", jsonObject.getInt("a_comment_id"));
            map.put("username", jsonObject.getString("username"));
            map.put("activity_id", jsonObject.getInt("activity_id"));
            map.put("content", jsonObject.getString("content"));
            map.put("release_date", jsonObject.getString("release_date"));
            map.put("is_reply", jsonObject.getInt("is_reply"));
            map.put("reply_tag", jsonObject.getInt("reply_tag"));
            map.put("like_count", jsonObject.getInt("like_count"));
            map.put("tag", jsonObject.getInt("tag"));
            map.put("like_id", jsonObject.getString("like_id"));
            map.put("head_image",jsonObject.getString("head_image"));
            list.add(map);
        }
        //Log.d("abc",list.toString());
        return list;
    }

}
