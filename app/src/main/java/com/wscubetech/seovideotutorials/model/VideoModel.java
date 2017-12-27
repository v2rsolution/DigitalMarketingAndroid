package com.wscubetech.seovideotutorials.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by wscubetech on 20/8/16.
 */
public class VideoModel implements Serializable, Comparable<VideoModel> {

    String videoId = "", videoTitle = "", videoDescription = "", videoLink = "", videoDuration = "", videoImage = "";
    long videoViews = 0;
    public static byte flag = 1; //1->Newest First, 2->Oldest First, 3->Most Viewed first

    public long getVideoViews() {
        return videoViews;
    }

    public void setVideoViews(long videoViews) {
        this.videoViews = videoViews;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(String videoImage) {
        this.videoImage = videoImage;
    }

    @Override
    public int compareTo(@NonNull VideoModel videoModel) {
        int id1 = Integer.parseInt(videoId);
        int id2 = Integer.parseInt(videoModel.videoId);
        switch (flag) {
            //Oldest First
            case 2:
                return id1 - id2;

            //Most Viewed First
            case 3:
                return (int) (videoModel.videoViews - videoViews);

            //Newest First
            default:
                return id2 - id1;
        }

    }


}
