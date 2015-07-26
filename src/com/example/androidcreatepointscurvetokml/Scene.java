package com.example.androidcreatepointscurvetokml;

import com.google.android.gms.maps.model.LatLng;

public class Scene {

	String name;
	LatLng latLng;
	
	public Scene(String name, LatLng latlng) {
		this.name = name;
		this.latLng = latlng;
	}
	 
}
