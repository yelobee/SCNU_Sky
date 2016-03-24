package com.example.administrator.huashixingkong.model;

import android.graphics.drawable.Drawable;
import android.location.Location;

public class MapObjectModel 
{
	private int x;
	private int y;
	private int id;
	private int icon_id;
	private String caption;
	private Location location;
	
	public MapObjectModel(int id, Location location,int icon, String caption)
	{
		this.location = location;
		this.setIcon_id(icon);
		this.caption = caption;
		this.id = id;
	}
	
	public MapObjectModel(int id, int x, int y,int icon, String caption)
	{
		this.id = id;
		this.setIcon_id(icon);
		this.x = x;
		this.y = y;
		this.caption = caption;
	}

	public int getId() 
	{
		return id;
	}

	
	public int getX() 
	{
		return x;
	}


	public int getY() 
	{
		return y;
	}
	
	
	public Location getLocation()
	{
		return location;
	}
	
	
	public String getCaption()
	{
		return caption;
	}

	public int getIcon_id() {
		return icon_id;
	}

	public void setIcon_id(int icon_id) {
		this.icon_id = icon_id;
	}


}