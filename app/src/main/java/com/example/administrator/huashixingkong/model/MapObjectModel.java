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
	private int active;
	private String type;
	private int isSky=0;
	private Location location;

	
	public MapObjectModel(int id, Location location,int icon, String caption)
	{
		this.location = location;
		this.setIcon_id(icon);
		this.caption = caption;
		this.detailed=0;
		this.id = id;
		this.active=0;
	}
	
	public MapObjectModel(int id, int x, int y,int icon, String caption)
	{
		this.id = id;
		this.setIcon_id(icon);
		this.x = x;
		this.y = y;
		this.caption = caption;
		this.detailed=0;
		this.active=0;
	}

	public MapObjectModel(int id,int x,int y,int icon,String caption,int detailed){
		this.id=id;
		this.setIcon_id(icon);
		this.x=x;
		this.y=y;
		this.caption=caption;
		this.detailed=detailed;
		this.active=0;
	}

	public MapObjectModel(int id,int x,int y,int icon,String caption,int detailed,int active){
		this.id=id;
		this.setIcon_id(icon);
		this.x=x;
		this.y=y;
		this.caption=caption;
		this.detailed=0;
		this.active=active;
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
		this.active=0;
	}

	public MapObjectModel(ActiveActivity aa){
		this.id=aa.getActivity_id();
		this.setIcon_id(R.drawable.maps_blue_dot);
		this.x=aa.getScreen_x();
		this.y=aa.getScreen_y();
		String[] begin=aa.getBegin_time().split(" ");
		String[] end=aa.getEnd_time().split(" ");
		this.caption=aa.getTitle()+"\n"+aa.getContent()+"\n"+begin[0]+"~"+end[0];
		this.detailed=0;
		this.type="活动";
		this.active=aa.getActive();
	}

	public MapObjectModel(SkyStar ss){
		this.id=ss.getSky_id();
		int int_temp=(int)(1+Math.random()*(3-1+1));
		switch (int_temp){
			case 1:
				this.setIcon_id(R.drawable.map_star_25px);
				break;
			case 2:
				this.setIcon_id(R.drawable.map_star_20px);
				break;
			case 3:
				this.setIcon_id(R.drawable.map_star_15px);
				break;
		}
		this.x=ss.getScreen_x();
		this.y=ss.getScreen_y();
		String str_temp=ss.getRelease_date().replace(" ","-");
		this.caption=ss.getUser_name()+":\n"+ss.getComment()+"\n"+str_temp;
		this.detailed=0;
		this.type="星空";
		this.active=0;
		this.isSky=ss.getIsSky();
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

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}
}