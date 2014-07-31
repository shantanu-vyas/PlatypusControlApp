package com.example.platypuscontrolapp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.cmu.ri.crw.FunctionObserver;
import edu.cmu.ri.crw.PoseListener;
import edu.cmu.ri.crw.VehicleServer.WaypointState;
import edu.cmu.ri.crw.VelocityListener;
import edu.cmu.ri.crw.WaypointListener;
import edu.cmu.ri.crw.data.Twist;
import edu.cmu.ri.crw.data.Utm;
import edu.cmu.ri.crw.data.UtmPose;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import org.jscience.geography.coordinates.*;
import org.jscience.geography.coordinates.crs.ReferenceEllipsoid;
import robotutils.Pose3D;
import robotutils.Quaternion;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

//import com.google.android.maps.GeoPoint;

public class TeleOpPanel extends Activity implements SensorEventListener {

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
	ToggleButton waypointButton = null;
	Button deleteWaypoint = null;
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
	LatLng pHollowStartingPoint = new LatLng((float) 40.436871,
			(float) -79.948825);

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
	String boatwaypoint;

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

	List<LatLng> waypointList = new ArrayList();
	List<Marker> markerList = new ArrayList();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.teleoppanel);
		// initBoat();
		ipAddressBox = (TextView) this.findViewById(R.id.printIpAddress);
		thrust = (SeekBar) this.findViewById(R.id.thrustBar);
		rudder = (SeekBar) this.findViewById(R.id.rudderBar);
		linlay = (LinearLayout) this.findViewById(R.id.linlay);
		thrustProgress = (TextView) this.findViewById(R.id.getThrustProgress);
		rudderProgress = (TextView) this.findViewById(R.id.getRudderProgress);
		// test = (TextView) this.findViewById(R.id.test12);
		tiltButton = (ToggleButton) this.findViewById(R.id.tiltButton);
		waypointButton = (ToggleButton) this.findViewById(R.id.waypointButton);
		deleteWaypoint = (Button) this.findViewById(R.id.waypointDeleteButton);
		log = (TextView) this.findViewById(R.id.log);
		// autonomous = (CheckBox) this.findViewById(R.id.Autonomous);

		senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		senAccelerometer = senSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		senSensorManager.registerListener(this, senAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		tiltButton.setTextOff("Tilt Control Deactivated");
		tiltButton.setTextOn("Tilt Control Activated");

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(new
		// LatLng((float) 40.4417, (float) -80.0000), 17));
		// map.setMyLocationEnabled(true);

		if (ConnectScreen.getBoatType() == true) {
			boat2 = map.addMarker(new MarkerOptions()
					.anchor(.5f, .5f)
					.flat(true)
					.rotation(270)
					.title("Boat 1")
					.snippet(ConnectScreen.boat.getIpAddress().toString())
					.position(pHollowStartingPoint)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.airboat))
					);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(
					pHollowStartingPoint, 14));
			map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

			map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				@Override
				public void onMapClick(LatLng point) {
					// TODO Auto-generated method stub
					if (waypointButton.isChecked()) {
						waypointList.add(point);
						// UtmPose temp = convertLatLngUtm(point);
						// ConnectScreen.boat.addWaypoint(temp.pose,temp.origin);

						Marker tempMarker = map.addMarker(new MarkerOptions()
								.position(point));
						markerList.add(tempMarker);
						// map.addMarker(new MarkerOptions().position(point));

					}
				}
			});

			deleteWaypoint.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// ConnectScreen.boat.cancelWaypoint();
					for (Marker i : markerList) {
						i.remove();
					}
					waypointList.clear();
					// Perform action on click
				}
			});

			networkThread = new NetworkAsync().execute();
			actualBoat();

		}
		if (ConnectScreen.getBoatType() == false) {
			ipAddressBox.setText("Simulated Phone");
			simulatedBoat();
		}
		// addWayPointFromMap();

		thrust.setProgress(0);
		rudder.setProgress(50);

		ipAddressBox.setText(ConnectScreen.boat.getIpAddress().toString());
		// setVehicle();
		// test.setText(ConnectScreen.boat.getPose());
		// connectScreen.boat.getPose();

	}

	@Override
	public void onPause() {
		super.onPause();
		// turns the thrust and rudder off when you pause the activity
		thrust.setProgress(0);
		rudder.setProgress(50);
	}

	// @Override
	// public void onResume()
	// {
	// super.onResume();
	// networkThread.execute();
	// }
	//
	public static boolean validIP(String ip) {
		if (ip == null || ip == "")
			return false;
		ip = ip.trim();
		if ((ip.length() < 6) & (ip.length() > 15))
			return false;

		try {
			Pattern pattern = Pattern
					.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
			Matcher matcher = pattern.matcher(ip);
			return matcher.matches();
		} catch (PatternSyntaxException ex) {
			return false;
		}
	}

	public void updateVelocity(Boat a) {
		// ConnectScreen.boat.setVelocity(thrust.getProgress(),
		// rudder.getProgress());
		if (a.returnServer() != null) {
			Twist twist = new Twist();
			twist.dx(fromProgressToRange(thrust.getProgress(), THRUST_MIN,
					THRUST_MAX));
			twist.drz(fromProgressToRange(rudder.getProgress(), RUDDER_MIN,
					RUDDER_MAX));
			a.returnServer().setVelocity(twist, null);
		}
	}

	private class NetworkAsync extends AsyncTask<String, Integer, String> {
		long oldTime = 0;
		String tester = "done";
		boolean connected = false;
		boolean firstTime = true;

		@Override
		protected String doInBackground(String... arg0) {
			PoseListener pl = new PoseListener() {
				public void receivedPose(UtmPose upwcs) {
					UtmPose _pose = upwcs.clone();
					{
						xValue = _pose.pose.getX();
						yValue = _pose.pose.getY();
						zValue = _pose.pose.getZ();
						rotation = String.valueOf(Math.PI / 2
								- _pose.pose.getRotation().toYaw());
						rot = Math.PI / 2 - _pose.pose.getRotation().toYaw();

						zone = String.valueOf(_pose.origin.zone);

						latlongloc = UTM.utmToLatLong(UTM.valueOf(
								_pose.origin.zone, 'T', _pose.pose.getX(),
								_pose.pose.getY(), SI.METER),
								ReferenceEllipsoid.WGS84);
						
					}
				}
			};
			// waypoint checker
			ConnectScreen.boat.returnServer().addWaypointListener(new WaypointListener() {
	            public void waypointUpdate(WaypointState ws) {
	            	boatwaypoint = ws.toString();
	                        }
	                    }, null);
			
			if (waypointList.size() > 0) {
				

				UtmPose tempUtm = convertLatLngUtm(waypointList.get(0));
				ConnectScreen.boat.addWaypoint(tempUtm.pose, tempUtm.origin);
				waypointList.remove(0);
			}
			// }

			ConnectScreen.boat.returnServer().addPoseListener(pl, null);

			// setVelListener();
			while (true) {
				if (System.currentTimeMillis() % 100 == 0
						&& oldTime != System.currentTimeMillis()) {

					if (ConnectScreen.boat.isConnected() == true) {
						connected = true;
					} else {
						connected = false;
					}

					if (thrust.getProgress() != thrustTemp) {
						updateVelocity(ConnectScreen.boat);
					}

					if (rudder.getProgress() != rudderTemp) {
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
		protected void onProgressUpdate(Integer... result) {
			try {
				log.setText(String.valueOf(waypointList.get(0))+ "\n" + boatwaypoint +"\n Achieved Waypoint: "
						+ (String.valueOf(ConnectScreen.boat
								.getCurrentWaypointStatus())));
				// log.setText(String.valueOf(ConnectScreen.boat.getCurrentWaypointStatus())
				// + "\n marker: " + waypointList.get(0).latitude + " " +
				// waypointList.get(1).longitude + "\n actual"+
				// latlongloc.toText());
				// log.setText(waypointList.toString());
				a++;
				// test.setText("x: " + xValue + "\n y: " +
				// yValue + "\n zone: " + zone + "\n rotation: "
				// + rotation + "\n"
				// + latlongloc.toText() + "\n" + a);

				boat2.setPosition(new LatLng(latlongloc
						.latitudeValue(SI.RADIAN) * 57.2957795, latlongloc
						.longitudeValue(SI.RADIAN) * 57.2957795));
				// test.setText(String.valueOf(rot * 57.2957795));
				boat2.setRotation((float) (rot * 57.2957795));
				if (firstTime == true) {
					try {
						map.moveCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(
										latlongloc.latitudeValue(SI.RADIAN) * 57.2957795,
										latlongloc.longitudeValue(SI.RADIAN) * 57.2957795),
								14));
						map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
						firstTime = false;
					} catch (Exception e) {
						firstTime = true;
					}
				}

				// boat2.setSnippet(String.valueOf(latlongloc.toText();
			} catch (Exception e) {
				// test.setText("x: " + xValue + "\n y: " +
				// yValue + "\n zone: " + zone + "\n rotation: "
				// + rotation + "\n"
				// + e.toString());

			}

			if (connected == true) {
				ipAddressBox.setBackgroundColor(Color.GREEN);
			} else {
				ipAddressBox.setBackgroundColor(Color.RED);
			}

			thrustProgress.setText(String.valueOf(fromProgressToRange(
					thrust.getProgress(), THRUST_MIN, THRUST_MAX)));
			rudderProgress.setText(String.valueOf(fromProgressToRange(
					rudder.getProgress(), RUDDER_MIN, RUDDER_MAX)));

		}
	}

	public void actualBoat() {
	}

	public void simulatedBoat() {
		boat2 = map.addMarker(new MarkerOptions().anchor(.5f, .5f)
				.rotation(270).title("Boat 1")
				.snippet("IP Address: 192.168.1.1")
				.position(pHollowStartingPoint).title("Boat 1")
				.snippet("127.0.0.1 (localhost)")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.airboat))
				.flat(true));

		lat = pHollowStartingPoint.latitude;
		lon = pHollowStartingPoint.longitude;
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(pHollowStartingPoint,
				15));
		map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

		boat2.setRotation((float) (heading * (180 / Math.PI)));
		handlerRudder.post(new Runnable() {
			@Override
			public void run() {
				if (thrust.getProgress() > 0) {
					lat += Math.cos(heading) * (thrust.getProgress() - 50)
							* .0000001;
					lon += Math.sin(heading) * (thrust.getProgress())
							* .0000001;
					heading -= (rudder.getProgress() - 50) * .001;
					boat2.setRotation((float) (heading * (180 / Math.PI)));
				}
				boat2.setPosition(new LatLng(lat, lon));
				handlerRudder.postDelayed(this, 200);
			}
		});
	}

	public void setVelListener() {
		ConnectScreen.boat.returnServer().addVelocityListener(
				new VelocityListener() {
					public void receivedVelocity(Twist twist) {
						thrust.setProgress(fromRangeToProgress(twist.dx(),
								THRUST_MIN, THRUST_MAX));
						rudder.setProgress(fromRangeToProgress(twist.drz(),
								RUDDER_MIN, RUDDER_MAX));
					}
				}, null);

	}

	// Converts from progress bar value to linear scaling between min and
	// max
	private double fromProgressToRange(int progress, double min, double max) {
		return (min + (max - min) * ((double) progress) / 100.0);
	}

	// Converts from progress bar value to linear scaling between min and
	// max
	private int fromRangeToProgress(double value, double min, double max) {
		return (int) (100.0 * (value - min) / (max - min));
	}

	/* accelerometer controls */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;
		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = sensorEvent.values[0];
			float y = sensorEvent.values[1];
			float z = sensorEvent.values[2];

			long curTime = System.currentTimeMillis();

			if (tiltButton.isChecked()) {
				if ((curTime - lastUpdate) > 100) {
					long diffTime = (curTime - lastUpdate);
					lastUpdate = curTime;
					float speed = Math
							.abs(x + y + z - last_x - last_y - last_z)
							/ diffTime * 10000;

					if (speed > SHAKE_THRESHOLD) {
					}

					last_x = x; // rudder
					last_y = y;
					last_z = z; // thrust
					// test.setText("x: " + last_x + "y: " + last_y + "z: "
					// + last_z);

					updateViaAcceleration(last_x, last_y, last_z);
				}
			}
		}
	}

	public void updateViaAcceleration(float xval, float yval, float zval) {
		if (Math.abs(tempX - last_x) > 2.5) {

			if (last_x > 2) {
				thrust.setProgress(thrust.getProgress() - 3);
			}
			if (last_x < 2) {
				thrust.setProgress(thrust.getProgress() + 3);
			}
		}
		if (Math.abs(tempY - last_y) > 1) {
			if (last_y > 2) {
				rudder.setProgress(rudder.getProgress() + 3);
			}
			if (last_y < -2) {
				rudder.setProgress(rudder.getProgress() - 3);
			}
		}
	}

	public void addWayPointFromMap() {
		// when you click you make utm pose... below is fake values
		Pose3D pose = new Pose3D(1, 1, 0, 0.0, 0.0, 10);
		Utm origin = new Utm(17, true);
		// ConnectScreen.boat.addWaypoint(pose, origin);
		UtmPose[] wpPose = new UtmPose[1];
		wpPose[0] = new UtmPose(pose, origin);
		ConnectScreen.boat.returnServer().startWaypoints(wpPose,
				"POINT_AND_SHOOT", new FunctionObserver<Void>() {
					public void completed(Void v) {
						log.setText("completed");
					}

					public void failed(FunctionError fe) {
						log.setText("failed");
					}
				});

		map.addMarker(new MarkerOptions().anchor(.5f, .5f).flat(true)
				.title("Boat 1").snippet("Waypoint")
				// .position(convertUtmLatLng(pose,origin))
				.position(pHollowStartingPoint).title("Current Waypoint")
				.snippet("127.0.0.1 (localhost)"));
	}

	public LatLng convertUtmLatLng(Pose3D _pose, Utm _origin) {
		LatLong temp = UTM
				.utmToLatLong(
						UTM.valueOf(_origin.zone, 'T', _pose.getX(),
								_pose.getY(), SI.METER),
						ReferenceEllipsoid.WGS84);
		return new LatLng(temp.latitudeValue(SI.RADIAN),
				temp.longitudeValue(SI.RADIAN));
	}

	public UtmPose convertLatLngUtm(LatLng point) {

		UTM utmLoc = UTM.latLongToUtm(LatLong.valueOf(point.latitude,
				point.longitude, NonSI.DEGREE_ANGLE), ReferenceEllipsoid.WGS84);

		// Convert to UTM data structure
		Pose3D pose = new Pose3D(utmLoc.eastingValue(SI.METER), utmLoc.northingValue(SI.METER), 0.0, 0, 0, 0);
		Utm origin = new Utm(utmLoc.longitudeZone(), utmLoc.latitudeZone() > 'O');
		UtmPose utm = new UtmPose(pose, origin);
		return utm;
	}

}
// class