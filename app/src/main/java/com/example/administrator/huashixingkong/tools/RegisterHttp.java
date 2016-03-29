package com.example.administrator.huashixingkong.tools;



import java.util.HashMap;
import java.util.Map;


public class RegisterHttp {
    private static final String httpUrl = "http://110.65.86.250:8080/scnu_sky/RegisterServlet";

    public static String save(String username, String password, String nickname){
        Map<String,String> datas = new HashMap<>();
        datas.put("username",username);
        datas.put("password",password);
        datas.put("nickname",nickname);
        try {
            return SimpleClient.SendPostRequest(httpUrl, datas, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
}
