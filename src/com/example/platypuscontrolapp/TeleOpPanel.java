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
import edu.cmu.ri.crw.PoseListener;
import edu.cmu.ri.crw.data.Twist;
import edu.cmu.ri.crw.data.UtmPose;
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
		String random = "";
	

		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				this.setContentView(R.layout.teleoppanel);
				//initBoat();
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
				//test.setText(ConnectScreen.boat.getPose());
				//testing new boat stuff
				ConnectScreen.boat.getPose();
		

				// change this to go to the boat class
				// find a way to pass a parameter for variables so i can access x y z in a different 
				// class while having the code being ran in the boat object
				// finish method you were writing for map activity
				// fix map activity so the automated stuff only works when you select simulation
				// 
				PoseListener pl = new PoseListener() {
					
					  public void receivedPose(UtmPose upwcs)
								{
								 UtmPose _pose = upwcs.clone();
									 {
							
										 random = "" + _pose.pose.getX() + "\n" + _pose.pose.getY() + "\n" + _pose.pose.getZ();
							
									 }
								}
						};
						ConnectScreen.boat.returnServer().addPoseListener(pl, null);
					
				

				
				new NetworkAsync().execute();


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
		
		public void updateVelocity(Boat a)
			{
				ConnectScreen.boat.setVelocity(thrust.getProgress(), rudder.getProgress());
			}
		public void updateScreenVelocity()
			{
				thrust.setProgress((int)ConnectScreen.boat.getThrust());
				rudder.setProgress((int)ConnectScreen.boat.getRudder());
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
									  
									
										updateVelocity(ConnectScreen.boat);
									//	updateScreenVelocity();
										oldTime = System.currentTimeMillis();
									//	a += 1;
										
										publishProgress();
									}
							}
					}

				@Override
				protected void onProgressUpdate(Integer... result)
					{
						test.setText("" + a + random);
					}

			}

	}// class
