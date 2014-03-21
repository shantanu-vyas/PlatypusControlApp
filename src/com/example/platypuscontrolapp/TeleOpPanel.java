package com.example.platypuscontrolapp;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;

public class TeleOpPanel extends Activity implements OnClickListener{
    
    SeekBar thrust = null;
    SeekBar rudder = null;
    TextView ipAddressBox = null;
    TextView thrustProgress = null;
    TextView rudderProgress = null;
    LinearLayout linlay = null;
    CheckBox autonomous = null;
    Button mapButton = null;
    static TextView testIP = null;
    Thread networkThread = null;
    TextView test = null;
    Handler network = new Handler();
    
    DatagramSocket socket = null;
    DatagramPacket inPacket = null;
    DatagramPacket outPacket = null;
    byte[] inBuf;
	byte[] outBuf;
    int PORT = 8888;
    String msg = null; 
    int a = 0;
    String data = "";
    
	
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.teleoppanel);
        
        ipAddressBox = (TextView)this.findViewById(R.id.printIpAddress);
        thrust = (SeekBar)this.findViewById(R.id.thrustBar);
        rudder = (SeekBar)this.findViewById(R.id.rudderBar);
        linlay = (LinearLayout)this.findViewById(R.id.linlay);
        thrustProgress = (TextView)this.findViewById(R.id.getThrustProgress);
        rudderProgress = (TextView)this.findViewById(R.id.getRudderProgress);
        //testIP = (TextView)this.findViewById(R.id.test);
        mapButton = (Button)this.findViewById(R.id.mapButton);
        autonomous = (CheckBox)this.findViewById(R.id.Autonomous);
        test = (TextView)this.findViewById(R.id.infotest);
        		
        thrust.setProgress(0);
        rudder.setProgress(50);
        
        new NetworkAsync().execute();
        
        
        if (validIP(ConnectScreen.textIpAddress))
        {
            ipAddressBox.setBackgroundColor(Color.GREEN);
        }
        else
        {
            ipAddressBox.setBackgroundColor(Color.RED);
        }
        
        if (ConnectScreen.textIpAddress != "")
        {
            ipAddressBox.setText("Boats IP: " + ConnectScreen.textIpAddress + "");
        }
        if (ConnectScreen.simul == true)
        {
            ipAddressBox.setText("Simulated Phone");
        }
        seekBarValue(thrust,thrustProgress);
        seekBarValue(rudder,rudderProgress);
        mapButton = (Button)this.findViewById(R.id.mapButton);
		mapButton.setOnClickListener(this);
		 		
    }
    
    public static boolean validIP(String ip) {
        if (ip == null || ip == "") return false;
        ip = ip.trim();
        if ((ip.length() < 6) & (ip.length() > 15)) return false;
        
        try {
            Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
            Matcher matcher = pattern.matcher(ip);
            return matcher.matches();
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }
    public static void seekBarValue(SeekBar seekBar, final TextView text)
    {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
                // TODO Auto-generated method stub
                text.setText(String.valueOf(progress));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                
                // TODO Auto-gbenerated method stub
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
       
    }
    public void serverTest()
    {			 
    	System.out.println("server test asdf");
        
	    try {
	 
	    	System.out.println("shantanu");
	        InetAddress address = InetAddress.getByName("http://10.0.2.2:8080");
	        socket = new DatagramSocket();
	        msg = "shantanu";
	        outBuf = msg.getBytes();
	        outPacket = new DatagramPacket(outBuf, 0, outBuf.length, address, PORT);
	        socket.send(outPacket);
	        inBuf = new byte[256];
	        inPacket = new DatagramPacket(inBuf, inBuf.length);
	        socket.receive(inPacket);
	        data = new String(inPacket.getData(), 0, inPacket.getLength());
	     	System.out.println("Server : " + data);
	        test.setText("server" );
	      	}
	    catch (IOException ioe) {
     	   	data = ioe.toString();
	        System.out.println(ioe);
    	      test.setText(ioe.toString());
	    }
	}


	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this,MapTest.class));
	}
	
	
		private class NetworkAsync extends AsyncTask<String, Integer, String>
	{
		
		long oldTime = 0;
		@Override
		protected String doInBackground(String... arg0) {
			while(true)
			{
				if(System.currentTimeMillis() % 100 == 0 && oldTime != System.currentTimeMillis())
				{
					serverTest();	
					//serverThread();
					oldTime = System.currentTimeMillis();
					a +=1;
					publishProgress();
				}
			}
		}
		@Override
		protected void onProgressUpdate(Integer... result)
		{
	
			test.setText("" + a + "\n");			// TODO Auto-generated method stub
			
		}
		
		
	}

}//class
    