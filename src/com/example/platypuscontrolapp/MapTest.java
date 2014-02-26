package com.example.platypuscontrolapp;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;


import android.app.Activity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MapTest extends Activity {
	
		SeekBar thrust = null;
	    SeekBar rudder = null;
	    TextView thrustProgress = null;
	    TextView rudderProgress = null;
	    TextView loca = null;
	    CheckBox autonomous = null;


 	
	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.maptest);
	       
	        thrust = (SeekBar)this.findViewById(R.id.thrustBar);
	        rudder = (SeekBar)this.findViewById(R.id.rudderBar);
	        thrustProgress = (TextView)this.findViewById(R.id.getThrustProgress);
	        rudderProgress = (TextView)this.findViewById(R.id.getRudderProgress);
	        autonomous = (CheckBox)this.findViewById(R.id.Autonomous);
	        
	        rudder.setProgress(50);
	        seekBarValue(thrust,thrustProgress);
	        seekBarValue(rudder,rudderProgress);
	        
	       
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
	        
	       map.addMarker(new MarkerOptions()
            .position(pittsburgh)
            .title("boat 123123")
            .snippet("ip address: q123123"));
           // .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
            //.infoWindowAnchor(0.5f, 0.5f));
            
	  }
	public static void seekBarValue(SeekBar seekBar, final TextView text)
	{
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) 
			{
            // TODO Auto-generated method stub
				text.setText(String.valueOf(progress));
			}
        
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) 
			{
            
            // TODO Auto-generated method stub
			}
        
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) 
			{
            // TODO Auto-generated method stub
			}
    });
	}
}