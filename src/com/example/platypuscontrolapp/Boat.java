package com.example.platypuscontrolapp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import android.widget.TextView;

import edu.cmu.ri.crw.PoseListener;
import edu.cmu.ri.crw.data.Twist;
import edu.cmu.ri.crw.data.UtmPose;
import edu.cmu.ri.crw.udp.UdpVehicleServer;

public class Boat
	{
		private UdpVehicleServer server = null;
		private String name;
		private InetSocketAddress ipAddress;
		private Twist tw = null;
		private PoseListener pl;
		private UtmPose pose;
		public Boat()
			{
			}

		public Boat(InetSocketAddress _ipAddress)
			{
				ipAddress = _ipAddress;
				server = new UdpVehicleServer();
				server.setVehicleService(ipAddress);
				tw = new Twist();
			}

		public void setAddress(InetSocketAddress a)
			{
				server.setVehicleService(a);
			}

		public void getPose()
			{
				pl = new PoseListener() {
				
			  public void receivedPose(UtmPose upwcs)
						{
						 UtmPose _pose = upwcs.clone();
							 {
								System.out.println(_pose.pose.getX());
							 }
						}
				};
				server.addPoseListener(pl, null);
			}
		public double getThrust()
			{
				return tw.dx()/.010;
			}
		public double getRudder()
			{
				return tw.drz()/.010;
			}

		public void setVelocity(int thrust, int rudder)
			{
				tw.dx(thrust * .010);
				tw.drz(rudder * .010);
				server.setVelocity(tw, null);
			}
		public UdpVehicleServer returnServer()
			{
				return server;
				
			}
	}
