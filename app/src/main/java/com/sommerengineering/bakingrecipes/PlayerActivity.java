package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {

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

    // bind views using Butterknife library
    @BindView(R.id.tv_short_description) TextView mShortDescriptionTv;
    @BindView(R.id.tv_description) TextView mDescriptionTv;
    @BindView(R.id.ib_left_arrow) ImageButton mLeftArrowIb;
    @BindView(R.id.tv_previous_step) TextView mPreviousStepTv;
    @BindView(R.id.ib_right_arrow) ImageButton mRightArrowIb;
    @BindView(R.id.tv_next_step) TextView mNextStepTv;
    @BindView(R.id.iv_step_image) ImageView mThumbnailIv;

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

        // if it exists, load the image into the UI using the Picasso library
        setImage();

        // TODO implement ExoPlayer

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

    // determine where this step is in the list and set member variables and UI components accordingly
    private void setPreviousAndNextSteps() {

        // check if this is the first step
        if (mStepId == 0) {

            // hide the left arrow button and previous step short description
            mLeftArrowIb.setVisibility(View.GONE);
            mPreviousStepTv.setVisibility(View.GONE);
            mPreviousStep = null;

            // get the next step and its short description
            mNextStep = mSteps.get(mStepId + 1);
            String nextStepShortDescription = mNextStep.getShortDescription();

            // set the text in the textview next to the right arrow
            mNextStepTv.setText(nextStepShortDescription);
        }

        // check that this step is in the middle of the list
        else if (mStepId > 0 && mStepId < mSteps.size() - 1) {

            // get the previous step and its short description
            mPreviousStep = mSteps.get(mStepId - 1);
            String previousStepShortDescription = mPreviousStep.getShortDescription();

            // set the text in the textview next to the left arrow
            mPreviousStepTv.setText(previousStepShortDescription);

            // get the next step and its short description
            mNextStep = mSteps.get(mStepId + 1);
            String nextStepShortDescription = mNextStep.getShortDescription();

            // set the text in the textview next to the right arrow
            mNextStepTv.setText(nextStepShortDescription);
        }

        // check if this is the last step
        else if (mStepId == mSteps.size() - 1) {

            // hide the right arrow button and next step short description
            mRightArrowIb.setVisibility(View.GONE);
            mNextStepTv.setVisibility(View.GONE);
            mNextStep = null;

            // get the previous step and its short description
            mPreviousStep = mSteps.get(mStepId - 1);
            String previousStepShortDescription = mPreviousStep.getShortDescription();

            // set the text in the textview next to the left arrow
            mPreviousStepTv.setText(previousStepShortDescription);
        }
    }

    // load the left and right arrows to restart this activity with a new step
    private void setNavigationArrows() {

        // if a previous step exists then put a click listener on the left arrow
        if (mPreviousStep != null) {

            // restart this activity with the new step
            mLeftArrowIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // bundle the selected Dessert and the step ID into an explicit intent for PlayerActivity
                    Intent intentToStartPlayerActivity = new Intent(mContext, PlayerActivity.class);
                    intentToStartPlayerActivity.putExtra("selectedDessert", mDessert);
                    intentToStartPlayerActivity.putExtra("selectedStepId", mStepId - 1);
                    startActivity(intentToStartPlayerActivity);
                }
            });
        }

        // if a next step exists then put a click listener on the right arrow
        if (mNextStep != null) {

            // restart this activity with the new step
            mRightArrowIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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

    // ensure that the back button returns the user to the DetailActivity
    @Override
    public void onBackPressed() {

        // bundle the Dessert into an explicit intent for DetailActivity
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra("selectedDessert", mDessert);
        startActivity(intentToStartDetailActivity);
    }
}
