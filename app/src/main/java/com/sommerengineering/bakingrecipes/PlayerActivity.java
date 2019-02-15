package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {

    // simple tag for log messages
    private static final String LOG_TAG = PlayerActivity.class.getSimpleName();

    // member variables
    private Context mContext;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // initialize the Butterknife library
        ButterKnife.bind(this);

        // reference to application context
        mContext = getApplicationContext();

        // extract all Steps and the selected Step ID from intent
        Intent intent = getIntent();
        mSteps = (ArrayList<Step>) intent.getSerializableExtra("listOfSteps");
        mStepId = (int) intent.getSerializableExtra("selectedStepId");
        mStep = mSteps.get(mStepId);

        // simple method to bind title textviews for dessert name and number of servings
        setTitles();

        // get the previous and next steps and set their textview descriptions
        setPreviousAndNextSteps();

        // method iterates through an ArrayList<Ingredient> and dynamically creates views
//        setIngredients(mDessert.getIngredients(), R.id.ingredients_divider);

        // method iterates through an ArrayList<Step> and dynamically creates views
//        setSteps(mDessert.getSteps(), R.id.steps_divider);

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

            // get the previous step and its short description
            mPreviousStep = mSteps.get(mStepId - 1);
            String previousStepShortDescription = mPreviousStep.getShortDescription();

            // set the text in the textview next to the left arrow
            mPreviousStepTv.setText(previousStepShortDescription);
        }
    }
}
