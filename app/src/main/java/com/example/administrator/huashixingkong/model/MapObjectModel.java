package com.example.administrator.huashixingkong.model;

import android.graphics.drawable.Drawable;
import com.example.administrator.huashixingkong.R;
import android.location.Location;

public class MapObjectModel 
{
	private int x;
	private int y;
	private int id;
	private int icon_id;
	private String caption;
	private int detailed;
	private String type;
	private Location location;

	
	public MapObjectModel(int id, Location location,int icon, String caption)
	{
		this.location = location;
		this.setIcon_id(icon);
		this.caption = caption;
		this.detailed=0;
		this.id = id;
	}
	
	public MapObjectModel(int id, int x, int y,int icon, String caption)
	{
		this.id = id;
		this.setIcon_id(icon);
		this.x = x;
		this.y = y;
		this.caption = caption;
		this.detailed=0;
	}

	public MapObjectModel(int id,int x,int y,int icon,String caption,int detailed){
		this.id=id;
		this.setIcon_id(icon);
		this.x=x;
		this.y=y;
		this.caption=caption;
		this.detailed=detailed;
	}
	public MapObjectModel(BuildingPositions bp){
		this.id=bp.getId();
		if (bp.getPng_type().equals("d")) {
			this.setIcon_id(R.drawable.map_icon_double);
		}else {
			this.setIcon_id(R.drawable.map_icon_single);
		}
		this.x=bp.getSreen_x();
		this.y=bp.getSreen_y();
		this.caption=bp.getDesc();
		this.detailed=bp.getDetailed();
		this.type=bp.getType();
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


	public int getDetailed() {
		return detailed;
	}

	public void setDetailed(int detailed) {
		this.detailed = detailed;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}