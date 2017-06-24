/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.model.VideoModel;

public class VideoTutorialDetailYoutubeActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {


    YouTubePlayerView youTubePlayerView;

    public static final int RECOVERY_DIALOG_REQUEST = 9;

    VideoModel videoModel;

    TextView txtTitle,txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        txtTitle=(TextView)findViewById(R.id.txtTitle);
        txtDescription=(TextView)findViewById(R.id.txtDescription);

        //content
        videoModel = (VideoModel) getIntent().getExtras().getSerializable("VideoModel");
        txtTitle.setText(videoModel.getVideoTitle());
        txtDescription.setText(videoModel.getVideoDescription());

        Linkify.addLinks(txtDescription,Linkify.ALL);


        // Initializing video player with developer key
        youTubePlayerView.initialize(getString(R.string.api_key_youtube), this);
    }



    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {

            //youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            youTubePlayer.cueVideo(videoModel.getVideoLink());
            // Hiding player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "Error", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(getString(R.string.api_key_youtube), this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
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
}
