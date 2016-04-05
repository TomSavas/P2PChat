package com.savas;

import javax.sound.sampled.*;
import java.io.*;

public class SoundPlayer {

    /*Whether sound is enabled or not*/
    public static boolean soundEnabled = true;
    /*Whether sound is enabled or not*/

    /*Removing spam possibility*/
    public static long lastTime = 0;
    /*Removing spam possibility*/

    public static void toggleSound(){ soundEnabled = !soundEnabled; }

    public static void notifyWithSound(){
        if(!soundEnabled) return;
            
        if((lastTime + 10000) > System.currentTimeMillis()) return;

        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("notification_sound.wav")));
            clip.start();
        } catch (Exception e){
            Server.printError("Unable to play sound... ---throws: " + e);
        }

        lastTime = System.currentTimeMillis();
    }
}