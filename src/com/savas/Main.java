package com.savas;

import java.lang.Runtime.*;

public class Main{

	public static void main(String[] args){
		if(args.length != 2){
			System.out.println("Syntax is: java com.savas.Server [port] [username]");
			System.exit(0);	
		}             

		System.out.println("**P2P chat**");
		System.out.println("Type --help for help...\n");

		Server server = new Server(Integer.parseInt(args[0]), args[1]);
		server.start();

		//Executes disconnecting method if application was terminated 
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run(){
				server.disconnect();
				Runtime.getRuntime().halt(0);
			}
		}));
	}
}