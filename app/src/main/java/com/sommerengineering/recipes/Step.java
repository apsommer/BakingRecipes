package com.sommerengineering.recipes;

import java.io.Serializable;

// POJO holds metadata for a cooking step when making a dessert
public class Step implements Serializable {

    // base attributes
    private final int mId;
    private final String mShortDescription;
    private final String mDescription;
    private String mVideoPath;
    private String mThumbnailPath;

    // constructor
    Step(int id, String shortDescription, String description, String videoPath, String thumbnailPath) {

        // base attributes
        mId = id;
        mShortDescription = shortDescription;
        mDescription = description;
        mVideoPath = videoPath;
        mThumbnailPath = thumbnailPath;
    }

    // getters
    int getId() {
        return mId;
    }
    String getShortDescription() {
        return mShortDescription;
    }
    String getDescription() {
        return mDescription;
    }
    String getVideoPath() {
        return mVideoPath;
    }
    String getThumbnailPath() {
        return mThumbnailPath;
    }

    // setters - needed to catch the JSON error of video URL incorrectly in the image URL key
    void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
    }
    void setThumbnailPath(String thumbnailPath) {
        mThumbnailPath = thumbnailPath;
    }
}
