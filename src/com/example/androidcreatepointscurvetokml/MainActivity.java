package com.example.androidcreatepointscurvetokml;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {

	 // Google Map
    private GoogleMap googleMap;
    ArrayList<Scene> sceneList ;
    
    
    
      
    
    
    LatLng GreenStreet = new LatLng(24.136570, 120.663010); //美術館綠廊道
    LatLng CityPark = new LatLng(24.205640,120.596920);//都會公園
    LatLng Temple_JENNLANN  = new LatLng(24.347720,120.623530);//鎮瀾宮
    LatLng Bridge = new LatLng(24.244820,120.746490);//'花樑鋼橋
    private LatLngBounds latLngBounds = new LatLngBounds(
            new LatLng(24.136570, 120.663010), //美術館綠廊道
            new LatLng(24.347720, 120.622850));//文昌祠

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        try {
            // Loading map
            initilizeMap();
            AddSceneList();
            addMarkers();
           
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    private void AddSceneList(){
    	sceneList = new ArrayList<Scene>();
    	sceneList.add(new Scene("美術館綠廊道", GreenStreet));
    	sceneList.add(new Scene("都會公園", CityPark));
    	sceneList.add(new Scene("鎮瀾宮", Temple_JENNLANN));
    	sceneList.add(new Scene("花樑鋼橋", Bridge));
    	
    	
    }
    private void addMarkers() {
    	
    	for(Scene scene:sceneList){
   
    	 googleMap.addMarker(new MarkerOptions()
            .position(scene.latLng).title(scene.name));
    
          }
    	 centerIncidentRouteOnMap(sceneList);
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
        final LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(maxLat, maxLon)).include(new LatLng(minLat, minLon)).build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50));
    } 
   
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                    .target(Temple_JENNLANN)
                    .zoom(17)
                    .build();
         
            // 使用動畫的效果移動地圖
          googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
          //  LatLng Bridge = new LatLng(24.244820,120.746490);//'花樑鋼橋
            

            // bounds
           // googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundss, 0));
            // check if map is created successfully or not
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
    }
}
