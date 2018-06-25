package com.fomchenkovoutlook.artem.android_music_player_example;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fomchenkovoutlook.artem.android_music_player_example.player.Player;
import com.fomchenkovoutlook.artem.android_music_player_example.player.Track;
import com.fomchenkovoutlook.artem.android_music_player_example.utils.PlayerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1;
    private static final int DELAY_IN_MILLIS = 1000;
    private static final int SYSTEM_EXIT_CODE = 5;

    private PlayerUtils playerUtils;

    private ImageView ivTrackCover;
    private TextView tvTrack;
    private TextView tvTrackStartTime;
    private TextView tvTrackEndTime;
    private SeekBar sbTrackTimeline;
    private ImageButton ibSkipPreviousTrack;
    private ImageButton ibPlayOrPauseTrack;
    private ImageButton ibSkipNextTrack;

    private List<Track> tracks;

    private int position;
    private int oldPosition;
    private int trackTimePosition;
    private boolean isTimeListenerSet;

    private Handler timeHandler = new Handler();

    /**
     * Set track's cover to ImageView
     * @param trackCover cover
     */
    private void setCover(@Nullable Bitmap trackCover) {
        if (trackCover != null) {
            ivTrackCover.setImageBitmap(trackCover);
        } else {
            ivTrackCover.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_note_track));
        }
    }

    /**
     * Set icon to ImageButton
     * @param imageButton needed button
     * @param drawable resource
     */
    private void setImageDrawable(@NonNull ImageButton imageButton, int drawable) {
        imageButton.setImageResource(drawable);
    }

    private void colorizeImage(@NonNull ImageButton imageButton, boolean toBlack) {
        if (toBlack) {
            imageButton.setColorFilter(ContextCompat.getColor(this, R.color.black));
        } else {
            imageButton.setColorFilter(ContextCompat.getColor(this, R.color.gray));
        }
    }

    /**
     * Open settings
     */
    private void openSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_error_dialog_title)
                .setMessage(R.string.permission_error_dialog_message)
                .setNegativeButton(R.string.permission_error_dialog_negative_button_text,
                        (dialogInterface, i) -> System.exit(SYSTEM_EXIT_CODE))
                .setPositiveButton(R.string.permission_error_dialog_positive_button_text,
                        (dialogInterface, i) -> {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * Check storage permission
     */
    private void checkReadStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openSettingsDialog();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST);
        }
    }

    /**
     * Listen track's position
     */
    private void positionListener() {
        isTimeListenerSet = true;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (oldPosition != position) {
                    oldPosition = position;
                    sbTrackTimeline.setMax(playerUtils.toSeconds(Player.getInstance().getTrackEndTime()));
                }
                sbTrackTimeline.setProgress(playerUtils.toSeconds(Player.getInstance().getTrackTimePosition()));
                tvTrackStartTime.setText(playerUtils.toMinutes(Player.getInstance()
                        .getTrackTimePosition()));

                // If a track was ended - play next:
                if (Player.getInstance().endPlaying()) {
                    next();
                }

                // Delay for check a track position:
                timeHandler.postDelayed(this, DELAY_IN_MILLIS);
            }
        });
    }

    /**
     * Skip to previous track
     */
    private void previous() {
        if (position > 0) {
            Player.getInstance().stop();

            // Set a new position:
            --position;
            colorizeImage(ibSkipNextTrack, true);
            try {
                Player.getInstance().play(tracks.get(position));
            } catch (IOException playerException) {
                playerException.printStackTrace();
            }
            setImageDrawable(ibPlayOrPauseTrack, R.drawable.ic_pause_track);
            setCover(Player.getInstance().getCover(tracks.get(position), this));
            tvTrack.setText(tracks.get(position).getName());
            tvTrackEndTime.setText(playerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
        }
        if (position == 0) {
            colorizeImage(ibSkipPreviousTrack, false);
        }
    }

    /**
     * Play or pause current track
     */
    private void playOrPause() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            checkReadStoragePermission();
        } else {
            if (tracks.isEmpty()) {
                List<Track> trackList = Player.getInstance().checkDirectory();
                if (!trackList.isEmpty()) {
                    tracks.addAll(trackList);
                } else {
                    Toast.makeText(this, R.string.music_dorectory_error, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Play:
            if (Player.getInstance().isPaused()) {
                if (trackTimePosition > 0) {
                    Player.getInstance().toTime(trackTimePosition);
                    Player.getInstance().resume();
                } else {
                    try {
                        Player.getInstance().play(tracks.get(position));
                        if (!isTimeListenerSet) {
                            positionListener();
                        }
                    } catch (IOException playerException) {
                        playerException.printStackTrace();
                    }
                    setCover(Player.getInstance().getCover(tracks.get(position), this));
                    tvTrack.setText(tracks.get(position).getName());
                    sbTrackTimeline.setMax(playerUtils.toSeconds(Player.getInstance().getTrackEndTime()));
                }
                setImageDrawable(ibPlayOrPauseTrack, R.drawable.ic_pause_track);
                if (tracks.size() > 1) {
                    if (tvTrackStartTime.getVisibility() == View.INVISIBLE) {
                        tvTrackStartTime.setVisibility(View.VISIBLE);
                        colorizeImage(ibSkipNextTrack, true);
                    }
                }
                tvTrackEndTime.setText(playerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
            } else { // Pause:
                trackTimePosition = Player.getInstance().getTrackTimePosition();
                Player.getInstance().pause();
                setImageDrawable(ibPlayOrPauseTrack, R.drawable.ic_play_track);
            }
        }
    }

    /**
     * Skip to next track
     */
    private void next() {
        if (position < tracks.size() - 1) {
            Player.getInstance().stop();

            // Set a new position:
            ++position;
            colorizeImage(ibSkipPreviousTrack, true);
            try {
                Player.getInstance().play(tracks.get(position));
            } catch (IOException playerException) {
                playerException.printStackTrace();
            }
            setImageDrawable(ibPlayOrPauseTrack, R.drawable.ic_pause_track);
            setCover(Player.getInstance().getCover(tracks.get(position), this));
            tvTrack.setText(tracks.get(position).getName());
            tvTrackEndTime.setText(playerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
        }
        if (position == tracks.size() - 1) {
            colorizeImage(ibSkipNextTrack, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        Player.getInstance().initialize();

        playerUtils = new PlayerUtils();
        tracks = new ArrayList<>();
        ivTrackCover = findViewById(R.id.iv_track_cover);

        sbTrackTimeline = findViewById(R.id.sb_track_timeline);
        sbTrackTimeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int length, boolean state) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Player.getInstance().toTime(playerUtils.toMilliseconds(seekBar.getProgress()));
            }
        });

        tvTrack = findViewById(R.id.tv_track);
        tvTrackStartTime = findViewById(R.id.tv_track_start_time);
        tvTrackEndTime = findViewById(R.id.tv_track_end_time);

        // Set the track ticker:
        tvTrack.setSelected(true);

        ibSkipPreviousTrack = findViewById(R.id.ib_skip_previous_track);
        ibPlayOrPauseTrack = findViewById(R.id.ib_play_or_pause_track);
        ibSkipNextTrack = findViewById(R.id.ib_skip_next_track);

        colorizeImage(ibSkipPreviousTrack, false);
        colorizeImage(ibSkipNextTrack, false);

        ibSkipPreviousTrack.setOnClickListener(v -> previous());
        ibPlayOrPauseTrack.setOnClickListener(v -> playOrPause());
        ibSkipNextTrack.setOnClickListener(v -> next());
    }

}
