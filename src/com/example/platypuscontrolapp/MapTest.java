package com.example.platypuscontrolapp;


import android.app.Activity;

import android.os.Bundle;

public class MapTest extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maptest);
        
       // GoogleMap gmap = (GoogleMap)this.findViewById(R.id.map);
       // LatLng sydney = new LatLng(-33.867, 151.206);

        //gmap.setMyLocationEnabled(true);
        //gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

    }
}
