package com.fomchenkovoutlook.artem.android_music_player_example.player;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final int DEFAULT_VALUE = 0;

    private MediaPlayer player;

    private int currentTime;

    private boolean isPaused;

    public static class PlayerHolder {
        static final Player PLAYER_INSTANCE = new Player();
    }

    private boolean getPlayerState() {
        return player == null;
    }

    private void getException() {
        throw new NullPointerException("Please, init a player!");
    }

    public static Player getInstance() {
        return PlayerHolder.PLAYER_INSTANCE;
    }

    public void init() {
        player = new MediaPlayer();
    }

    public void play(Track track) {
        if (getPlayerState() && !player.isPlaying()) {
            if (isPaused) {
                player.seekTo(currentTime);
            }

            player.start();
        } else {
            getException();
        }
    }

    public void pause() {
        if (getPlayerState() && player.isPlaying()) {
            currentTime = player.getCurrentPosition();

            isPaused = true;

            player.stop();
        } else {
            getException();
        }
    }

    public void toTime(int time) {
        if (getPlayerState()) {
            player.seekTo(time);
        }
    }

    public int getTrackTimePosition() {
        if (getPlayerState()) {
            return player.getCurrentPosition();
        } else {
            getException();
        }

        return DEFAULT_VALUE;
    }

    public int getTrackEndTime() {
        if (getPlayerState()) {
            player.getDuration();
        } else {
            getException();
        }

        return DEFAULT_VALUE;
    }

    public List<Track> checkDirectory() {
        if (getPlayerState()) {

        } else {
            getException();
        }

        return new ArrayList<>();
    }

    public Bitmap getCover(Track track) {
        if (getPlayerState()) {

        } else {
            getException();
        }

        // TODO: add default image:
        return null;
    }
}
