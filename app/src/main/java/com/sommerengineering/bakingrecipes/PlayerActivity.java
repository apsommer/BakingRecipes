package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity implements ExoPlayer.EventListener {

    // simple tag for log messages
    private static final String LOG_TAG = PlayerActivity.class.getSimpleName();

    // member variables
    private Context mContext;
    private Dessert mDessert;
    private ArrayList<Step> mSteps;
    private Step mStep;
    private int mStepId;
    private Step mPreviousStep;
    private Step mNextStep;
    private SimpleExoPlayer mExoPlayer;
    private android.support.v4.media.session.MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    // bind views using Butterknife library
    @BindView(R.id.tv_short_description) TextView mShortDescriptionTv;
    @BindView(R.id.tv_description) TextView mDescriptionTv;
    @BindView(R.id.b_left_arrow) Button mLeftArrowB;
    @BindView(R.id.b_right_arrow) Button mRightArrowB;
    @BindView(R.id.iv_step_image) ImageView mThumbnailIv;
    @BindView(R.id.exo_player_view) SimpleExoPlayerView mExoPlayerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // initialize the Butterknife library
        ButterKnife.bind(this);

        // reference to application context
        mContext = getApplicationContext();

        // extract the Dessert and Step ID from the intent
        Intent intent = getIntent();
        mDessert = (Dessert) intent.getSerializableExtra("selectedDessert");
        mStepId = (int) intent.getSerializableExtra("selectedStepId");

        // get the full list of steps for this dessert, and the current step
        mSteps = mDessert.getSteps();
        mStep = mSteps.get(mStepId);

        // simple method to bind title textviews for dessert name and number of servings
        setTitles();

        // set the previous and next steps and set their textview descriptions
        setPreviousAndNextSteps();

        // set click listeners on the left and right navigation arrows
        setNavigationArrows();

        // load the step image into the UI using the Picasso library
        setImage();

        // load the step video into the UI using ExoPlayer
        setVideo();

    }

    // simple method to bind title textviews
    private void setTitles() {

        // get basic attributes of this step
        final String shortDescription = mStep.getShortDescription();
        final String description = mStep.getDescription();

        // set textviews for title and descriptions
        mShortDescriptionTv.setText(shortDescription);
        mDescriptionTv.setText(description);
    }

    // determine where this step is in the list and set member variables
    private void setPreviousAndNextSteps() {

        // the first step has no previous step
        if (mStepId == 0) {
            mPreviousStep = null;
            mNextStep = mSteps.get(mStepId + 1);
        }

        // a step in the middle of the list has both previous and next steps
        else if (mStepId > 0 && mStepId < mSteps.size() - 1) {
            mPreviousStep = mSteps.get(mStepId - 1);
            mNextStep = mSteps.get(mStepId + 1);
        }

        // the last step has no next step
        else if (mStepId == mSteps.size() - 1) {
            mPreviousStep = mSteps.get(mStepId - 1);
            mNextStep = null;
        }
    }

    // load the left and right arrows to restart this activity with a new step
    private void setNavigationArrows() {

        // if there is no previous step then hide the navigation button
        if (mPreviousStep == null) {
            mLeftArrowB.setVisibility(View.GONE);
        }

        // set the previous button text and add a click listener to restart this activity
        else {

            // previous step button text
            String previousStepShortDescription = mPreviousStep.getShortDescription();
            mLeftArrowB.setText(previousStepShortDescription);

            // add ripple effect on button press
            RippleDrawable rippleDrawable = Utilities.getButtonRipple(mContext);
            mLeftArrowB.setBackground(rippleDrawable);

            // on the click restart this activity with the new step
            mLeftArrowB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // stop the playing video
                    if (mExoPlayer != null) mExoPlayer.stop();

                    // bundle the selected Dessert and the step ID into an explicit intent for PlayerActivity
                    Intent intentToStartPlayerActivity = new Intent(mContext, PlayerActivity.class);
                    intentToStartPlayerActivity.putExtra("selectedDessert", mDessert);
                    intentToStartPlayerActivity.putExtra("selectedStepId", mStepId - 1);
                    startActivity(intentToStartPlayerActivity);
                }
            });
        }

        // if there is no next step then hide the navigation button
        if (mNextStep == null) {
            mRightArrowB.setVisibility(View.GONE);
        }

        // set the next button text and add a click listener to restart this activity
        else {

            // next step button text
            String nextStepShortDescription = mNextStep.getShortDescription();
            mRightArrowB.setText(nextStepShortDescription);

            // add ripple effect on button press
            RippleDrawable rippleDrawable = Utilities.getButtonRipple(mContext);
            mRightArrowB.setBackground(rippleDrawable);

            // on the click restart this activity with the new step
            mRightArrowB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // stop playing the video
                    if (mExoPlayer != null) mExoPlayer.stop();

                    // bundle the selected Dessert and the step ID into an explicit intent for PlayerActivity
                    Intent intentToStartPlayerActivity = new Intent(mContext, PlayerActivity.class);
                    intentToStartPlayerActivity.putExtra("selectedDessert", mDessert);
                    intentToStartPlayerActivity.putExtra("selectedStepId", mStepId + 1);
                    startActivity(intentToStartPlayerActivity);
                }
            });
        }
    }

    // load the step image if it exists
    private void setImage() {

        // extract image URL from current step
        String thumbnailPath = mStep.getThumbnailPath();

        // Udacity deliberately put the video URL into the image URL JSON key for
        // Nutella Pie > Step 5 ... catch this error here by swapping the attributes
        if (thumbnailPath.contains(".mp4")) {
            mStep.setThumbnailPath(null);
            mStep.setVideoPath(thumbnailPath);
        }

        // check that the image URL is valid
        if (thumbnailPath != null && !thumbnailPath.isEmpty()) {

            // ! placeholder image must be set for the Picasso library to load correctly
            ColorDrawable simpleColor =
                    new ColorDrawable(mContext.getResources().getColor(R.color.white));

            // load image into UI using Picasso library
            Picasso.with(mContext).load(thumbnailPath).placeholder(simpleColor).into(mThumbnailIv);
        }
    }

    // play video using ExoPlayer
    private void setVideo() {

        // extract the video URL from the current step
        String videoPath = mStep.getVideoPath();

        // if there is no video then hide the player and return
        if (videoPath == null || videoPath.isEmpty()) {
            mExoPlayerView.setVisibility(View.GONE);
            return;
        }

        // convert path string to path URI
        Uri videoUri = Uri.parse(videoPath);

        // check that the URI is valid
        if (videoUri != null) {

            // set the default artwork while the player loads
            mExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(
                    getResources(), R.drawable.white));

            // instantiate the player using a default track selector and load control
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

            // associate the player to the player view
            mExoPlayerView.setPlayer(mExoPlayer);

            // prepare the media source using a default data source factory and extractors factory
            String userAgent = Util.getUserAgent(this, "BakingRecipes");
            DefaultDataSourceFactory sourceFactory = new DefaultDataSourceFactory(this, userAgent);
            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, sourceFactory,
                    extractorsFactory, null, null);

            // add an event listener, the listener only outputs log messages for now
            mExoPlayer.addListener(this);

            // scale video to the device width while maintaining aspect ratio
            mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

            // prepare the player with the media source and play when ready
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

            // initialize a media session to give external clients (ex. headphones) control of the player
            initializeMediaSession();

        }
    }

    // ensure that the back button returns the user to the DetailActivity
    @Override
    public void onBackPressed() {

        // bundle the Dessert into an explicit intent for DetailActivity
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra("selectedDessert", mDessert);
        startActivity(intentToStartDetailActivity);
    }

    // release the ExoPlayer when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // set media session to inactive
        mMediaSession.setActive(false);

        // if a video is playing then stop and release it
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    // a media session gives external clients (ex. headphones) control of the player
    private void initializeMediaSession() {

        // initialize the session
        mMediaSession = new MediaSessionCompat(this, "MediaSessionTAG");

        // enable callbacks for buttons and transport controls
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // do not allow external media buttons to restart the player when the app is not visible
        mMediaSession.setMediaButtonReceiver(null);

        // build an initial playback state so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_PLAY_PAUSE);

        // set the session playback state
        mMediaSession.setPlaybackState(mStateBuilder.build());

        // set the callbacks to the inner class
        mMediaSession.setCallback(new StepMediaSessionCallback());

        // start the session in this active activity
        mMediaSession.setActive(true);
    }

    // six ExoPlayer event listener methods
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.e(LOG_TAG, "onTimelineChanged");
    }
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.e(LOG_TAG, "onTracksChanged");
    }
    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.e(LOG_TAG, "onLoadingChanged");
    }
    @Override
    public void onPlayerStateChanged(boolean isPlaying, int playbackState) {

        if (isPlaying && playbackState == ExoPlayer.STATE_READY) {

            Log.e(LOG_TAG, "onPlayerStateChanged: playing");

            // update the playback state
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);

        } else if (playbackState == ExoPlayer.STATE_READY) {

            Log.e(LOG_TAG, "onPlayerStateChanged: paused");

            // update the playback state
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
    }
    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e(LOG_TAG, "onPlayerError");
    }
    @Override
    public void onPositionDiscontinuity() {
        Log.e(LOG_TAG, "onPositionDiscontinuity");
    }

    // callbacks for external clients controlling the player
    private class StepMediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
