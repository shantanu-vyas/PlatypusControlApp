package com.example.platypuscontrolapp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.cmu.ri.crw.AsyncVehicleServer;
import edu.cmu.ri.crw.FunctionObserver;
import edu.cmu.ri.crw.PoseListener;
import edu.cmu.ri.crw.VelocityListener;
import edu.cmu.ri.crw.FunctionObserver.FunctionError;
import edu.cmu.ri.crw.data.Twist;
import edu.cmu.ri.crw.data.Utm;
import edu.cmu.ri.crw.data.UtmPose;
import edu.cmu.ri.crw.udp.UdpVehicleServer;
import java.net.InetSocketAddress;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.apache.commons.math.linear.RealMatrix;
import org.jscience.geography.coordinates.*;
import org.jscience.geography.coordinates.crs.ReferenceEllipsoid;

import robotutils.Pose3D;
import robotutils.Quaternion;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;

public class TeleOpPanel extends Activity implements OnMapClickListener, SensorEventListener
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
		AsyncTask networkThread;
		TextView test = null;
		ToggleButton tiltButton = null;
		TextView log = null;
		Handler network = new Handler();

		int a = 0;
	
		
		double xValue;
		double yValue;
		double zValue;
		LatLong latlongloc;
		LatLng boatLocation;

		GoogleMap map;
		String zone;
		String rotation;

		TextView loca = null;
		Marker boat;
		Marker boat2;
		LatLng pHollowStartingPoint = new LatLng((float) 40.436871, (float) -79.948825);

		double lat = 10;
		double lon = 10;

		Handler handlerRudder = new Handler();
		int thrustCurrent;
		int rudderCurrent;
		double heading = Math.PI / 2.;
		int rudderTemp = 50;
		int thrustTemp = 0;
		double temp;
		double rot;

		float tempX = 0;
		float tempY = 0;

		SensorManager senSensorManager;
		Sensor senAccelerometer;

		private long lastUpdate = 0;
		private float last_x, last_y, last_z;
		private static final int SHAKE_THRESHOLD = 600;

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
				tiltButton = (ToggleButton) this.findViewById(R.id.tiltButton);
				log = (TextView) this.findViewById(R.id.log);
				// autonomous = (CheckBox) this.findViewById(R.id.Autonomous);

				senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
				senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

				tiltButton.setTextOff("Tilt Control Deactivated");
				tiltButton.setTextOn("Tilt Control Activated");

				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				// map.moveCamera(CameraUpdateFactory.newLatLngZoom(new
				// LatLng((float) 40.4417, (float) -80.0000), 17));
				// map.setMyLocationEnabled(true);

				if (ConnectScreen.getBoatType() == true)
					{
						boat2 = map.addMarker(new MarkerOptions().anchor(.5f, .5f).flat(true).rotation(270).title("Boat 1")
								.snippet(ConnectScreen.boat.getIpAddress().toString()).position(pHollowStartingPoint).title("Boat 1")
								.snippet("127.0.0.1 (localhost)").flat(true));
						map.moveCamera(CameraUpdateFactory.newLatLngZoom(pHollowStartingPoint, 14));
						map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
						map.setOnMapClickListener(this);
//						setOnMapClickListener(new OnMapClickListener()
//							{
//							    @Override
//							    public void onMapClick(LatLng point)
//							    	{
//							    		try
//							    			{
//							    				log.setText(String.valueOf(point.latitude) + " " + String.valueOf(point.longitude));
//							    			}
//							    		catch(Exception e)
//							    		{
//							    			log.setText(e.toString());
//							    		}
////							    		log.setText(point.toString());
////							    		map.addMarker(new MarkerOptions().anchor(.5f, .5f).flat(true).title("Boat 1").snippet("Waypoint")
////												 .position(point).title("Current Waypoint").snippet("127.0.0.1 (localhost)"));
////										
//							    	}
//				
//							});
						networkThread = new NetworkAsync().execute();
						actualBoat();

					}
				if (ConnectScreen.getBoatType() == false)
					{
						ipAddressBox.setText("Simulated Phone");
						simulatedBoat();
					}
				//addWayPointFromMap();

				thrust.setProgress(0);
				rudder.setProgress(50);

				ipAddressBox.setText(ConnectScreen.boat.getIpAddress().toString());
				// setVehicle();
				// test.setText(ConnectScreen.boat.getPose());
				// connectScreen.boat.getPose();

			}
		private void setOnMapClickListener(OnMapClickListener onMapClickListener)
			{
				// TODO Auto-generated method stub
				
			}
		
		@Override
		public void onPause()
			{
				//turns the thrust and rudder off when you pause the activity
				thrust.setProgress(0);
				rudder.setProgress(50);
			}
		@Override
		public void onResume()
			{
				networkThread.execute();
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
				boolean connected = false;
				boolean firstTime = true;

				@Override
				protected String doInBackground(String... arg0)
					{
						PoseListener pl = new PoseListener()
							{
								public void receivedPose(UtmPose upwcs)
									{
										UtmPose _pose = upwcs.clone();
											{
												xValue = _pose.pose.getX();
												yValue = _pose.pose.getY();
												zValue = _pose.pose.getZ();
												rotation = String.valueOf(Math.PI / 2 - _pose.pose.getRotation().toYaw());
												rot = Math.PI / 2 - _pose.pose.getRotation().toYaw();

												zone = String.valueOf(_pose.origin.zone);

												latlongloc = UTM.utmToLatLong(
														UTM.valueOf(_pose.origin.zone, 'T', _pose.pose.getX(), _pose.pose.getY(), SI.METER),
														ReferenceEllipsoid.WGS84);
											}
									}
							};

						ConnectScreen.boat.returnServer().addPoseListener(pl, null);

						// setVelListener();
						while (true)
							{
								if (System.currentTimeMillis() % 100 == 0 && oldTime != System.currentTimeMillis())
									{

										if (ConnectScreen.boat.isConnected() == true)
											{
												connected = true;
											} else
											{
												connected = false;
											}

										if (thrust.getProgress() != thrustTemp)
											{
												updateVelocity(ConnectScreen.boat);
											}

										if (rudder.getProgress() != rudderTemp)
											{
												updateVelocity(ConnectScreen.boat);
											}
										thrustTemp = thrust.getProgress();
										rudderTemp = rudder.getProgress();
										oldTime = System.currentTimeMillis();
										publishProgress();

									}
							}
					}

				@Override
				protected void onProgressUpdate(Integer... result)
					{
						try
							{
								a++;
								// test.setText("x: " + xValue + "\n y: " +
								// yValue + "\n zone: " + zone + "\n rotation: "
								// + rotation + "\n"
								// + latlongloc.toText() + "\n" + a);

								boat2.setPosition(new LatLng(latlongloc.latitudeValue(SI.RADIAN) * 57.2957795,
										latlongloc.longitudeValue(SI.RADIAN) * 57.2957795));
								test.setText(String.valueOf(rot * 57.2957795));
								boat2.setRotation((float) (rot * 57.2957795));
								if (firstTime == true)
									{
										try
											{
												map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
														latlongloc.latitudeValue(SI.RADIAN) * 57.2957795,
														latlongloc.longitudeValue(SI.RADIAN) * 57.2957795), 14));
												map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
												firstTime = false;
											} catch (Exception e)
											{
												firstTime = true;
											}
									}

								// boat2.setSnippet(String.valueOf(latlongloc.toText();
							} catch (Exception e)
							{
								// test.setText("x: " + xValue + "\n y: " +
								// yValue + "\n zone: " + zone + "\n rotation: "
								// + rotation + "\n"
								// + e.toString());

							}

						if (connected == true)
							{
								ipAddressBox.setBackgroundColor(Color.GREEN);
							} else
							{
								ipAddressBox.setBackgroundColor(Color.RED);
							}

						thrustProgress.setText(String.valueOf(fromProgressToRange(thrust.getProgress(), THRUST_MIN, THRUST_MAX)));
						rudderProgress.setText(String.valueOf(fromProgressToRange(rudder.getProgress(), RUDDER_MIN, RUDDER_MAX)));

					}
			}

		public void actualBoat()
			{
			}

		public void simulatedBoat()
			{
				boat2 = map.addMarker(new MarkerOptions().anchor(.5f, .5f).flat(true).rotation(270).title("Boat 1")
						.snippet("IP Address: 192.168.1.1").position(pHollowStartingPoint).title("Boat 1").snippet("127.0.0.1 (localhost)")
						.flat(true));

				lat = pHollowStartingPoint.latitude;
				lon = pHollowStartingPoint.longitude;
				map.setMyLocationEnabled(true);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(pHollowStartingPoint, 15));
				map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

				boat2.setRotation((float) (heading * (180 / Math.PI)));
				handlerRudder.post(new Runnable()
					{
						@Override
						public void run()
							{
								if (thrust.getProgress() > 0)
									{
										lat += Math.cos(heading) * (thrust.getProgress() - 50) * .0000001;
										lon += Math.sin(heading) * (thrust.getProgress()) * .0000001;
										heading -= (rudder.getProgress() - 50) * .001;
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

		public LatLng utmToLatLng(UTM a)
			{

				return null;
			}

		/* accelerometer controls */
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
			{

			}

		@Override
		public void onSensorChanged(SensorEvent sensorEvent)
			{
				Sensor mySensor = sensorEvent.sensor;
				if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
					{
						float x = sensorEvent.values[0];
						float y = sensorEvent.values[1];
						float z = sensorEvent.values[2];

						long curTime = System.currentTimeMillis();

						if (tiltButton.isChecked())
							{
								if ((curTime - lastUpdate) > 100)
									{
										long diffTime = (curTime - lastUpdate);
										lastUpdate = curTime;
										float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

										if (speed > SHAKE_THRESHOLD)
											{
											}

										last_x = x; // rudder
										last_y = y;
										last_z = z; // thrust
										test.setText("x: " + last_x + "y: " + last_y + "z: " + last_z);

										updateViaAcceleration(last_x, last_y, last_z);
									}
							}
					}
			}

		public void updateViaAcceleration(float xval, float yval, float zval)
			{
				if (Math.abs(tempX - last_x) > 2.5)
					{

						if (last_x > 2)
							{
								thrust.setProgress(thrust.getProgress() - 3);
							}
						if (last_x < 2)
							{
								thrust.setProgress(thrust.getProgress() + 3);
							}
					}
				if (Math.abs(tempY - last_y) > 1)
					{
						if (last_y > 2)
							{
								rudder.setProgress(rudder.getProgress() + 3);
							}
						if (last_y < -2)
							{
								rudder.setProgress(rudder.getProgress() - 3);
							}
					}
			}
//		public boolean onTouchEvent(MotionEvent event)
//		    {
//		    	
//		        int X = (int)event.getX();          
//		        int Y = (int)event.getY();
//		        LatLng markerloc = map.getProjection().fromScreenLocation(new Point(X,Y));
//		        //GeoPoint geoPoint = map.getProjection().fromPixels(X, Y);
//		        map.addMarker(new MarkerOptions().anchor(.5f, .5f).flat(true).title("Boat 1").snippet("Waypoint")
//						// .position(convertUtmLatLng(pose,origin))
//								.position(markerloc).title("Current Waypoint").snippet("127.0.0.1 (localhost)"));
//				return true;
//		    }
		
		@Override
		public void onMapClick(LatLng point) {
		
		  test.setText(String.valueOf(point.latitude) + " "+ String.valueOf(point.longitude));
	      map.addMarker(new MarkerOptions()
	      .anchor(.5f, .5f).flat(true)
	      .title("Waypoint")
	      .snippet("Waypoint" + point.toString())
		  .position(point));
		 }

		public void addWayPointFromMap()
			{
				// when you click you make utm pose... bewlow is fake values
				Pose3D pose = new Pose3D(1, 1, 0, 0.0, 0.0, 10);
				Utm origin = new Utm(17, true);
				//ConnectScreen.boat.addWaypoint(pose, origin);
				UtmPose[] wpPose = new UtmPose[1];
				wpPose[0] = new UtmPose(pose, origin);
				ConnectScreen.boat.returnServer().startWaypoints(wpPose, "POINT_AND_SHOOT", new FunctionObserver<Void>()
					{
						public void completed(Void v)
							{
								log.setText("completed");
							}

						public void failed(FunctionError fe)
							{
								log.setText("failed");
							}
					});				
				
	
				
				map.addMarker(new MarkerOptions().anchor(.5f, .5f).flat(true).title("Boat 1").snippet("Waypoint")
				// .position(convertUtmLatLng(pose,origin))
						.position(pHollowStartingPoint).title("Current Waypoint").snippet("127.0.0.1 (localhost)"));
			}

		public LatLng convertUtmLatLng(Pose3D _pose, Utm _origin)
			{
				LatLong temp = UTM.utmToLatLong(UTM.valueOf(_origin.zone, 'T', _pose.getX(), _pose.getY(), SI.METER), ReferenceEllipsoid.WGS84);
				return new LatLng(temp.latitudeValue(SI.RADIAN), temp.longitudeValue(SI.RADIAN));
			}
	}
// class
