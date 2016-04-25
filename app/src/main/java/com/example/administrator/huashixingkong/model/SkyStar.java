package com.example.administrator.huashixingkong.model;

/**
 * Created by yelobee_ on 16/4/25.
 */
public class SkyStar {
    private int sky_id;
    private int screen_x;
    private int screen_y;
    private String user_name;
    private String comment;
    private String release_date;
    private int isSky;

    public int getSky_id() {
        return sky_id;
    }

    public void setSky_id(int sky_id) {
        this.sky_id = sky_id;
    }

    public int getScreen_x() {
        return screen_x;
    }

    public void setScreen_x(int screen_x) {
        this.screen_x = screen_x;
    }

    public int getScreen_y() {
        return screen_y;
    }

    public void setScreen_y(int screen_y) {
        this.screen_y = screen_y;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getIsSky() {
        return isSky;
    }

    public void setIsSky(int isSky) {
        this.isSky = isSky;
    }
}
