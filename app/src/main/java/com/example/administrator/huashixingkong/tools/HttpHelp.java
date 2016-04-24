package com.example.administrator.huashixingkong.tools;


import java.util.HashMap;
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

    public static String SaveActivity(String username){
        String path = "http://110.65.86.250:8080/scnu_sky/ActivityServlet";
        Map<String, String> student = new HashMap<>();
        student.put("username", username);
        try {
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveComment(String username){
        String path = "http://110.65.86.250:8080/scnu_sky/CommentServlet";
        Map<String, String> student = new HashMap<>();
        student.put("name", username);
        try {
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
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
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveMComment(Map<String, String> MComment){
        String path = "http://110.65.86.250:8080/scnu_sky/MCommentServlet";
        try {
            return SimpleClient.SendPostRequest(path, MComment, "UTF-8");
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
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveAComment(Map<String, String> AComment){
        String path = "http://110.65.86.250:8080/scnu_sky/ACommentServlet";
        try {
            return SimpleClient.SendPostRequest(path, AComment, "UTF-8");
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
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String SaveLike(String username,String tag,int tagID){
        String path = "http://110.65.86.250:8080/scnu_sky/LikeServlet";
        Map<String, String> student = new HashMap<>();
        student.put("username", username);
        student.put("tag", tag);
        student.put("tagID", String.valueOf(tagID));
        try {
            return SimpleClient.SendPostRequest(path, student, "UTF-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    //发送页面详情请求至服务器
    public static String SaveDetail(int position_id){
        String path="http://110.65.84.43:8080/scnu_sky/PositionDetailServlet";
        Map<String,String> positionDetail=new HashMap<>();
        positionDetail.put("position_id",String.valueOf(position_id));
        try{
            return SimpleClient.SendPostRequest(path,positionDetail,"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //发送进行中活动信息请求至服务器
    public static String SaveActiveActivity(){
        String path="http://110.65.84.43:8080/scnu_sky/ActiveActivityServlet";
        try{
            return SimpleClient.SendPostRequest(path,null,"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
