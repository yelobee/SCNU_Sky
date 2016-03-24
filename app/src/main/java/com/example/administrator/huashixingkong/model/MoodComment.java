package com.example.administrator.huashixingkong.model;

/**
 * Created by Administrator on 2016/3/24.
 */
public class MoodComment {
    private int m_comment_id;
    private String username;
    private int mood_id;
    private String content;
    private String release_date;
    private boolean is_reply;
    private String reply_user;
    private int like_count;

    public int getM_comment_id() {
        return m_comment_id;
    }

    public void setM_comment_id(int m_comment_id) {
        this.m_comment_id = m_comment_id;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMood_id() {
        return mood_id;
    }

    public void setMood_id(int mood_id) {
        this.mood_id = mood_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public boolean is_reply() {
        return is_reply;
    }

    public void setIs_reply(boolean is_reply) {
        this.is_reply = is_reply;
    }

    public String getReply_user() {
        return reply_user;
    }

    public void setReply_user(String reply_user) {
        this.reply_user = reply_user;
    }

    @Override
    public String toString() {
        return "MoodComment{" +
                "m_comment_id=" + m_comment_id +
                ", username='" + username + '\'' +
                ", mood_id=" + mood_id +
                ", content='" + content + '\'' +
                ", release_date='" + release_date + '\'' +
                ", is_reply=" + is_reply +
                ", reply_user='" + reply_user + '\'' +
                ", like_count=" + like_count +
                '}';
    }
}
