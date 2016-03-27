package com.example.administrator.huashixingkong.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Created by Administrator on 2016/3/27.
 */
public class DeleteThread implements Runnable{

    private Handler handler;
    private int ID;
    private int tag;//所属评论标志

    public DeleteThread(int tag,int ID,Handler handler){
        this.handler = handler;
        this.ID = ID;
        this.tag = tag;
    }

    @Override
    public void run() {
        String result = null;
        try {
            result = HttpHelp.SaveDelete(tag,ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage();
        if(result!=null){
            msg.what = 0;
            msg.obj = result;
            handler.sendMessage(msg);
        }else{
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }
}
