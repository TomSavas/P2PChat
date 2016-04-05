package com.savas;

import java.io.*;
import java.net.*;

public class IPChecker {
    public static String getPublicIP(){
        
        URL ipecho = null;
        try{
            ipecho = new URL("http://ipecho.net/plain");
        } catch (Exception e){
            System.out.println("Unable to establish connection with http://ipecho.net/plain... --throws: " + e);
        }
        
        BufferedReader html = null;
        try{
            html = new BufferedReader(new InputStreamReader(ipecho.openStream()));
        } catch (Exception e){
            Server.printError("Unable to get public IP... --throws: " + e);
        }
            

        String ip = "";
        try {
            ip = html.readLine();
        } catch (Exception e){
            Server.printError("Unable to read from http://ipecho.net/plain... ---throws: " + e);
        }

        try{
            html.close();
        } catch (Exception e){
            Server.printError("Unable to close input stream... ---throws: " + e);
        }


        return ip;
    }
}