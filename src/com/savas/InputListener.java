package com.savas;

import java.io.*;
import java.util.*;

public class InputListener extends Thread{
	/*Reference to the server*/
	private Server server;
	/*Reference to the server*/

	/*Thread aliveness control lol*/
	private boolean disconnect = false;
	/*Thread aliveness control lol*/

	public InputListener(Server server){
		this.server = server;
	}

	public void setDisconnect() { this.disconnect = true; }

	public void run(){
		System.out.println("[INPUTLISTENER] Input listener started...\n");
		System.out.print(">> ");
		while(!disconnect){
			String input = System.console().readLine();

			if(input.startsWith("--connect ")){
				if((input.split(" ", 2)[1]).equals(IPChecker.getPublicIP())){
					System.out.println("You can't connect to yourself via public IP, use localhost instead.");
				} else {
					input = input.split(" ", 2)[1];
					server.addOutgoingClient(input);
				}
			} else if(input.equals("--list")){
				server.listClients();
			} else if(input.equals("--quit")){
				// server.disconnect();
				System.exit(0);
			} else if(input.equals("--toggleErrors")){
				server.toggleErrors();
				System.out.println("Error messages are " + (Server.errorsEnabled ? "enabled." : "disabled."));
			} else if(input.equals("--toggleSound")){
				SoundPlayer.toggleSound();
				System.out.println("Notification sounds are " + (SoundPlayer.soundEnabled ? "enabled." : "disabled."));
			} else if(input.equals("--help")){
				displayHelp();
			} else if(input.equals("--wtf")){
				System.out.println("\n"
									+ " ____      ____  _________  ________  \n" 
									+ "|_  _|    |_  _||  _   _  ||_   __  | \n"
									+ "  \\ \\  /\\  / /  |_/ | | \\_|  | |_ \\_| \n "
									+ "  \\ \\/  \\/ /       | |      |  _| \n "  
									+ "   \\  /\\  /       _| |_    _| |_     \n "
									+ "    \\/  \\/       |_____|  |_____|    \n");
				server.broadcast("\n"
									+ " ____      ____  _________  ________  \n" 
									+ "|_  _|    |_  _||  _   _  ||_   __  | \n"
									+ "  \\ \\  /\\  / /  |_/ | | \\_|  | |_ \\_| \n "
									+ "  \\ \\/  \\/ /       | |      |  _| \n "  
									+ "   \\  /\\  /       _| |_    _| |_     \n "
									+ "    \\/  \\/       |_____|  |_____|    \n");
			} else if(input.startsWith("--")){
				System.out.println(input + " is an invalid command. Type --help for help.");
			} else {
				server.broadcast(input);
			}
			
			System.out.print(">> ");
		}
	}

	public void displayHelp(){
		System.out.println("\nType --connect [IP address]        i.e. --connect 25.54.248.01\n"
						   + "               To connect to a server with stated IP address.\n"
						   + "               Also, it must be on the same LAN as you are.\n"
						   + "               If you are not on LAN look up Hamachi or similar programs.");
		System.out.println("\nType --list\n"
						   + "               To list all incoming/outgoing connections.");
		System.out.println("\nType --quit\n"
						   + "               To disconnect from all established connections and kill the server.");
		System.out.println("\nType --toggleErrors\n"
						   + "               To toggle exception printing. Off by default.");
		System.out.println("\nType --toggleSound\n"
						   + "               To toggle notification sound. On by default.");
		System.out.println("\nType --wtf\n"
						   + "               To express yourself in the most beautiful ASCII way.");
		System.out.println("\nType --help\n"
						   + "               To bring up this summary.\n");

		System.out.println("To connect to another PC simply type --connect [Public IP of PC you want to connect to]\n"
						 + "								     i.e. --connect 11.22.33.444 ");
		System.out.println("Note: all users must have forwarded the port they are using o ntheir routers.");
	}

}