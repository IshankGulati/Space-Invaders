package io.github.ishankgulati.spaceinvaders;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hackbook on 10/27/2015.
 */
public class SoundManager {

    public SoundPool soundPool;
    public MediaPlayer player;
    private int damageshelterID = -1;
    private int invaderexplodeID = -1;
    private int uhID = -1;
    private int ohID = -1;
    private int playerexplodeID = -1;
    private int shootID = -1;
    private int winID = -1;
    private int loseID = -1;
    private int streamId = -1;
    private ConcurrentHashMap<String, Integer> soundIds;

    SoundManager(Context context){
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        soundIds = new ConcurrentHashMap<String, Integer>();

        // load sounds
        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("damageshelter.ogg");
            damageshelterID = soundPool.load(descriptor, 0);
            soundIds.put("damageshelter", damageshelterID);

            descriptor = assetManager.openFd("invaderexplode.ogg");
            invaderexplodeID = soundPool.load(descriptor, 0);
            soundIds.put("invaderexplode", invaderexplodeID);

            descriptor = assetManager.openFd("uh.ogg");
            uhID = soundPool.load(descriptor, 0);
            soundIds.put("uh", uhID);

            descriptor = assetManager.openFd("oh.ogg");
            ohID = soundPool.load(descriptor, 0);
            soundIds.put("oh", ohID);

            descriptor = assetManager.openFd("playerexplode.ogg");
            playerexplodeID = soundPool.load(descriptor, 0);
            soundIds.put("playerexplode", playerexplodeID);

            descriptor = assetManager.openFd("shoot.ogg");
            shootID = soundPool.load(descriptor, 0);
            soundIds.put("shoot", shootID);

            descriptor = assetManager.openFd("win.wav");
            winID = soundPool.load(descriptor, 0);
            soundIds.put("win", winID);

            descriptor = assetManager.openFd("lose.wav");
            loseID = soundPool.load(descriptor, 0);
            soundIds.put("lose", loseID);


            // Adding music
            player = MediaPlayer.create(context, R.raw.soundtrack);
            player.setLooping(true);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
    }

    public void playSound(String name){
        int id = soundIds.get(name);
        streamId = soundPool.play(id, 1, 1, 0, 0, 1);
    }


    public void playMusic(){
        if(!player.isPlaying()){
            player.start();
        }
    }

    public void stopAllSounds(){
        if(player.isPlaying()) {
            player.stop();
            try {
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        soundPool.stop(streamId);
    }
}
