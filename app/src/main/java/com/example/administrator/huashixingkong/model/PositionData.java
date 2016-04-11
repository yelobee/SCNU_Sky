package com.example.administrator.huashixingkong.model;

import java.util.List;

public class PositionData {
	//所获取的positionList版本
	private int config_version;
	//所包含的positionList
	private List<BuildingPositions> positions;

	public int getConfig_version() {
		return config_version;
	}

	public void setConfig_version(int config_version) {
		this.config_version = config_version;
	}

	public List<BuildingPositions> getPositions() {
		return positions;
	}

	public void setPositions(List<BuildingPositions> positions) {
		this.positions = positions;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Version:" + config_version + " have " + positions.size() + " positions";
	}
}
