package com.example.platypuscontrolapp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import edu.cmu.ri.crw.PoseListener;
import edu.cmu.ri.crw.data.Twist;
import edu.cmu.ri.crw.data.UtmPose;
import edu.cmu.ri.crw.udp.UdpVehicleServer;
import java.net.InetSocketAddress;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

		int a = 0;
		InetSocketAddress addr;
		UdpVehicleServer server = null;
		Twist twist = new Twist();
		String random = "";
		double xValue;
		double yValue;
		double zValue;
		GoogleMap map;

		
		TextView loca = null;
		Marker boat;
		Marker boat2;
		LatLng pHollowStartingPoint = new LatLng((float) 40.436871, (float) -79.948825);
		double lat;
		double lon;
		Handler handlerRudder = new Handler();
		int thrustCurrent;
		int rudderCurrent;
		double heading = Math.PI / 2.;
		
		
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
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				
				
				thrust.setProgress(0);
				rudder.setProgress(50);
				//test.setText(ConnectScreen.boat.getPose());

				ConnectScreen.boat.getPose();
				ipAddressBox.setText(ConnectScreen.boat.getIpAddress().toString());
				updateThrust();
				updateRudder();
			//	ConnectScreen.boat.getPose();
				
				PoseListener pl = new PoseListener() {
					
					  public void receivedPose(UtmPose upwcs)
								{
								 UtmPose _pose = upwcs.clone();
									 {
										 random = "" + _pose.pose.getX() + "\n" + _pose.pose.getY() + "\n" + _pose.pose.getZ();
										 xValue = _pose.pose.getX();
										 yValue = _pose.pose.getY();
										 zValue = _pose.pose.getZ();
									 }
								}
						};
						ConnectScreen.boat.returnServer().addPoseListener(pl, null);

				
				new NetworkAsync().execute();

				if (ConnectScreen.getBoatType() == false)
					{
						ipAddressBox.setText("Simulated Phone");
						simulatedBoat();
					}
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
						//test.setText("" + a + random);
					}
			}
		
		public void updateThrust()
			{
				thrust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
					{
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
							{
								// TODO Auto-generated method stub
								thrustProgress.setText(String.valueOf(progress));
								thrustCurrent = progress;
							} 	

						@Override
						public void onStartTrackingTouch(SeekBar seekBar)
							{
							}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar)
							{
							}
					});
			}

		public void updateRudder()
			{
				rudder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
					{
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
							{
								// TODO Auto-generated method stub
								rudderProgress.setText(String.valueOf(progress));
								rudderCurrent = progress;
							}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar)
							{
							}

						@Override
						public void onStopTrackingTouch(SeekBar seekBar)
							{
							}
					});
			}

		
		public void simulatedBoat()
			{
				
				boat2 = map.addMarker(new MarkerOptions()
							.anchor(.5f, .5f).flat(true)
							.rotation(270).title("Boat 1")
							.snippet("IP Address: 192.168.1.1")
							.position(pHollowStartingPoint)
							.flat(true)
						// .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
						);

				lat = pHollowStartingPoint.latitude;
				lon = pHollowStartingPoint.longitude;
				map.setMyLocationEnabled(true);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(pHollowStartingPoint, 15));
				map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

			
//				handlerRudder.post(new Runnable()
//					{
//						@Override
//						public void run()
//							{
//								heading -= (rudderCurrent - 50) * .001;
//								lat += Math.cos(heading) * (thrustCurrent - 50) * .0000001;
//								lon += Math.sin(heading) * (thrustCurrent) * .0000001;
//								boat2.setPosition(new LatLng(lat, lon));
//								loca.setText(lat + "\n" + lon);
//								boat2.setRotation((float) (heading * (180 / Math.PI)));
//								handlerRudder.postDelayed(this, 300);
//							}
//					});

			}

	}// class
