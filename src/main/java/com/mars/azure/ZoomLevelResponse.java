package com.mars.azure;

import java.util.LinkedList;
import java.util.List;

public class ZoomLevelResponse {

	private Double squareSideInMiles;
	private int count;
	private List<ZoomParams> details = new LinkedList<ZoomParams>();

	public ZoomLevelResponse(ZoomParams zp) {
		details.add( zp );
		squareSideInMiles = zp.getMilesDelta() * 2;
		count = zp.getCount();
	}
	
	public Double getSquareSideInMiles() {
		return squareSideInMiles;
	}

	public void setSquareSideInMiles(Double squareSideInMiles) {
		this.squareSideInMiles = squareSideInMiles;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<ZoomParams> getDetails() {
		return details;
	}
	
}
