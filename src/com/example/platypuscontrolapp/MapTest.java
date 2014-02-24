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

	        LatLng pittsburgh = new LatLng((float)40.436871,(float)-79.948825);

	        map.setMyLocationEnabled(true);
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pittsburgh, 15));
	        map.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );

	        map.addMarker(new MarkerOptions()
	                .title("Pittsburgh")
	                .snippet("asdfasdf pittsburgh")
	                .position(pittsburgh));
	        
	  }
}