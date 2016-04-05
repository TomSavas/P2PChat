package com.savas;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread{
	
	/*Connection variables*/
	private ServerSocket serverSocket;
	private int port;
	/*Connection variables*/

	/*Indentification variables*/
	private String serverUsername;
	/*Indentification variables*/

	/*Connection control*/
	private boolean disconnect = false;
	/*Connection control*/

	/*Clients*/
	private ArrayList<Client> outgoingClients;
	private ArrayList<Client> incomingClients;
	/*Clients*/

	/*Input control and its variables*/
	private InputListener inputListner;
	private String input;
	/*Input control and its variables*/
 
 	/*Exception printing control*/
	public static boolean errorsEnabled = false;
 	/*Exception printing control*/

	public Server(int port, String serverUsername){
		this.port = port;
		this.serverUsername = serverUsername;
		outgoingClients = new ArrayList<Client>();
		incomingClients = new ArrayList<Client>();
	}

	public String getUsername(){ return this.serverUsername; }

	public void setDisconnect(){ this.disconnect = true; }

	public void toggleErrors(){ errorsEnabled = !errorsEnabled; }



	public void run(){
		//Try to create a new server socket @port
		try {
			serverSocket = new ServerSocket(this.port);
			System.out.println("[SERVER] Server started @" + IPChecker.getPublicIP() + ":" + port + "    <---- [your public IP]:[port]");
		} catch (Exception e) {
			printError("[SERVER] Unable to open serverSocket on " + this.port + " port");
			//If failed to start up the server socket, kill the application
			System.exit(0);
		}
		
		//Start up the inputListener
		inputListner = new InputListener(this);
		inputListner.start();

		while(!disconnect){
			//Create an empty socket
			Socket socket = null;

			//Watch if anyone is trying to connect to the serverSocket
			try {
				socket = serverSocket.accept();
			} catch (Exception e){
				printError("[SERVER] Unable to accept server socket... What?");
				continue;
			}

			//If connection was established, create a new Client instance and add it to the array
			try{
				//Read the client name
				DataInputStream tmp_input = new DataInputStream(socket.getInputStream()); 
				String name = tmp_input.readUTF();
				System.out.println("[SERVER] " + name + " connected.");
				System.out.print(">> ");

				//Send this name to the client
				DataOutputStream tmp_output = new DataOutputStream(socket.getOutputStream()); 
				tmp_output.writeUTF(serverUsername);
				
				addIncomingClient(socket, name);
			} catch (Exception e){
				printError("[SERVER] Unable to open O stream or read from it... ---throws: " + e);
			}

		}
	}

	public void disconnect(){
		System.out.println("Exitting...");

		broadcast("--quit");

		this.setDisconnect();

		for(Client client : incomingClients)
			client.disconnect();
		for(Client client : outgoingClients)
			client.disconnect();

		inputListner.setDisconnect();

		Runtime.getRuntime().halt(0);
	}

	public void disconnectServer(String serverUsername, String serverName){
		System.out.println("[SERVER] Disconnecting " + serverUsername + "...");

		for(Client client : incomingClients){
			if(client.getServerUsername().equals(serverUsername) && client.getServerName().equals(serverName)){
				client.disconnect();
				incomingClients.remove(incomingClients.indexOf(client));
				break;
			}
		}

		for(Client client : outgoingClients){
			if(client.getServerUsername().equals(serverUsername) && client.getServerName().equals(serverName)){
				client.disconnect();
				outgoingClients.remove(outgoingClients.indexOf(client));
				break;
			}
		}
	}

	public void disconnectClient(String serverUsername){
		System.out.println("[SERVER] Disconnecting " + serverUsername + "...");

		for(Client client : incomingClients){
			if(client.getServerUsername().equals(serverUsername)){
				client.disconnect();
				incomingClients.remove(incomingClients.indexOf(client));
				break;
			}
		}

		for(Client client : outgoingClients){
			if(client.getServerUsername().equals(serverUsername)){
				client.disconnect();
				outgoingClients.remove(outgoingClients.indexOf(client));
				break;
			}
		}
	}

	public void broadcast(String message){
		String date = (new Date()).toString().split(" ")[3];

		for(Client client : incomingClients)
			client.sendMessage("[" + date + "] " + serverUsername + ": " + message);

		for(Client client : outgoingClients)
			client.sendMessage("[" + date + "] " + serverUsername + ": " + message);
	}

	public void addIncomingClient(Socket socket, String name){
		Client newClient = new Client(socket, name, this);
		incomingClients.add(newClient);
	}

	public void addOutgoingClient(String serverName){
		System.out.println("[SERVER] Trying to connect to server@" + serverName + ":" + port);
		
		Client newClient = new Client(serverName, port, serverUsername, this);
		outgoingClients.add(newClient);

		if(!newClient.isAlive()) disconnectServer(serverUsername, serverName);
	}

	public void listClients(){
		System.out.println("[SERVER]");
		System.out.println("Incoming client list:");
		
		if(incomingClients.size() > 0){
			for(Client client : this.incomingClients)
				System.out.println(client.getServerUsername() + " connected from "
					+ client.getSocket().getRemoteSocketAddress().toString().split("/", 2)[1].split(":", 2)[0] + ":" + port);
		}

		System.out.println("\nOutgoing client list:");

		if(outgoingClients.size() > 0){
			for(Client client : this.outgoingClients)
				System.out.println("[YOU] connected to " + client.getClientUsername() + "@" 
					+ client.getSocket().getRemoteSocketAddress().toString().split("/", 2)[1].split(":", 2)[0] + ":" + port);
		}

		System.out.println("");
	}

	public static void printError(String msg){
		if(Server.errorsEnabled){
			System.out.println(msg);
		}
	}
}