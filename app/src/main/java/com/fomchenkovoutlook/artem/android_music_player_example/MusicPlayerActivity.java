package com.fomchenkovoutlook.artem.android_music_player_example;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fomchenkovoutlook.artem.android_music_player_example.player.Player;
import com.fomchenkovoutlook.artem.android_music_player_example.player.Track;
import com.fomchenkovoutlook.artem.android_music_player_example.utils.PlayerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity
        extends AppCompatActivity
            implements View.OnClickListener, View.OnLongClickListener {

    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1;
    private static final int SEEK_TO_ON_LONG_CLICK = 5000;

    private int SYSTEM_EXIT_CODE = 119;

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

    private Handler timeHandler = new Handler();

    private boolean isTimeListenerSet;

    private void setCover(Bitmap trackCover) {
        if (trackCover != null) {
            ivTrackCover.setImageBitmap(trackCover);
        } else {
            ivTrackCover.setImageDrawable(getResources().getDrawable(R.drawable.ic_music_note_track));
        }
    }

    private void setImageDrawable(ImageButton imageButton, int drawable) {
        imageButton.setImageDrawable(getResources()
                .getDrawable(drawable));
    }

    private void toSettingsDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.permission_error_dialog_title)
                .setMessage(R.string.permission_error_dialog_message)
                .setNegativeButton(R.string.permission_error_dialog_negative_button_text,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.exit(SYSTEM_EXIT_CODE);
                            }
                        })
                .setPositiveButton(R.string.permission_error_dialog_positive_button_text,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                intent.setData(uri);
                                context.startActivity(intent);
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
    }

    private void permissionCheck() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            toSettingsDialog(this);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST);
        }
    }

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

                if (Player.getInstance().endPlaying()) {
                    next();
                }

                timeHandler.postDelayed(this, 1000);
            }
        });
    }

    private void previous() {
        if (position > 0) {
            Player.getInstance().stop();

            --position;

            ibSkipNextTrack.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_skip_next_track_on));

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
            setImageDrawable(ibSkipPreviousTrack, R.drawable.ic_skip_previous_track_off);
        }
    }

    private void play() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        } else {
            if (tracks.isEmpty()) {
                tracks.addAll(Player.getInstance().checkDirectory());
            }

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
                    }

                    setImageDrawable(ibSkipNextTrack, R.drawable.ic_skip_next_track_on);
                }

                tvTrackEndTime.setText(playerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
            } else {
                trackTimePosition = Player.getInstance().getTrackTimePosition();

                Player.getInstance().pause();

                setImageDrawable(ibPlayOrPauseTrack, R.drawable.ic_play_track);
            }
        }
    }

    private void next() {
        if (position < tracks.size()) {
            Player.getInstance().stop();

            ++position;

            ibSkipPreviousTrack.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_skip_previous_track_on));

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
            ibSkipNextTrack.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_skip_next_track_off));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_skip_previous_track:
                previous();

                break;
            case R.id.ib_play_or_pause_track:
                play();

                break;
            case R.id.ib_skip_next_track:
                next();

                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    private void init() {
        Player.getInstance().init();

        playerUtils = new PlayerUtils();

        tracks = new ArrayList<>();

        ivTrackCover = findViewById(R.id.iv_track_cover);

        sbTrackTimeline = findViewById(R.id.sb_track_timeline);
        sbTrackTimeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int length, boolean state) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Player.getInstance().toTime(playerUtils.toMilliseconds(seekBar.getProgress()));
            }
        });

        tvTrack = findViewById(R.id.tv_track);
        tvTrackStartTime = findViewById(R.id.tv_track_start_time);
        tvTrackEndTime = findViewById(R.id.tv_track_end_time);

        tvTrack.setSelected(true);

        ibSkipPreviousTrack = findViewById(R.id.ib_skip_previous_track);
        ibPlayOrPauseTrack = findViewById(R.id.ib_play_or_pause_track);
        ibSkipNextTrack = findViewById(R.id.ib_skip_next_track);

        ibSkipPreviousTrack.setOnLongClickListener(this);
        ibSkipNextTrack.setOnLongClickListener(this);

        ibSkipPreviousTrack.setOnClickListener(this);
        ibPlayOrPauseTrack.setOnClickListener(this);
        ibSkipNextTrack.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        init();
    }
}
