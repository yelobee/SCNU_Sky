package com.example.administrator.huashixingkong.tools;

import java.util.HashMap;
import java.util.Map;


public class MyHttp {

    public static String save(String name){
        String path = "http://110.65.86.250:8080/scnu_sky/UserServlet";
        Map<String, String> student = new HashMap<>();
        student.put("name", name);
        try {
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String ModifySave(String name,String title, String content){
        String path = "http://110.65.86.250:8080/scnu_sky/ChangeServlet";
        Map<String, String> student = new HashMap<>();
        student.put("name", name);
        student.put("title", title);
        student.put("content", content);
        try {
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String PassSave(String name,String oldPass,String newPass){
        String path = "http://110.65.86.250:8080/scnu_sky/ChangePassServlet";
        Map<String, String> student = new HashMap<>();
        student.put("name", name);
        student.put("oldPass", oldPass);
        student.put("newPass", newPass);
        try {
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


}
