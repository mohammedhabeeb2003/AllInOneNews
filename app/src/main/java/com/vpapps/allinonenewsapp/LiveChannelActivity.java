package com.vpapps.allinonenewsapp;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.vpapps.AsyncTask.LoadChannel;
import com.vpapps.interfaces.ChannelListener;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LiveChannelActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textView_title, textView_desc;
    private String name="", url="", desc="", channelType ="", TAG_LIVE_URL = "liveurl", TAG_YOUTUBE = "youtube";
    private Methods methods;
    private LinearLayout ll_ad, ll_main;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private ImageView exo_pause, exo_play;
    private int videoplayer_height;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    private CircularProgressBar progressBar;
    private float scale;
    private View youtubeFragment;
    private YouTubePlayer ytPlayer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_channel);

        scale = getResources().getDisplayMetrics().density;
        videoplayer_height = (int) (220 * scale + 0.5f);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        progressBar = findViewById(R.id.progressBar_channel);

        ll_empty = findViewById(R.id.ll_empty);
        textView_empty = findViewById(R.id.textView_empty_msg);
        button_try = findViewById(R.id.button_empty_try);

        youtubeFragment = findViewById(R.id.youtubeFragment);
        ll_main = findViewById(R.id.ll_tv);
        ll_ad = findViewById(R.id.adView_ch);
        methods.showBannerAd(ll_ad);

        toolbar = findViewById(R.id.toolbar_channel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        exo_play = findViewById(R.id.exo_play);
        exo_pause = findViewById(R.id.exo_pause);

        textView_title = findViewById(R.id.textView_channel_title);
        textView_desc = findViewById(R.id.textView_channel_desc);

        textView_title.setTypeface(methods.getFontMedium());

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(LiveChannelActivity.this, trackSelector, loadControl);
        simpleExoPlayerView = new SimpleExoPlayerView(LiveChannelActivity.this);
        simpleExoPlayerView = findViewById(R.id.player_view);

        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        simpleExoPlayerView.setPlayer(player);
        player.addListener(eventListener);

        loadChannel();

        exo_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.setPlayWhenReady(true);
            }
        });

        exo_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopVideoPlayer();
            }
        });
    }

    private void stopVideoPlayer() {
        if(channelType.equals(TAG_LIVE_URL)) {
            player.setPlayWhenReady(false);
            player.stop();

            initiExo();
        }
    }

    private void initiExo() {
        Uri mp4VideoUri = Uri.parse(url);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(LiveChannelActivity.this, Util.getUserAgent(LiveChannelActivity.this, "exoplayer2example"), null);
        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
        player.prepare(loopingSource);
        player.setPlayWhenReady(true);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeToLandscape();
        } else {
            changeToPortrait();
        }
    }

    private void initYoutube() {
        YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        youtubeFragment.initialize(BuildConfig.API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        ytPlayer = youTubePlayer;
                        youTubePlayer.loadVideo(url);
                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            changeToLandscape();
                        } else {
                            changeToPortrait();
                        }
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        ytPlayer = null;
                    }
                });
    }

    private void loadChannel() {
        if (methods.isNetworkAvailable()) {
            LoadChannel loadChannel = new LoadChannel(new ChannelListener() {
                @Override
                public void onStart() {
                    ll_empty.setVisibility(View.GONE);
                    ll_main.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, String ch_name, String ch_desc, String ch_url, String ch_type) {
                    errr_msg = getString(R.string.no_data_found);

                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            name = ch_name;
                            url = ch_url;
                            desc = ch_desc;
                            channelType = ch_type;

                            if(channelType.equals(TAG_LIVE_URL)) {
                                simpleExoPlayerView.setVisibility(View.VISIBLE);
                                youtubeFragment.setVisibility(View.GONE);
                                initiExo();
                            } else if(channelType.equals(TAG_YOUTUBE)) {
                                initYoutube();
                                youtubeFragment.setVisibility(View.VISIBLE);
                            }
                        } else {
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                        ll_empty.setVisibility(View.GONE);
                        ll_main.setVisibility(View.VISIBLE);
                    } else {
                        ll_empty.setVisibility(View.VISIBLE);
                        ll_main.setVisibility(View.GONE);
                    }
                    setVariables();
                    progressBar.setVisibility(View.GONE);
                }

            }, methods.getAPIRequest(Constant.METHOD_CHANNEL, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadChannel.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            ll_empty.setVisibility(View.VISIBLE);
            ll_main.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setVariables() {
        getSupportActionBar().setTitle(name);
        textView_title.setText(name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView_desc.setText(Html.fromHtml(desc, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView_desc.setText(Html.fromHtml(desc));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    Player.EventListener eventListener = new Player.EventListener() {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playWhenReady) {
                exo_pause.setVisibility(View.VISIBLE);
                exo_play.setVisibility(View.GONE);
            } else {
                exo_pause.setVisibility(View.GONE);
                exo_play.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (simpleExoPlayerView != null) {
                stopVideoPlayer();
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    };

    private void changeToLandscape() {
        if (channelType.equals(TAG_LIVE_URL)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            toolbar.setVisibility(View.GONE);
            ll_ad.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            simpleExoPlayerView.setLayoutParams(params);
        } else if(channelType.equals(TAG_YOUTUBE)){
            ytPlayer.setFullscreen(true);
        }
    }

    private void changeToPortrait() {
        if (channelType.equals(TAG_LIVE_URL)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            toolbar.setVisibility(View.VISIBLE);
            ll_ad.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = videoplayer_height;
            simpleExoPlayerView.setLayoutParams(params);
        } else if(channelType.equals(TAG_YOUTUBE)){
            ytPlayer.setFullscreen(false);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if((ytPlayer != null && channelType.equals(TAG_YOUTUBE))|| (simpleExoPlayerView != null && channelType.equals(TAG_LIVE_URL))) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                changeToLandscape();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                changeToPortrait();
            }
        }
    }

    @Override
    protected void onResume() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onResume();
    }

    @Override
    public void onPause() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (player != null) {
            try {
                stopVideoPlayer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(channelType.equals(TAG_LIVE_URL)) {
                player.setPlayWhenReady(false);
                player.stop();
                player.release();
            } else {
                ytPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
