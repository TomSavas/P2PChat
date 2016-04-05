package com.savas;

import java.io.*;
import java.util.*;

public class ServerListener extends Thread {
	
	/*Input stream that will be watched by the listener*/
	private DataInputStream inputStream;
	/*Input stream that will be watched by the listener*/

	/*Thread control*/
	private boolean disconnect = false;
	/*Thread control*/	

	/*Indentification*/
	private String serverUsername;
	/*Indentification*/

	/*Reference to server*/
	Server server;
	/*Reference to server*/

	public ServerListener(DataInputStream inputStream, String serverUsername, Server server){
		this.inputStream = inputStream;
		this.serverUsername = serverUsername;
		this.server = server;
	}

	public void setDisconnect() { this.disconnect = disconnect; }

	public void run(){
		String dataFromServer;
		
		while(!disconnect){
			try {
				//Read data from server and print it out
				dataFromServer = inputStream.readUTF();

				SoundPlayer.notifyWithSound();

				if(dataFromServer.contains("--quit")){
					server.disconnectClient((dataFromServer.split(" ")[1]).split(":")[0]);
				} else {
					System.out.println(dataFromServer);
				}

				
				System.out.print(">> ");
			} catch (Exception e) {
				if(Server.errorsEnabled){
					System.out.println("[SERVERLISTENER (" + this.serverUsername + ")] Unable to read data from server...");
				}

				//Wait for 5 sec 
				try {
					this.sleep(5000);
				} catch (Exception ee){
					//...
				}

				if(Server.errorsEnabled){
					System.out.println("[SERVERLISTENER (" + this.serverUsername + ")] Retrying to read from server...");
				}

				//Try to read from server again
				try {
					dataFromServer = inputStream.readUTF();
				} catch (Exception ee){
					if(Server.errorsEnabled){
						System.out.println("[SERVERLISTENER (" + this.serverUsername + ")] Connection closed...");
					}
					
					//If failed again, disconnect this thread
					disconnect = true;
				}
			}
		}

	}
}