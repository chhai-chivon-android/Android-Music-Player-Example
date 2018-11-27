package com.fomchenkovoutlook.artem.android_music_player_example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fomchenkovoutlook.artem.android_music_player_example.player.Player;
import com.fomchenkovoutlook.artem.android_music_player_example.player.Track;
import com.fomchenkovoutlook.artem.android_music_player_example.support.Constants;
import com.fomchenkovoutlook.artem.android_music_player_example.support.utils.AppUtils;
import com.fomchenkovoutlook.artem.android_music_player_example.support.utils.PlayerUtils;
import com.fomchenkovoutlook.artem.android_music_player_example.support.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MusicPlayerActivity extends AppCompatActivity {

    @BindView(R.id.track_cover) protected ImageView trackCover;
    @BindView(R.id.previous_track) protected ImageView previousTrack;
    @BindView(R.id.play_or_pause) protected ImageView playOrPause;
    @BindView(R.id.next_track) protected ImageView nextTrack;


    @BindView(R.id.track_title) protected TextView trackTitle;
    @BindView(R.id.track_progress) protected TextView trackProgress;
    @BindView(R.id.track_duration) protected TextView trackDuration;

    @BindView(R.id.track_timeline) protected SeekBar trackTimeline;

    private List<Track> tracks;

    private int position;
    private int oldPosition;
    private int trackTimePosition;
    private boolean isTimeListenerSet;

    private Handler timeHandler = new Handler();

    private void setPositionListener() {
        isTimeListenerSet = true;
        runOnUiThread(() -> {
            if (oldPosition != position) {
                oldPosition = position;
                trackTimeline.setMax(PlayerUtils.toSeconds(Player.getInstance().getTrackEndTime()));
            }

            trackTimeline.setProgress(PlayerUtils.toSeconds(Player.getInstance().getTrackTimePosition()));
            trackProgress.setText(PlayerUtils.toMinutes(Player.getInstance()
                    .getTrackTimePosition()));

            if (Player.getInstance().endPlaying()) {
                next();
            }

            timeHandler.postDelayed(this::setPositionListener, Constants.TIME.TRACK_PROGRESS_DELAY_IN_MILLIS);
        });
    }

    private void previous() {
        if (position > 0) {
            Player.getInstance().stop();
            --position;
            UIUtils.colorizeImage(nextTrack, true);
            Player.getInstance().play(tracks.get(position));
            UIUtils.setImageDrawable(playOrPause, R.drawable.ic_pause);
            UIUtils.setBitmapCover(trackCover, Player.getInstance().getCover(tracks.get(position), this));
            trackTitle.setText(tracks.get(position).getTitle());
            trackDuration.setText(PlayerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
        }
        if (position == 0) {
            UIUtils.colorizeImage(previousTrack, false);
        }
    }

    private void playOrPause() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            AppUtils.checkReadStoragePermission(this);
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
            if (Player.getInstance().isPausing()) {
                if (trackTimePosition > 0) {
                    Player.getInstance().resume();
                } else {
                    Player.getInstance().play(tracks.get(position));
                }
                if (!isTimeListenerSet) {
                    setPositionListener();
                    UIUtils.setBitmapCover(trackCover, Player.getInstance().getCover(tracks.get(position), this));
                    trackTitle.setText(tracks.get(position).getTitle());
                    trackTimeline.setMax(PlayerUtils.toSeconds(Player.getInstance().getTrackEndTime()));
                }
                UIUtils.setImageDrawable(playOrPause, R.drawable.ic_pause);
                if (tracks.size() > 1) {
                    if (trackProgress.getVisibility() == View.INVISIBLE) {
                        trackProgress.setVisibility(View.VISIBLE);
                        UIUtils.colorizeImage(nextTrack, true);
                    }
                }
                trackDuration.setText(PlayerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
            } else {
                trackTimePosition = Player.getInstance().getTrackTimePosition();
                Player.getInstance().pause();
                UIUtils.setImageDrawable(playOrPause, R.drawable.ic_play);
            }
        }
    }

    private void next() {
        if (position < tracks.size() - 1) {
            Player.getInstance().stop();
            ++position;
            UIUtils.colorizeImage(previousTrack, true);
            Player.getInstance().play(tracks.get(position));
            UIUtils.setImageDrawable(playOrPause, R.drawable.ic_pause);
            UIUtils.setBitmapCover(trackCover, Player.getInstance().getCover(tracks.get(position), this));
            trackTitle.setText(tracks.get(position).getTitle());
            trackDuration.setText(PlayerUtils.toMinutes(Player.getInstance().getTrackEndTime()));
        }
        if (position == tracks.size() - 1) {
            UIUtils.colorizeImage(nextTrack, false);
        }
    }

    @OnClick(R.id.previous_track)
    public void onPreviousTrackClick() {
        previous();
    }

    @OnClick(R.id.play_or_pause)
    public void onPlayPauseClick() {
        playOrPause();
    }

    @OnClick(R.id.next_track)
    public void onNextTrackClick() {
        next();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);

        tracks = new ArrayList<>();

        trackTimeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int length, boolean state) {}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Player.getInstance().toTime(PlayerUtils.toMilliseconds(seekBar.getProgress()));
            }
        });

        // If the track title is long - start the running line:
        trackTitle.setSelected(true);

        UIUtils.colorizeImage(previousTrack, false);
        UIUtils.colorizeImage(nextTrack, false);
    }

}
