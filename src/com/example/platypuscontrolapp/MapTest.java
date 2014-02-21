package com.example.platypuscontrolapp;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;


import android.app.Activity;

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class MapTest extends Activity {
	  @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.maptest);
	        TextView loca = (TextView)this.findViewById(R.id.loc);
	        // Get a handle to the Map Fragment
	        GoogleMap map = ((MapFragment) getFragmentManager()
	                .findFragmentById(R.id.map)).getMap();

	        LatLng pittsburgh = new LatLng(80.0000, 40.4417);

	        map.setMyLocationEnabled(true);
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pittsburgh, 2));

	        map.addMarker(new MarkerOptions()
	                .title("Pittsburgh")
	                .snippet("asdfasdf pittsburgh")
	                .position(pittsburgh));
	        Location location = map.getMyLocation();
	        //String s = location.toString();
	       // loca.setText(s);
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.maptest);
//        
 //       GoogleMap gmap = (GoogleMap)this.findViewById(R.id.map);
  //      LatLng sydney = new LatLng(-33.867, 151.206);

        //gmap.setMyLocationEnabled(true);
        //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

    }
}
