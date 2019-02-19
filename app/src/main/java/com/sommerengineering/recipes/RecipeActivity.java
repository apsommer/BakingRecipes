package com.sommerengineering.recipes;

import android.app.Activity;
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

    // flag for two-pane UI (tablet) or single pane UI (phone)
    private boolean mIsTwoPane;

    Dessert mDessert;
    boolean mIsComingFromStep;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // inflate the fragment container
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // extract dessert from the main activity intent
        Intent intent = getIntent();
        mDessert = (Dessert) intent.getSerializableExtra("selectedDessert");

        // put dessert into a bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedDessert", mDessert);

        // instantiate a new DetailFragment and add the bundle
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);

        // get reference to the Android fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // single pane layout
        if (findViewById(R.id.fl_detail_fragment_container) == null) {
            mIsTwoPane = false;

            // create the detail fragment showing ingredients and step metadata
            fragmentManager.beginTransaction().add(R.id.fl_fragment_container, detailFragment).commit();

        // two-pane layout
        } else {
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
            fragmentManager.beginTransaction().add(R.id.fl_detail_fragment_container, detailFragment).commit();
        }

        //


    }

    @Override
    public void onStepSelected(Dessert dessert, int stepId) {

        // bundle the selected dessert and the step ID for a StepFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedDessert", dessert);
        bundle.putInt("selectedStepId", stepId);

        // instantiate a new StepFragment and add the bundle
        StepFragment stepFragment = new StepFragment();
        stepFragment.setArguments(bundle);

        mIsComingFromStep = true;

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
        finish();

        // coming from the step activity on a single pane layout
        if (!mIsTwoPane && mIsComingFromStep) {

            // bundle the Dessert into an explicit intent for RecipeActivity
            Intent intentToStartRecipeActivity = new Intent(this, RecipeActivity.class);
            intentToStartRecipeActivity.putExtra("selectedDessert", mDessert);
            startActivity(intentToStartRecipeActivity);
        }

        // start MainActivity
        else {
            Intent intentToStartMainActivity = new Intent(this, MainActivity.class);
            startActivity(intentToStartMainActivity);
        }
        
    }
}
