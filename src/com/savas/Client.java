package com.savas;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client{
	/*
		All non-set/get methods are bool in order
		to ease the modification/debugging
	*/

	/*Connection variables*/
	private String serverName;
	private Socket socket;
	private int port;
	/*Connection variables*/
	
	/*Indentification variables*/
	private String serverUsername;
	private String clientUsername;
	/*Indentification variables*/

	/*I/O variables*/
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	/*I/O variables*/

	/*ServerListener thread*/
	private ServerListener serverListener;
	/*ServerListener thread*/

	/*Aliveness*/
	private boolean alive = false;
	/*Aliveness*/ 

	public Client(String serverName, int port, String serverUsername, Server server){
		this.serverName = serverName;
		this.port = port;
		this.serverUsername = serverUsername;

		//If connection was successful start the listener
		if(this.connect(server)){
			alive = true;
			System.out.println("[CLIENT (" + serverUsername +")] Succesfully connected to " + serverName + ":" + port + " as " + serverUsername + "\n");	
		} else {
			System.out.println("[CLIENT (" + serverUsername +")] Unable to connect to the server @" + serverName + ":" + port);
		}
	}

	public Client(Socket socket, String serverUsername, Server server){
		this.socket = socket;
		this.port = socket.getPort();
		this.serverUsername = serverUsername;

		//Try to create I/O streams from the socket
		try {
			inputStream  = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
		}
		catch (Exception e) {
			Server.printError("[CLIENT (" + serverUsername +")] Unable to create I/O streams " + " ---throws:" + e);
		}

		//If connection was successful start the listener
		serverListener = new ServerListener(this.inputStream, this.serverUsername, server);
		serverListener.start();	
	}

	public String getServerUsername() { return this.serverUsername; }
	public String getClientUsername() { return this.clientUsername; }
	public String getServerName() { return this.serverName; }
	public DataInputStream getInputStream() { return this.inputStream; }
	public Socket getSocket() { return this.socket; }
	public boolean isAlive() { return this.alive; }


	public boolean connect(Server server){
		//Try to open up the socket @serverName:port

		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(serverName, port), 5000); 
		} catch (Exception e){
			Server.printError("[CLIENT (" + serverUsername +")] Unable to open socket @" + serverName + ":" + port + " ---throws: " + e);
			return false; //Failed to connect at some point
		}

		//Try to create I/O streams from the socket
		try {
			inputStream  = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
		}
		catch (Exception e) {
			Server.printError("[CLIENT (" + serverUsername +")] Unable to create I/O streams " + " ---throws:" + e);
			return false; //Failed to connect at some point
		}

		//Output the username of the client to inform the server of who has connected
		//Read the name of PC this is connecting to
		try {
			outputStream.writeUTF(serverUsername);
			
			clientUsername = inputStream.readUTF();
		} catch (Exception e){
			Server.printError("[CLIENT (" + serverUsername +")] Unable to send username @" + serverName + ":" + port + " ---throws:" + e);
			return false; //Failed to connect at some point	
		}
		serverListener = new ServerListener(this.inputStream, this.serverUsername, server);
		serverListener.start();

		return true; //Connected without a fail
	}

	public void disconnect(){
		try {
			// outputStream.writeUTF("--quit");
			inputStream.close();
			outputStream.close();
		} catch (Exception e){
			Server.printError("[CLIENT (" + serverUsername +")] Unable to close socket or I/O streams..." + " ---throws:" + e);
		}

		//Kill the listener thread
		if(serverListener != null)
			serverListener.setDisconnect();

		try {
			socket.close();
		} catch(Exception e){
			//...
		}
	}

	public void sendMessage(String message){		
		//Output the message to the socket
		try {
			outputStream.writeUTF(message);
		} catch (Exception e){
			Server.printError("[CLIENT (" + serverUsername +")] Unable to send message via O stream @" + serverName + ":" + port + " ---throws:" + e
								+ "\nMessage: " + message);
		}
	}
}