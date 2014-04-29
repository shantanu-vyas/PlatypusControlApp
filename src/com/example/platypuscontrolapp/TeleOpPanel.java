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
import edu.cmu.ri.crw.AsyncVehicleServer;
import edu.cmu.ri.crw.PoseListener;
import edu.cmu.ri.crw.VelocityListener;
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
		int rudderTemp = 50;
		int thrustTemp = 0;
		double temp;
		public static final double THRUST_MIN = 0.0;
		public static final double THRUST_MAX = 1.0;
		public static final double RUDDER_MIN = 1.0;
		public static final double RUDDER_MAX = -1.0;

		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				this.setContentView(R.layout.teleoppanel);
				// initBoat();
				ipAddressBox = (TextView) this.findViewById(R.id.printIpAddress);
				thrust = (SeekBar) this.findViewById(R.id.thrustBar);
				rudder = (SeekBar) this.findViewById(R.id.rudderBar);
				linlay = (LinearLayout) this.findViewById(R.id.linlay);
				thrustProgress = (TextView) this.findViewById(R.id.getThrustProgress);
				rudderProgress = (TextView) this.findViewById(R.id.getRudderProgress);
				test = (TextView) this.findViewById(R.id.test12);
				// mapButton = (Button) this.findViewById(R.id.mapButton);
				autonomous = (CheckBox) this.findViewById(R.id.Autonomous);
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

				thrust.setProgress(0);
				rudder.setProgress(50);
				;
				// test.setText(ConnectScreen.boat.getPose());

				ConnectScreen.boat.getPose();
				// ipAddressBox.setText(ConnectScreen.boat.getIpAddress().toString());
				// setVehicle();

				// pose stuff get this working
				// ConnectScreen.boat.getPose();
				// test.setText(String.valueOf(ConnectScreen.boat.getPoseX()) +
				// "\n" + String.valueOf(ConnectScreen.boat.getPoseY())+"\n" +
				// String.valueOf(ConnectScreen.boat.getPoseZ()));

				new NetworkAsync().execute();

				if (ConnectScreen.getBoatType() == false)
					{
						ipAddressBox.setText("Simulated Phone");
						simulatedBoat();
					}
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
				startActivity(new Intent(this, MapTest.class));
			}

		public void updateVelocity(Boat a)
			{
				// ConnectScreen.boat.setVelocity(thrust.getProgress(),
				// rudder.getProgress());
				if (a.returnServer() != null)
					{
						Twist twist = new Twist();
						twist.dx(fromProgressToRange(thrust.getProgress(), THRUST_MIN, THRUST_MAX));
						twist.drz(fromProgressToRange(rudder.getProgress(), RUDDER_MIN, RUDDER_MAX));
						a.returnServer().setVelocity(twist, null);
					}
			}

		private class NetworkAsync extends AsyncTask<String, Integer, String>
			{
				long oldTime = 0;
				String tester = "done";

				@Override
				protected String doInBackground(String... arg0)
					{
						setVelListener();
						while (true)
							{
								if (System.currentTimeMillis() % 100 == 0 && oldTime != System.currentTimeMillis())
									{
										if (thrust.getProgress() != thrustTemp)
											{
												updateVelocity(ConnectScreen.boat);
												tester = "thrust sent";
											}
										thrustTemp = thrust.getProgress();

										if (rudder.getProgress() != rudderTemp)
											{
												updateVelocity(ConnectScreen.boat);
												tester = "rudder sent";
											}

										oldTime = System.currentTimeMillis();
										publishProgress();
										rudderTemp = rudder.getProgress();
									}
							}
					}

				@Override
				protected void onProgressUpdate(Integer... result)
					{
						thrustProgress.setText(String.valueOf(fromProgressToRange(thrust.getProgress(), THRUST_MIN, THRUST_MAX)));
						rudderProgress.setText(String.valueOf(fromProgressToRange(rudder.getProgress(), RUDDER_MIN, RUDDER_MAX)));
					}
			}

		public void simulatedBoat()
			{

				boat2 = map.addMarker(new MarkerOptions().anchor(.5f, .5f).flat(true).rotation(270).title("Boat 1")
						.snippet("IP Address: 192.168.1.1").position(pHollowStartingPoint).flat(true)
				// .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
						);

				lat = pHollowStartingPoint.latitude;
				lon = pHollowStartingPoint.longitude;
				map.setMyLocationEnabled(true);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(pHollowStartingPoint, 15));
				map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

				rudderCurrent = 50;
				boat2.setRotation((float) (heading * (180 / Math.PI)));
				handlerRudder.post(new Runnable()
					{
						@Override
						public void run()
							{
								if (thrustCurrent > 0)
									{
										lat += Math.cos(heading) * (thrustCurrent - 50) * .0000001;
										lon += Math.sin(heading) * (thrustCurrent) * .0000001;
										heading -= (rudderCurrent - 50) * .001;
										boat2.setRotation((float) (heading * (180 / Math.PI)));
									}
								boat2.setPosition(new LatLng(lat, lon));
								handlerRudder.postDelayed(this, 200);
							}
					});
			}

		public void setVelListener()
			{

				ConnectScreen.boat.returnServer().addVelocityListener(new VelocityListener()
					{
						public void receivedVelocity(Twist twist)
							{
								thrust.setProgress(fromRangeToProgress(twist.dx(), THRUST_MIN, THRUST_MAX));
								rudder.setProgress(fromRangeToProgress(twist.drz(), RUDDER_MIN, RUDDER_MAX));
							}
					}, null);

			}

		// Converts from progress bar value to linear scaling between min and
		// max
		private double fromProgressToRange(int progress, double min, double max)
			{
				return (min + (max - min) * ((double) progress) / 100.0);
			}

		// Converts from progress bar value to linear scaling between min and
		// max
		private int fromRangeToProgress(double value, double min, double max)
			{
				return (int) (100.0 * (value - min) / (max - min));
			}

	}// class
