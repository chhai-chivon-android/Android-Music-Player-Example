package com.fomchenkovoutlook.artem.android_music_player_example.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import com.fomchenkovoutlook.artem.android_music_player_example.utils.PlayerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String MUSIC_FOLDER_PATH = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            .getPath() + "/";

    private final int DEFAULT_VALUE = 0;

    private MediaPlayer player;

    private PlayerUtils playerUtils;

    private int currentTime;

    private boolean isPaused = true;
    private boolean endPlaying = false;

    public static class PlayerHolder {
        static final Player PLAYER_INSTANCE = new Player();
    }

    private boolean getPlayerState() {
        return player == null;
    }

    public static Player getInstance() {
        return PlayerHolder.PLAYER_INSTANCE;
    }

    public void init() {
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                endPlaying = true;
            }
        });

        playerUtils = new PlayerUtils();
    }

    public void play(Track track)
            throws IOException {
        if (!getPlayerState() && !player.isPlaying()) {
            FileInputStream fileInputStream =
                    new FileInputStream(new File(MUSIC_FOLDER_PATH + track.getName()));

            player.reset();
            player.setDataSource(fileInputStream.getFD());
            player.prepare();
            player.start();

            isPaused = false;
            endPlaying = false;
        }
    }

    public void resume() {
        if (!getPlayerState() && !player.isPlaying() && isPaused) {
            player.seekTo(currentTime);

            isPaused = false;

            player.start();
        }
    }

    public void pause() {
        if (!getPlayerState() && player.isPlaying() && !isPaused) {
            currentTime = player.getCurrentPosition();

            isPaused = true;

            player.pause();
        }
    }

    public void stop() {
        if (!getPlayerState() && player.isPlaying()) {
            player.stop();
        }
    }

    public void toTime(int time) {
        if (!getPlayerState()) {
            player.seekTo(time);
        }
    }

    public int getTrackTimePosition() {
        if (!getPlayerState()) {
            return player.getCurrentPosition();
        }

        return DEFAULT_VALUE;
    }

    public int getTrackEndTime() {
        if (!getPlayerState()) {
            return player.getDuration();
        }

        return DEFAULT_VALUE;
    }

    public List<Track> checkDirectory() {
        List<Track> tracks = new ArrayList<>();

        if (!getPlayerState()) {
            for (File track: new File(MUSIC_FOLDER_PATH).listFiles()) {
                if (playerUtils.isMusicFile(track)) {
                    tracks.add(new Track(track.getName()));
                }
            }
        }

        return tracks;
    }

    public Bitmap getCover(Track track, Context context) {
        Bitmap cover;

        if (!getPlayerState()) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

            byte[] rawCover;

            BitmapFactory.Options options = new BitmapFactory.Options();

            mediaMetadataRetriever.setDataSource(context,
                    Uri.fromFile(new File(MUSIC_FOLDER_PATH + track.getName())));

            rawCover = mediaMetadataRetriever.getEmbeddedPicture();

            if (null != rawCover) {
                cover = BitmapFactory.decodeByteArray(rawCover, 0, rawCover.length, options);

                return cover;
            }
        }

        return null;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean endPlaying() {
        return endPlaying;
    }
}
