package com.fomchenkovoutlook.artem.android_music_player_example.utils;

import android.annotation.SuppressLint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.fomchenkovoutlook.artem.android_music_player_example.utils.ExtensionsInterface.*;

public class PlayerUtils {

    private List<String> musicExtensions = new ArrayList<>();

    public PlayerUtils() {
        musicExtensions.add(MUSIC_EXTENSION_3GP);
        musicExtensions.add(MUSIC_EXTENSION_MP4);
        musicExtensions.add(MUSIC_EXTENSION_M4A);
        musicExtensions.add(MUSIC_EXTENSION_AAC);
        musicExtensions.add(MUSIC_EXTENSION_TS);
        musicExtensions.add(MUSIC_EXTENSION_FLAC);
        musicExtensions.add(MUSIC_EXTENSION_XMF);
        musicExtensions.add(MUSIC_EXTENSION_MXMF);
        musicExtensions.add(MUSIC_EXTENSION_RTTTL);
        musicExtensions.add(MUSIC_EXTENSION_RTX);
        musicExtensions.add(MUSIC_EXTENSION_OTA);
        musicExtensions.add(MUSIC_EXTENSION_IMY);
        musicExtensions.add(MUSIC_EXTENSION_MP3);
        musicExtensions.add(MUSIC_EXTENSION_MKV);
        musicExtensions.add(MUSIC_EXTENSION_WAV);
        musicExtensions.add(MUSIC_EXTENSION_OGG);
    }

    public boolean isMusicFile(File file) {
        for (String extension: musicExtensions) {
            if (file.getAbsolutePath().endsWith(extension)) {
                return true;
            }
        }

        return false;
    }

    @SuppressLint("DefaultLocale")
    public String toMinutes(int trackTime) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(trackTime),
                TimeUnit.MILLISECONDS.toSeconds(trackTime) % TimeUnit.MINUTES.toSeconds(1));
    }

    public int toSeconds(int trackTime) {
        return (int) (TimeUnit.MILLISECONDS.toSeconds(trackTime));
    }

    public int toMilliseconds(int trackTime) {
        return (int) TimeUnit.SECONDS.toMillis(trackTime);
    }
}
