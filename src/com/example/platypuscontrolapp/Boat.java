package com.example.platypuscontrolapp;

public class Boat {

	private String name;
	private int ipAddress;
	//coords for location
	private int boatNumber;
	private String color;
	
	public Boat(String _name)
	{
		name = _name;
	}
	
	public Boat(String _name, int _ipAddress)
	{
		name = _name;
		ipAddress = _ipAddress;
	}
	
	public Boat(String _name, int _ipAddress, int _boatNumber)
	{
		name = _name;
		ipAddress = _ipAddress;
		boatNumber = _boatNumber;
	}
	
}
