package com.example.androidcreatepointscurvetokml;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {

	private GoogleMap googleMap;
	ArrayList<Scene> sceneList;

	LatLng GreenStreet = new LatLng(24.136570, 120.663010); // 美術館綠廊道
	LatLng CityPark = new LatLng(24.205640, 120.596920);// 都會公園
	LatLng Temple_JENNLANN = new LatLng(24.347720, 120.623530);// 鎮瀾宮
	LatLng Bridge = new LatLng(24.244820, 120.746490);// '花樑鋼橋

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void AddSceneList() {
		sceneList = new ArrayList<Scene>();
		sceneList.add(new Scene("美術館綠廊道", GreenStreet));
		sceneList.add(new Scene("都會公園", CityPark));
		sceneList.add(new Scene("鎮瀾宮", Temple_JENNLANN));
		sceneList.add(new Scene("花樑鋼橋", Bridge));

	}

	private void addMarkers() {

		for (Scene scene : sceneList) {

			googleMap.addMarker(new MarkerOptions().position(scene.latLng)
					.title(scene.name));

		}

	}

	public void centerIncidentRouteOnMap(List<Scene> sceneList) {
		double minLat = Integer.MAX_VALUE;
		double maxLat = Integer.MIN_VALUE;
		double minLon = Integer.MAX_VALUE;
		double maxLon = Integer.MIN_VALUE;
		for (Scene scene : sceneList) {
			maxLat = Math.max(scene.latLng.latitude, maxLat);
			minLat = Math.min(scene.latLng.latitude, minLat);
			maxLon = Math.max(scene.latLng.longitude, maxLon);
			minLon = Math.min(scene.latLng.longitude, minLon);
		}
		final LatLngBounds bounds = new LatLngBounds.Builder()
				.include(new LatLng(maxLat, maxLon))
				.include(new LatLng(minLat, minLon)).build();

		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;
		int padding = (int) (width * 0.12); // offset from edges of the map 12%
											// of screen
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width,
				height, padding);

		googleMap.animateCamera(cu);
	}

	private ArrayList<Scene> randomTwoPoint(ArrayList<Scene> scenes){
		LatLng start =scenes.get(0).latLng;
		LatLng end =scenes.get(scenes.size()-1).latLng;
		LatLng newStart = getAdjustedLatLon(start);
		LatLng newEnd = getAdjustedLatLon(end);
		//LatLng cp =midPoint(start.latitude,start.longitude,end.latitude,end.longitude);
			googleMap.addMarker(new MarkerOptions().position(newStart)
					.title("newStart").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

			googleMap.addMarker(new MarkerOptions().position(newEnd)
					.title("newStart").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

			for(Scene scene :randomTwoPoint_v2(sceneList)){

				googleMap.addMarker(new MarkerOptions().position(scene.latLng)
						.title("newStart").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			}
			for(Scene scene :randomTwoPoint_v3(sceneList)){

				googleMap.addMarker(new MarkerOptions().position(scene.latLng)
						.title("newStart").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
			}
//			for(Scene scene :randomTwoPoint_v4(sceneList)){
//
//				googleMap.addMarker(new MarkerOptions().position(scene.latLng)
//						.title("newStart").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//			}
			
			
		return scenes;
		
	}
	private ArrayList<Scene> randomTwoPoint_v2(ArrayList<Scene> scenes){
		 ArrayList<Scene> slist = new ArrayList<Scene>();
		LatLng start =scenes.get(0).latLng;
		LatLng end =scenes.get(scenes.size()-1).latLng;
		
			LatLng cp =midPoint(start.latitude,start.longitude,end.latitude,end.longitude);
			CartesianCoordinates cart1 = new CartesianCoordinates(start);
			CartesianCoordinates cart2 = new CartesianCoordinates(end);
			CartesianCoordinates cart3 = new CartesianCoordinates(CityPark.latitude, CityPark.longitude);
			 
			for (double t = 0; t <= 1.0; t += 0.1) {
			    double oneMinusT = (1.0 - t);
			    double t2 = Math.pow(t, 2);
			 
			    double y = oneMinusT * oneMinusT * cart1.y + 2 * t * oneMinusT * cart3.y + t2 * cart2.y;
			    double x = oneMinusT * oneMinusT * cart1.x + 2 * t * oneMinusT * cart3.x + t2 * cart2.x;
			    double z = oneMinusT * oneMinusT * cart1.z + 2 * t * oneMinusT * cart3.z + t2 * cart2.z;
			    LatLng control = CartesianCoordinates.toLatLng(x, y, z);
			    
			        Log.v("MainActivity", "t: " + t + control.toString());
			        slist.add(new Scene("ABC", control));
			} 
		
		
		return slist;
		
	}
	private ArrayList<Scene> randomTwoPoint_v3(ArrayList<Scene> scenes){
		 ArrayList<Scene> slist = new ArrayList<Scene>();
		LatLng start =scenes.get(0).latLng;
		LatLng end =scenes.get(scenes.size()-1).latLng;
		
			LatLng cp =midPoint(start.latitude,start.longitude,end.latitude,end.longitude);
			CartesianCoordinates cart1 = new CartesianCoordinates(start);
			CartesianCoordinates cart2 = new CartesianCoordinates(end);
			CartesianCoordinates cart3 = new CartesianCoordinates(Temple_JENNLANN.latitude, Temple_JENNLANN.longitude);
			 
			for (double t = 0; t <= 1.0; t += 0.05) {
			    double oneMinusT = (1.0 - t);
			    double t2 = Math.pow(t, 2);
			
			    double y = oneMinusT * oneMinusT * cart1.y + 2 * t * oneMinusT * cart3.y + t2 * cart2.y;
			    double x = oneMinusT * oneMinusT * cart1.x + 2 * t * oneMinusT * cart3.x + t2 * cart2.x;
			    double z = oneMinusT * oneMinusT * cart1.z + 2 * t * oneMinusT * cart3.z + t2 * cart2.z;
			    LatLng control = CartesianCoordinates.toLatLng(x, y, z);
			    
			        Log.v("MainActivity", "t: " + t + control.toString());
			        slist.add(new Scene("ABC", control));
			} 
		
		
		return slist;
		
	}
	private ArrayList<Scene> randomTwoPoint_v4(ArrayList<Scene> scenes){
		Tuple3d[] sPlist;
		 ArrayList<Scene> slist = new ArrayList<Scene>();
		LatLng start =scenes.get(0).latLng;
		LatLng end =scenes.get(scenes.size()-1).latLng;
		CartesianCoordinates cart1 = new CartesianCoordinates(start);
		CartesianCoordinates cart2 = new CartesianCoordinates(end);
			Tuple3d control1s = new Tuple3d(cart1.x, cart1.y, cart1.z);
			Tuple3d control2s = new Tuple3d(cart2.x, cart2.y, cart2.z);
			Tuple3d [] controls = new Tuple3d[2];
			controls[0] = control1s;
			controls[1] = control2s;
 			LatLng cp =midPoint(start.latitude,start.longitude,end.latitude,end.longitude);
	     Bezier bezier =new Bezier(scenes);
	     sPlist =Bezier.getCurvePoints(controls, 0.1);
	     for(Tuple3d tuple3d :sPlist){
	    	 LatLng control = CartesianCoordinates.toLatLng(tuple3d.x,tuple3d.y, tuple3d.z);
	    	 slist.add(new Scene("WOW", control));
	     }
	     
		    
		return slist;
		
	}
	public  LatLng midPoint(double lat1,double lon1,double lat2,double lon2)
    {
		return new LatLng((lat1+lat2)/2, (lon1+lon2)/2);

    }
	private  LatLng midPoint(LatLng p1, LatLng p2) throws IllegalArgumentException{

	    if(p1 == null || p2 == null)
	        throw new IllegalArgumentException("two points are needed for calculation");

	    double lat1;
	    double lon1;
	    double lat2;
	    double lon2;

	    //convert to radians
	    lat1 = Math.toRadians(p1.latitude);
	    lon1 = Math.toRadians(p1.longitude);
	    lat2 = Math.toRadians(p2.latitude);
	    lon2 = Math.toRadians(p2.longitude);

	    double x1 = Math.cos(lat1) * Math.cos(lon1);
	    double y1 = Math.cos(lat1) * Math.sin(lon1);
	    double z1 = Math.sin(lat1);

	    double x2 = Math.cos(lat2) * Math.cos(lon2);
	    double y2 = Math.cos(lat2) * Math.sin(lon2);
	    double z2 = Math.sin(lat2);

	    double x = (x1 + x2)/2;
	    double y = (y1 + y2)/2;
	    double z = (z1 + z2)/2;

	    double lon = Math.atan2(y, x);
	    double hyp = Math.sqrt(x*x + y*y);

	    // HACK: 0.9 and 1.1 was found by trial and error; this is probably *not* the right place to apply mid point shifting
	    double lat = Math.atan2(.9*z, hyp); 
	    if(lat>0) lat = Math.atan2(1.1*z, hyp);

	        Log.v("MainActivity", Math.toDegrees(lat) + " " + Math.toDegrees(lon));

	    return new LatLng(Math.toDegrees(lat),  Math.toDegrees(lon));
	}

	//x and y are offsets in meters
	private LatLng getAdjustedLatLon(LatLng points) { 
		double offsetLat = 1000;
		double offsetLon = 1000;
		//double offsetAlt = 1;

	double objLat = points.latitude;
	double objLon = points.longitude;
	double[] latlon = new double[2];
	double dLat = offsetLat/6378100.0;
	double dLon = offsetLon/(6378100.0*Math.cos(Math.PI*objLat/180.));
	latlon[0] = objLat + dLat * 180./Math.PI;
	latlon[1] = objLon + dLon * 180./Math.PI;
	
	return new LatLng(latlon[0] , latlon[1]);
	}
	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
		AddSceneList();
		Log.d("MainActivity", "Size:" + sceneList.size());
		addMarkers();
		centerIncidentRouteOnMap(sceneList);
		randomTwoPoint(sceneList);
		
	}
	
	
}
class CartesianCoordinates { 
private static final int R = 6378137; // approximate radius of earth
double x;
double y;
double z;
 
public CartesianCoordinates(LatLng p) {
    this(p.latitude, p.longitude);
} 
 
public CartesianCoordinates(double lat, double lon) {
    double _lat = Math.toRadians(lat);
    double _lon = Math.toRadians(lon);
 
    x = R * Math.cos(_lat) * Math.cos(_lon);
    y = R * Math.cos(_lat) * Math.sin(_lon);
    z = R * Math.sin(_lat);
} 
 
public static LatLng toLatLng(double x, double y, double z){
        return new LatLng(Math.toDegrees(Math.asin(z / R)), Math.toDegrees(Math.atan2(y, x)));
    } 
} 
