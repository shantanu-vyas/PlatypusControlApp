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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
    
    @SuppressLint("NewApi")
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
        
        rudder.setProgress(50);
        
        //serverTest();
        
        
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
    public static void serverTest()
    {
    	String sHostName = "127.0.0.1";
        InetAddress IPAddress = null;
        boolean done;
        boolean keepGoing;

        String s1;
        ArrayList lines = new ArrayList();
        int size;
        BufferedReader br;

        try {
            IPAddress = InetAddress.getByName(sHostName);
            System.out.println ("Attemping to connect to " + IPAddress +
                                ") via UDP port 9876");
        }
        catch (UnknownHostException ex)
            {
                System.err.println(ex);
                System.exit(1);
            }


        // set up the buffered reader to read from the keyboard
        try {
            //br = new BufferedReader (new FileReader ("UDPInputFile.txt"));
        	s1 = "test";
            
            while (s1 != null)
                {
                    lines.add(s1);
                  
                }
            size = lines.size();
            System.out.println ("ArrayList lines has size of: " + size);

            done = false;

            DatagramSocket clientSocket = new DatagramSocket();
            for (int i = 0; i < size ; i++)
                {

                    byte[] sendData = new byte[1024];

                    s1 = (String) lines.get(i);
                    sendData = s1.getBytes();

                    System.out.println ("Sending data to " + sendData.length +
                                        " bytes to server from line " + (i + 1));
                    DatagramPacket sendPacket =
                        new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

                    clientSocket.send(sendPacket);
                }
            done = true;

            byte[] receiveData = new byte[1024];

            keepGoing = true;

            DatagramPacket receivePacket =
                new DatagramPacket(receiveData, receiveData.length);

            System.out.println ("Waiting for return packet");
            clientSocket.setSoTimeout(10000);

            while (keepGoing)
                {
                    try {
                        clientSocket.receive(receivePacket);
                        String modifiedSentence =
                            new String(receivePacket.getData());

                        //InetAddress returnIPAddress = receivePacket.getAddress();

                        //int port = receivePacket.getPort();

                        //System.out.println ("From server at: " + returnIPAddress +
                        //                    ":" + port);
                        System.out.println("Message: " + modifiedSentence);

                    }
                    catch (SocketTimeoutException ste)
                        {
                            System.out.println ("Timeout Occurred: Packet assumed lost");
                            if (done)
                                keepGoing = false;
                        }

                }
            clientSocket.close();
        }
        catch (IOException ex)
            {
                System.err.println(ex);
            }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this,MapTest.class));
	}

    }//class
    