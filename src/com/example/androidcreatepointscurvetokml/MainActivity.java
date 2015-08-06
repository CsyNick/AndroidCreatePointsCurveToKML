package com.example.androidcreatepointscurvetokml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity implements OnClickListener{

	private GoogleMap googleMap;
	ArrayList<Scene> sceneList;

	LatLng GreenStreet = new LatLng(24.136570, 120.663010); // 美術館綠廊道
	LatLng CityPark = new LatLng(24.205640, 120.596920);// 都會公園
	LatLng Temple_JENNLANN = new LatLng(24.347720, 120.623530);// 鎮瀾宮
	LatLng Bridge = new LatLng(24.244820, 120.746490);// '花樑鋼橋

	//UI Declaration
	Button btn_gotoGoogleEarth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findviews();
	}

	private void findviews(){
		 btn_gotoGoogleEarth = (Button) findViewById(R.id.btn_gotoGoogleEarth);
		 btn_gotoGoogleEarth.setOnClickListener(this);
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
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_gotoGoogleEarth:
			
			File file = new File(Environment.getExternalStorageDirectory(), "district.kml");
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.google-earth.kml+xml");
			intent.putExtra("com.google.earth.EXTRA.tour_feature_id", "my_track");
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	
}
 
