package com.fomchenkovoutlook.artem.android_music_player_example.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_3GP;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_AAC;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_FLAC;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_IMY;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_M4A;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_MKV;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_MP3;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_MP4;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_MXMF;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_OGG;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_OTA;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_RTTTL;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_RTX;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_TS;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_WAV;
import static com.fomchenkovoutlook.artem.android_music_player_example.utils.Extensions.MUSIC_EXTENSION_XMF;

public class PlayerUtils {

    private List<String> musicExtensions = new ArrayList<>();

    /**
     * Add all supported music extensions
     */
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

    /**
     * Check file extension and get result
     * @param file selected file
     */
    public boolean isMusicFile(@NonNull File file) {
        for (String extension: musicExtensions) {
            if (file.getAbsolutePath().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert time value to minutes
     * @param trackTime track time
     */
    @SuppressLint("DefaultLocale")
    public String toMinutes(int trackTime) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(trackTime),
                TimeUnit.MILLISECONDS.toSeconds(trackTime) % TimeUnit.MINUTES.toSeconds(1));
    }

    /**
     * Convert time value to seconds
     * @param trackTime track time
     */
    public int toSeconds(int trackTime) {
        return (int) (TimeUnit.MILLISECONDS.toSeconds(trackTime));
    }

    /**
     * Convert time value to milliseconds
     * @param trackTime track time
     */
    public int toMilliseconds(int trackTime) {
        return (int) TimeUnit.SECONDS.toMillis(trackTime);
    }

}
