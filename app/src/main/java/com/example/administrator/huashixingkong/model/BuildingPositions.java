package com.example.administrator.huashixingkong.model;

public class BuildingPositions {
	//position id
	private int id;
	//x坐标
	private int sreen_x;
	//y坐标
	private int sreen_y;
	//所用的png类型
	private String png_type;
	//建筑描述
	private String desc;
	//建筑类型
	private String type;
	//是否有详细介绍
	private int detailed;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSreen_x() {
		return sreen_x;
	}

	public void setSreen_x(int sreen_x) {
		this.sreen_x = sreen_x;
	}

	public int getSreen_y() {
		return sreen_y;
	}

	public void setSreen_y(int sreen_y) {
		this.sreen_y = sreen_y;
	}

	public String getPng_type() {
		return png_type;
	}

	public void setPng_type(String png_type) {
		this.png_type = png_type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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
