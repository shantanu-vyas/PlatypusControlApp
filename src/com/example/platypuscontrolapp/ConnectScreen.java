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
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;




 public class ConnectScreen extends Activity implements OnClickListener{
 
	 public EditText ipAddress = null;
	 public EditText phoneIDNumber = null;
	 public EditText color = null;
	 public RadioButton actualBoat = null;
	 public RadioButton simulation = null;
	 public Button submitButton = null;
	 
	 public static String textIpAddress = "";
	 public static String phoneID = "";
	 public static boolean simul = false;
	 public static boolean actual = false;
	 public static boolean validIP;
	 
	 
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		 this.setContentView(R.layout.connectscreen);
		 ipAddress = (EditText)this.findViewById(R.id.ipAddress1);
		 phoneIDNumber = (EditText)this.findViewById(R.id.phoneIDNumber);
		 color = (EditText)this.findViewById(R.id.colorBox);
		 actualBoat = (RadioButton)this.findViewById(R.id.actualBoatRadio);
		 simulation = (RadioButton)this.findViewById(R.id.simulationRadio);
		 submitButton = (Button)this.findViewById(R.id.submit);
		 submitButton.setOnClickListener(this);
		 		
 
		 
//		 phoneIDNumber.setOnClickListener(new OnKeyListener(){
		 //			 @Override
		 //	 public boolean onKey(View v, int keyCode, KeyEvent event)
		 //	 {
		 //		 if (event.getAction() == KeyEvent.ACTION_DOWN)
		 //		 {
		 //			 
		 //			 if(keyCode == KeyEvent.KEYCODE_ENTER)
		 //			 {
		 //				 if(phoneIDNumber.getText().toString().charAt(phoneIDNumber.getText().length()-1) == '\n')
		 //				 {
		 //					 phoneIDNumber.setText(phoneIDNumber.getText().toString().substring(0,phoneIDNumber.getText().length()-1));
		 //				 }
		 //					 return true; 
		 //			 }
		 //		 }
		 //		 return false;
		 //	 }

		 //@Override
			//		public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
		 //		return false;
		 //	}
		 // });
		 
	 } 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 // Inflate the menu; this adds items to the action bar if it is present.
	   //  getMenuInflater().inflate(R.menu.main, menu);
		 return true;
	 }
	 
	 public void onClick(View arg0) {
		 // TODO Auto-generated method stub
		 //if (textIpAddress != "")
		 if (actualBoat.isChecked())
		 {actual = true; simul = false;}
		 if (simulation.isChecked())
		 {simul = true; actual = false;}
		 
		 textIpAddress = ipAddress.getText().toString();
		 phoneID = phoneIDNumber.getText().toString();
		// startActivity(new Intent(this,MapTest.class));
		 startActivity(new Intent(this,TeleOpPanel.class));
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
	            String sentence = inFromUser.readLine();
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



