package com.example.platypuscontrolapp;


import java.net.InetSocketAddress;

import edu.cmu.ri.crw.PoseListener;
import edu.cmu.ri.crw.data.Twist;
import edu.cmu.ri.crw.data.UtmPose;
import edu.cmu.ri.crw.udp.UdpVehicleServer;

import org.jscience.geography.coordinates.*;

import robotutils.Pose3D;
import robotutils.Quaternion;


public class Boat
	{
		private UdpVehicleServer server = null;
		private String name;
		private InetSocketAddress ipAddress;
		private Twist tw = null;
		private PoseListener pl;
		private UtmPose pose;
		private double xValue;
		private double yValue;
		private double zValue;
		private Quaternion rotation;
		public Boat()
			{
				ipAddress = new InetSocketAddress("127.0.0.1", 11411);
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
		public InetSocketAddress getIpAddress()
			{
				return ipAddress;
			}
		public void getPose()
			{
				PoseListener pl = new PoseListener() {
					
					  public void receivedPose(UtmPose upwcs)
								{
								 UtmPose _pose = upwcs.clone();
									 {
										// random = "" + _pose.pose.getX() + "\n" + _pose.pose.getY() + "\n" + _pose.pose.getZ();
										 xValue = _pose.pose.getX();
										 yValue = _pose.pose.getY();
										 zValue = _pose.pose.getZ();
										 rotation =_pose.pose.getRotation();
										 //_pose.origin.
		
									 }
								}
						};
						ConnectScreen.boat.returnServer().addPoseListener(pl, null);
			}
		public double getThrust()
			{
				return tw.dx();
			}
		public double getRudder()
			{
				return tw.drz();
			}

		public void setVelocity(int thrust, int rudder)
			{	
				//tw.dx(thrust * .010);
				//tw.drz(rudder * .010);
				tw.dx(thrust);
				tw.drz(rudder);
				server.setVelocity(tw, null);
				
			}
		public UdpVehicleServer returnServer()
			{
				return server;	
			}
		public double getPoseX()
			{
				return xValue;
			}
		public double getPoseY()
			{
				return yValue;
			}
		public double getPoseZ()
			{
				return zValue;
			}
		public Quaternion getPoseRotation()
			{
				return rotation;
			}
		

			
	}
