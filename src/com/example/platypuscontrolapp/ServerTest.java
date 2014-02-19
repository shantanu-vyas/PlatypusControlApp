package com.example.platypuscontrolapp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;



public class ServerTest extends Activity{
	TextView testIP = null;
	protected void onCreate(Bundle savedInstanceState) 
	{
		testIP = (TextView)this.findViewById(R.id.iptest);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.servertest);
        testIP.setText("test123123");
        
        
	}
	 public void serverTest() throws Exception 
	 {
	        try {
	            String serverHostname = new String ("127.0.0.1");
	            BufferedReader inFromUser =
	                new BufferedReader(new InputStreamReader(System.in));

	            DatagramSocket clientSocket = new DatagramSocket();

	            InetAddress IPAddress = InetAddress.getByName(serverHostname);
	            System.out.println ("Attemping to connect to " + IPAddress +
	                                ") via UDP port 9876");

	            byte[] sendData = new byte[1024];
	            byte[] receiveData = new byte[1024];

	            System.out.print("Enter Message: ");
	            //String sentence = inFromUser.readLine();
	            String sentence = "shantanu";
	            sendData = sentence.getBytes();

	            System.out.println ("Sending data to " + sendData.length +
	                                " bytes to server.");
	            DatagramPacket sendPacket =
	                new DatagramPacket(sendData, sendData.length, IPAddress, 9876);

	            clientSocket.send(sendPacket);

	            DatagramPacket receivePacket =
	                new DatagramPacket(receiveData, receiveData.length);

	            System.out.println ("Waiting for return packet");
	            clientSocket.setSoTimeout(10000);

	            try {
	                clientSocket.receive(receivePacket);
	                String modifiedSentence =
	                    new String(receivePacket.getData());

	                InetAddress returnIPAddress = receivePacket.getAddress();

	                int port = receivePacket.getPort();

	                System.out.println ("From server at: " + returnIPAddress +
	                                    ":" + port);
	                System.out.println("Message: " + modifiedSentence);

	            }
	            catch (SocketTimeoutException ste)
	                {
	                    System.out.println ("Timeout Occurred: Packet assumed lost");
	                }

	            clientSocket.close();
	        }
	        catch (UnknownHostException ex) {
	            System.err.println(ex);
	        }
	        catch (IOException ex) {
	            System.err.println(ex);
	        }
	    }
	 
		 
}
