package com.sommerengineering.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements DetailFragment.OnStepClickListener {

    // simple tag for log messages
    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();

    // flag for two-pane UI (tablet) or single pane UI (phone)
    private boolean mIsTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // inflate the fragment container
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // extract dessert from the main activity intent
        Intent intent = getIntent();
        Dessert dessert = (Dessert) intent.getSerializableExtra("selectedDessert");

        // put dessert into a bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedDessert", dessert);

        // instantiate a new DetailFragment and add the bundle
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);

        // get reference to the Android fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // single pane layout
        if (findViewById(R.id.fl_detail_container) == null) {

            // set flag
            mIsTwoPane = false;

            // create the detail fragment showing ingredients and step metadata
            fragmentManager.beginTransaction().add(R.id.fl_detail_and_step_container, detailFragment).commit();

        // two-pane layout
        } else {

            // set flag
            mIsTwoPane = true;

            // detail fragment always occupies 1/3 of screen width
            // get current device width in dp
            int dpWidth = getResources().getDisplayMetrics().widthPixels;

            // layout size and position are defined in LayoutParams
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    dpWidth/3, ViewGroup.LayoutParams.MATCH_PARENT);

            // bind these layout parameters to the framelayout
            FrameLayout recipeContainer = findViewById(R.id.fl_detail_container);
            recipeContainer.setLayoutParams(layoutParams);

            // create the recipe fragment showing ingredients and step metadata
            fragmentManager.beginTransaction().add(R.id.fl_detail_container, detailFragment).commit();
        }

    }

    @Override
    public void onStepSelected(Dessert dessert, int stepId) {

        // bundle the selected dessert and the step ID for a StepFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedDessert", dessert);
        bundle.putInt("selectedStepId", stepId);

        // instantiate a new StepFragment  and add the bundle
        StepFragment stepFragment = new StepFragment();
        stepFragment.setArguments(bundle);

        // get reference to the Android fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // single pane layout: step fragment replaces the recipe fragment
        if (!mIsTwoPane) {
            fragmentManager.beginTransaction().replace(R.id.fl_detail_and_step_container, stepFragment).commit();

        // two-pane layout: recipe fragment and step fragment shown simultaneously
        } else {
            fragmentManager.beginTransaction().replace(R.id.fl_step_container, stepFragment).commit();
        }

    }

    // ensure that the back button returns the user to the MainActivity
    @Override
    public void onBackPressed() {
        finish();
        Intent intentToStartMainActivity = new Intent(this, MainActivity.class);
        startActivity(intentToStartMainActivity);
    }
}
