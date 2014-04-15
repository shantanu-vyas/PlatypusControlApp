package com.example.platypuscontrolapp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import edu.cmu.ri.crw.CrwNetworkUtils;
import edu.cmu.ri.crw.data.Twist;
import edu.cmu.ri.crw.udp.UdpVehicleServer;
import java.net.InetSocketAddress;

public class TeleOpPanel extends Activity implements OnClickListener
	{

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
		String ipAddress;
		String serverData;
		String clientData;
		InetSocketAddress addr;
		UdpVehicleServer server = null;
		Twist twist = new Twist();
	

		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				this.setContentView(R.layout.teleoppanel);
				initBoat();
				ipAddressBox = (TextView) this.findViewById(R.id.printIpAddress);
				thrust = (SeekBar) this.findViewById(R.id.thrustBar);
				rudder = (SeekBar) this.findViewById(R.id.rudderBar);
				linlay = (LinearLayout) this.findViewById(R.id.linlay);
				thrustProgress = (TextView) this.findViewById(R.id.getThrustProgress);
				rudderProgress = (TextView) this.findViewById(R.id.getRudderProgress);
				// testIP = (TextView)this.findViewById(R.id.test);
				mapButton = (Button) this.findViewById(R.id.mapButton);
				autonomous = (CheckBox) this.findViewById(R.id.Autonomous);
				test = (TextView) this.findViewById(R.id.infotest);
				thrust.setProgress(0);
				rudder.setProgress(50);
				
				//testing new boat stuff
				

				new NetworkAsync().execute();
				// serverTest();

//				if (ConnectScreen.getBoatType() == true)
//					{
//
//						if (validIP(ConnectScreen.textIpAddress))
//							{
//								ipAddressBox.setBackgroundColor(Color.GREEN);
//							} else
//							{
//								ipAddressBox.setBackgroundColor(Color.RED);
//							}
//						if (ConnectScreen.textIpAddress != "")
//							{
//								ipAddressBox.setText("Boats IP: " + ConnectScreen.getIpAddress() + "");
//							}
//					}
				if (ConnectScreen.getBoatType() == false)
					{
						ipAddressBox.setText("Simulated Phone");
					}
				seekBarValue(thrust, thrustProgress);
				seekBarValue(rudder, rudderProgress);
				mapButton = (Button) this.findViewById(R.id.mapButton);
				mapButton.setOnClickListener(this);

			}
		

		public static boolean validIP(String ip)
			{
				if (ip == null || ip == "")
					return false;
				ip = ip.trim();
				if ((ip.length() < 6) & (ip.length() > 15))
					return false;

				try
					{
						Pattern pattern = Pattern
								.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
						Matcher matcher = pattern.matcher(ip);
						return matcher.matches();
					} catch (PatternSyntaxException ex)
					{
						return false;
					}
			}

		public static void seekBarValue(SeekBar seekBar, final TextView text)
			{
				seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
					{
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
							{
								// TODO Auto-generated method stub
								text.setText(String.valueOf(progress));
							}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar)
							{

								// TODO Auto-gbenerated method stub
							}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar)
							{
								// TODO Auto-generated method stub
							}
					});

			}

		

		@Override
		public void onClick(View v)
			{
				// TODO Auto-generated method stub
				startActivity(new Intent(this, MapTest.class));
			}

		public void initBoat()
			{
				server = new UdpVehicleServer();
				if (ConnectScreen.getAddress() != null)
					{
						addr = CrwNetworkUtils.toInetSocketAddress(ConnectScreen.getIpAddress() + ":11411");
					} else
					{
						addr = CrwNetworkUtils.toInetSocketAddress("127.0.0.1:11411");
					}
				server.setVehicleService(addr);
				twist = new Twist();
				
				
			}
		
		public void boatConnectTest()
			{

				//twist.dx(thrust.getProgress() * .010);
				//twist.drz(rudder.getProgress() * .010);
				//ConnectScreen.boat.returnServer().setVelocity(twist, null);
				ConnectScreen.boat.setVelocity(thrust.getProgress(), rudder.getProgress());
				//server.setVelocity(twist, null);

			}

		private class NetworkAsync extends AsyncTask<String, Integer, String>
			{
				long oldTime = 0;

				@Override
				protected String doInBackground(String... arg0)
					{
						while (true)
							{
								if (System.currentTimeMillis() % 100 == 0 && oldTime != System.currentTimeMillis())
									{
										
										boatConnectTest();
										oldTime = System.currentTimeMillis();
										a += 1;
										publishProgress();
									}
							}
					}

				@Override
				protected void onProgressUpdate(Integer... result)
					{
						test.setText("" + a + "\n" + serverData); // TODO
																	// Auto-generated
																	// method
																// stub
					}

			}

	}// class
