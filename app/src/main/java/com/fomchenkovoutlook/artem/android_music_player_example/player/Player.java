package com.fomchenkovoutlook.artem.android_music_player_example.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.fomchenkovoutlook.artem.android_music_player_example.utils.PlayerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player {

    // Path to the music folder:
    private final String MUSIC_FOLDER_PATH = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            .getPath() + "/";

    private final int DEFAULT_VALUE = 0;

    private MediaPlayer player;
    private PlayerUtils playerUtils;

    private int currentTime;
    private boolean isPaused = true;
    private boolean endPlaying = false;

    // Singleton holder:
    public static class PlayerHolder {
        static final Player PLAYER_INSTANCE = new Player();
    }

    // Singleton state:
    public static Player getInstance() {
        return PlayerHolder.PLAYER_INSTANCE;
    }

    /**
     * Check player state
     * @return is player initialize value
     */
    private boolean isPlayerInitialize() {
        return player != null;
    }

    /**
     * Initialize player
     */
    public void initialize() {
        player = new MediaPlayer();
        player.setOnCompletionListener(mediaPlayer -> endPlaying = true);
        playerUtils = new PlayerUtils();
    }

    /**
     * Play track
     * @param track selected track
     */
    public void play(@NonNull Track track)
            throws IOException {
        if (isPlayerInitialize() && !player.isPlaying()) {
            FileInputStream fileInputStream = new FileInputStream(new File(MUSIC_FOLDER_PATH + track.getName()));

            player.reset();
            player.setDataSource(fileInputStream.getFD());
            player.prepare();
            player.start();

            isPaused = false;
            endPlaying = false;
        }
    }

    /**
     * Resume
     */
    public void resume() {
        if (isPlayerInitialize() && !player.isPlaying() && isPaused) {
            player.seekTo(currentTime);
            isPaused = false;
            player.start();
        }
    }

    /**
     * Pause
     */
    public void pause() {
        if (isPlayerInitialize() && player.isPlaying() && !isPaused) {
            currentTime = player.getCurrentPosition();
            isPaused = true;
            player.pause();
        }
    }

    /**
     * Stop
     */
    public void stop() {
        if (isPlayerInitialize() && player.isPlaying()) {
            player.stop();
        }
    }

    /**
     * Seek to needed time
     * @param time needed time
     */
    public void toTime(int time) {
        if (isPlayerInitialize()) {
            player.seekTo(time);
        }
    }

    /**
     * Get track's positions
     * @return time position
     */
    public int getTrackTimePosition() {
        if (isPlayerInitialize()) {
            return player.getCurrentPosition();
        }
        return DEFAULT_VALUE;
    }

    /**
     * Track length
     * @return length
     */
    public int getTrackEndTime() {
        if (isPlayerInitialize()) {
            return player.getDuration();
        }
        return DEFAULT_VALUE;
    }

    /**
     * Check default Music directory on device
     * @return tracks
     */
    public List<Track> checkDirectory() {
        List<Track> tracks = new ArrayList<>();
        if (isPlayerInitialize()) {
            File[] musicDirectoryList = new File(MUSIC_FOLDER_PATH).listFiles();
            if (musicDirectoryList != null) {
                for (File track : musicDirectoryList) {
                    if (playerUtils.isMusicFile(track)) {
                        tracks.add(new Track(track.getName()));
                    }
                }
            }
        }
        return tracks;
    }

    /**
     * Get cover from track
     * @param track current track
     * @param context context
     * @return cover
     */
    public Bitmap getCover(@NonNull Track track, @NonNull Context context) {
        Bitmap cover;
        if (isPlayerInitialize()) {
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

    /**
     * Check player pause state
     * @return player state value
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Check track playing is done
     * @return track state
     */
    public boolean endPlaying() {
        return endPlaying;
    }
}
