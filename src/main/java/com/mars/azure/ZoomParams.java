package com.mars.azure;

public class ZoomParams {

	private Double milesDelta;
	private Double latDelta;
	private Double longDelta;
	private Double latitude;
	private Double longitude;
	private Double multiplier;
	private int recurrencyDepth;
	private int count;
	
	
	public ZoomParams(Double miles, Double lat, Double longi, int d, Double mult) {

		milesDelta = miles;
		latitude = lat;
		longitude = longi;
		recurrencyDepth = d;
		multiplier = mult;
		latDelta = milesDelta / 69D;
		longDelta = milesDelta / (69D * Math.cos(Math.toRadians(latitude)));
		
	}
	
	public Double getMilesDelta() {
		return milesDelta;
	}
	public Double getLatDelta() {
		return latDelta;
	}
	public Double getLongDelta() {
		return longDelta;
	}
	public Double getLatitude() {
		return latitude;
	}
	public Double getLongitude() {
		return longitude;
	}

	public Double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Double multiplier) {
		this.multiplier = multiplier;
	}

	public int getRecurrencyDepth() {
		return recurrencyDepth;
	}

	public void setRecurrencyDepth(int recurrencyDepth) {
		this.recurrencyDepth = recurrencyDepth;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
