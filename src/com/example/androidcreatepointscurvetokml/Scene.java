package com.example.androidcreatepointscurvetokml;

import com.google.android.gms.maps.model.LatLng;

public class Scene {

	String name;
	LatLng latLng;
	double[] ecef = new double[3];
	double alt = 100;
	GeoTool geoTool = new GeoTool();
	public Scene(String name, LatLng latlng) {
		this.name = name;
		this.latLng = latlng;
		getLLAToECEF();
	}
	
	 private double[] getLLAToECEF(){
		 this.ecef= geoTool.lla2ecef(new double[]{latLng.latitude,latLng.longitude,alt});
		 
		return ecef; 
	 }
		 
	
	 
}
