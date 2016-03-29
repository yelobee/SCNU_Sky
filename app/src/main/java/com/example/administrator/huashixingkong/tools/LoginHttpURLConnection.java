package com.example.administrator.huashixingkong.tools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class LoginHttpURLConnection {

    /**
     * 登录验证
     * @param name 姓名
     * @param password rn
     */
    public static String save(String name, String password){
        String path = "http://110.65.86.250:8080/scnu_sky/LoginServlet";
        Map<String, String> student = new HashMap<>();
        student.put("name", name);
        student.put("password", password);
        try {
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 发送GET请求
     * @param path  请求路径
     * @param student  请求参数
     * @return 请求是否成功
     * @throws Exception
     */
 /*   private static boolean SendGETRequest(String path, Map<String, String> student, String ecoding) throws Exception{


        StringBuilder url = new StringBuilder(path);
        url.append("?");
        for(Map.Entry<String, String> map : student.entrySet()){
            url.append(map.getKey()).append("=");
            url.append(URLEncoder.encode(map.getValue(), ecoding));
            url.append("&");
        }
        url.deleteCharAt(url.length() - 1);
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection)new URL(url.toString()).openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 200){
            return true;
        }
        return false;
  } */

  /*  private static String SendPostRequest(String path, Map<String, String> student, String ecoding) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(path);
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String,String> entry: student.entrySet()){
            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(),entry.getValue());
            params.add(nameValuePair);
        }
        HttpEntity entity = new UrlEncodedFormEntity(params,ecoding);
        httpPost.setEntity(entity);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,3000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,3000);
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_OK){
            return EntityUtils.toString(response.getEntity(),"UTF-8");
        }
        return null;
    }*/

}
