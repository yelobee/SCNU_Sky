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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHelp {

    /*public static String save(String name){
        String path = "http://110.65.86.250:8080/scnu_sky/LoginServlet";
        Map<String, String> student = new HashMap<String, String>();
        student.put("name", name);
        try {
            return SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }*/

    public static String SaveActivity(String start){
        String path = "http://110.65.86.250:8080/scnu_sky/ActivityServlet";
        Map<String, String> student = new HashMap<>();
        student.put("start", start);
        try {
            return SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveComment(String start,String username){
        String path = "http://110.65.86.250:8080/scnu_sky/CommentServlet";
        Map<String, String> student = new HashMap<>();
        student.put("start", start);
        student.put("name", username);
        try {
            return SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveMood(int moodID){
        String path = "http://110.65.86.250:8080/scnu_sky/MoodServlet";
        Map<String, String> student = new HashMap<>();
        student.put("mood_id", String.valueOf(moodID));
        try {
            return SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveMComment(Map<String, String> MComment){
        String path = "http://110.65.86.250:8080/scnu_sky/MCommentServlet";
        try {
            return SendPostRequest(path, MComment, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String GetAComment(int activityID){
        String path = "http://110.65.86.250:8080/scnu_sky/GetAComment";
        Map<String, String> student = new HashMap<>();
        student.put("activity_id", String.valueOf(activityID));
        try {
            return SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveAComment(Map<String, String> AComment){
        String path = "http://110.65.86.250:8080/scnu_sky/ACommentServlet";
        try {
            return SendPostRequest(path, AComment, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveDelete(int tag,int ID){
        String path = "http://110.65.86.250:8080/scnu_sky/DeleteCommentServlet";
        Map<String, String> student = new HashMap<>();
        student.put("tag", String.valueOf(tag));
        student.put("commentID", String.valueOf(ID));
        try {
            return SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static String SendPostRequest(String path, Map<String, String> student, String ecoding) throws IOException {
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
        if (response.getStatusLine().getStatusCode()== HttpURLConnection.HTTP_OK){
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        return null;
    }
}
