/*Designed and Developed by V2R Solution*/
package com.wscubetech.seovideotutorials.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.wscubetech.seovideotutorials.R;
import com.wscubetech.seovideotutorials.Urls.Urls;
import com.wscubetech.seovideotutorials.model.VideoModel;
import com.wscubetech.seovideotutorials.utils.ConnectionDetector;
import com.wscubetech.seovideotutorials.utils.GetSetSharedPrefs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoTutorialDetailYoutubeActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {


    YouTubePlayerView youTubePlayerView;

    public static final int RECOVERY_DIALOG_REQUEST = 9;

    VideoModel videoModel;

    TextView txtTitle, txtDescription;
    TextView txtHeaderTitle, txtViews;

    boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        txtHeaderTitle = (TextView) findViewById(R.id.txtHeaderTitle);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtViews = (TextView) findViewById(R.id.txtViews);

        active=true;

        setHeaderTitle();

        //content
        videoModel = (VideoModel) getIntent().getExtras().getSerializable("VideoModel");
        txtTitle.setText(videoModel.getVideoTitle());
        txtDescription.setText(videoModel.getVideoDescription());
        updateVideoViewsUi();


        Linkify.addLinks(txtDescription, Linkify.ALL);


        // Initializing video player with developer key
        youTubePlayerView.initialize(getString(R.string.api_key_youtube), this);

        if(new ConnectionDetector(this).isConnectingToInternet())
            fetchNumberOfViewsOfVideo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active=true;
    }

    @Override
    protected void onStop() {
        active=false;
        super.onStop();
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

    private void setHeaderTitle() {
        GetSetSharedPrefs prefs = new GetSetSharedPrefs(this, "CourseSelection");
        String courseTitle = prefs.getData("SelectedCourseName");
        txtHeaderTitle.setText(courseTitle);
    }

    private void fetchNumberOfViewsOfVideo() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Urls.YOU_TUBE_DATA_API).newBuilder();
        urlBuilder.addQueryParameter("id", videoModel.getVideoLink());
        String url = urlBuilder.build().toString();
        Log.v("UrlVideosForCount", url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response res) throws IOException {

                final String response = Html.fromHtml(res.body().string()).toString();
                Log.v("ResponseYouTubeDataApi", response);
                if (active) {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray array = json.getJSONArray("items");
                        for (int i = 0; i < array.length(); i++) {
                            final int k = i;
                            JSONObject obj = array.getJSONObject(i);
                            JSONObject objStats = obj.getJSONObject("statistics");
                            final long views = Long.parseLong(objStats.getString("viewCount"));
                            videoModel.setVideoViews(views);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(active){
                                    updateVideoViewsUi();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.v("ExceptionParseYoutube", "" + e);
                    }

                }




            }
        });
    }

    private void updateVideoViewsUi(){
        txtViews.setText(videoModel.getVideoViews() + " views");
        txtViews.setVisibility(videoModel.getVideoViews() == 0 ? View.GONE : View.VISIBLE);
    }
}
