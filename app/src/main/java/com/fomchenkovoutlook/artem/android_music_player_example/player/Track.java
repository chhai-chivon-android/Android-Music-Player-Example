package com.fomchenkovoutlook.artem.android_music_player_example.player;

import android.support.annotation.NonNull;

// Track:
public class Track {

    private String name;

    public String getName() {
        return name;
    }

    Track(@NonNull String name) {
        this.name = name;
    }
}
