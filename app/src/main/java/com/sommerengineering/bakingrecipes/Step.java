package com.sommerengineering.bakingrecipes;

// POJO holds metadata for a cooking step when making a dessert
public class Step {

    // base attributes
    private final int mId;
    private final String mShortDescription;
    private final String mDescription;
    private final String mVideoPath;
    private final String mThumbnailPath;

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

}
