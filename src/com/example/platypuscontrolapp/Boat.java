package com.example.platypuscontrolapp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import edu.cmu.ri.crw.data.Twist;
import edu.cmu.ri.crw.udp.UdpVehicleServer;

public class Boat
	{
		private UdpVehicleServer server = null;
		private String name;
		private InetSocketAddress ipAddress;
		private Twist tw = null;

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
				
			}
		public double getThrust()
			{
				return tw.dx();
			}
		public double getVelocity()
			{
				return tw.drz();
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
