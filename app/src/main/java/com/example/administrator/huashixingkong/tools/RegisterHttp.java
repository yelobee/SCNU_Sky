package com.example.administrator.huashixingkong.tools;


import android.util.Log;

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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/4.
 */
public class RegisterHttp {
    private static final String httpUrl = "http://192.168.191.1:8080/scnu_sky/RegisterServlet";

    public static String save(String username, String password, String sex) throws Exception {
        Map<String,String> datas = new HashMap<String,String>();
        datas.put("username",username);
        datas.put("password",password);
        datas.put("sex",sex);
        return SendPostRequest(datas);
    }

    public static String SendPostRequest(Map<String,String> datas)throws Exception{
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(httpUrl);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (Map.Entry<String,String> entry: datas.entrySet()){
            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(),entry.getValue());
            params.add(nameValuePair);
        }
        HttpEntity entity = new UrlEncodedFormEntity(params,"UTF-8");
        request.setEntity(entity);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,3000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
        HttpResponse response = httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK){
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }

        return null;
    }
}
