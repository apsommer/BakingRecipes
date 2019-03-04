package com.sommerengineering.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RecipeActivity extends AppCompatActivity implements DetailFragment.OnStepClickListener {

    // simple tag for log messages
    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();

    // constants
    public static final String SELECTED_STEP_ID = "SELECTED_STEP_ID";
    public static final String IS_COMING_FROM_STEP = "IS_COMING_FROM_STEP";

    // flag for two-pane UI (tablet) or single pane UI (phone)
    private boolean mIsTwoPane;

    private Dessert mDessert;
    private boolean mIsComingFromStep;
    private int mStepId;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(IS_COMING_FROM_STEP, mIsComingFromStep);
        outState.putInt(SELECTED_STEP_ID, mStepId);
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {

        // inflate the fragment container
        super.onCreate(inState);
        setContentView(R.layout.activity_recipe);

        // extract dessert from the main activity intent
        Intent intent = getIntent();
        mDessert = (Dessert) intent.getSerializableExtra(MainActivity.SELECTED_DESSERT);

        // put dessert into a bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.SELECTED_DESSERT, mDessert);

        // instantiate a new DetailFragment and add the bundle
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);

        // get reference to the Android fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // detect single- or two- pane layout from presence of view ID only in two-pane layout
        // this conditional is for single-pane
        if (findViewById(R.id.fl_detail_fragment_container) == null) {

            // set flag
            mIsTwoPane = false;

            // check if a saved bundle exists
            if (inState != null) {
                mIsComingFromStep = inState.getBoolean(IS_COMING_FROM_STEP);
                mStepId = inState.getInt(SELECTED_STEP_ID);
            }

            // user was just viewing the step fragment
            if (mIsComingFromStep) {

                // use the callback for selecting a step
                onStepSelected(mDessert, mStepId);

            // user is coming from main activity
            } else {
                fragmentManager.beginTransaction().replace(R.id.fl_fragment_container, detailFragment).commit();
            }

        // two-pane layout
        } else {

            // set flag
            mIsTwoPane = true;

            // get current device width in dp
            int dpWidth = getResources().getDisplayMetrics().widthPixels;
            int screenColumns = 3; // split screen into 3 sections

            // layout size and position are defined in LayoutParams
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    dpWidth / screenColumns, ViewGroup.LayoutParams.MATCH_PARENT);

            // bind these layout parameters to the framelayout
            FrameLayout recipeContainer = findViewById(R.id.fl_detail_fragment_container);
            recipeContainer.setLayoutParams(layoutParams);

            // create the recipe fragment showing ingredients and step metadata
            fragmentManager.beginTransaction().replace(R.id.fl_detail_fragment_container, detailFragment).commit();
        }
    }

    // triggered when the user selects a step
    @Override
    public void onStepSelected(Dessert dessert, int stepId) {

        mIsComingFromStep = true;
        mStepId = stepId;

        // bundle the selected dessert and the step ID for a StepFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.SELECTED_DESSERT, dessert);
        bundle.putInt(SELECTED_STEP_ID, mStepId);

        // instantiate a new StepFragment and add the bundle
        StepFragment stepFragment = new StepFragment();
        stepFragment.setArguments(bundle);

        // get reference to the Android fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // single pane layout: step fragment replaces the recipe fragment
        if (!mIsTwoPane) {
            fragmentManager.beginTransaction().replace(R.id.fl_fragment_container, stepFragment).commit();

        // two-pane layout: recipe fragment and step fragment shown simultaneously
        } else {
            fragmentManager.beginTransaction().replace(R.id.fl_step_fragment_container, stepFragment).commit();
        }
    }

    // control navigation
    @Override
    public void onBackPressed() {

        // end activity
        finish();

        // the user is on a single-pane layout and coming from the step fragment the restart this activity
        if (!mIsTwoPane && mIsComingFromStep) {

            // bundle the Dessert into an explicit intent for RecipeActivity
            Intent intentToStartRecipeActivity = new Intent(this, RecipeActivity.class);
            intentToStartRecipeActivity.putExtra(MainActivity.SELECTED_DESSERT, mDessert);
            startActivity(intentToStartRecipeActivity);
        }

        // any other case go back to main activity
        else {
            Intent intentToStartMainActivity = new Intent(this, MainActivity.class);
            startActivity(intentToStartMainActivity);
        }
    }
}
