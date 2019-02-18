package com.sommerengineering.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class RecipeActivity extends AppCompatActivity implements DetailFragment.OnStepClickListener {

    // simple tag for log messages
    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();

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

        // create the DetailFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.recipe_container, detailFragment).commit();
    }

    @Override
    public void onStepSelected(Dessert dessert, int stepId) {

        // bundle the selected dessert and the step ID for a StepFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedDessert", dessert);
        bundle.putInt("selectedStepId", stepId);

        // instantiate a new StepFragment  and add the bundle
        StepFragment playerFragment = new StepFragment();
        playerFragment.setArguments(bundle);

        // create the StepFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.recipe_container, playerFragment).commit();

    }

    // ensure that the back button returns the user to the MainActivity
    @Override
    public void onBackPressed() {
        finish();
        Intent intentToStartMainActivity = new Intent(this, MainActivity.class);
        startActivity(intentToStartMainActivity);
    }
}
