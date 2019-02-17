package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements DetailFragment.OnStepClickListener {

    // simple tag for log messages
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // inflate the fragment container
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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
        fragmentManager.beginTransaction().add(R.id.detail_container, detailFragment).commit();
    }

    @Override
    public void onStepSelected(Dessert dessert, int stepId) {

        // bundle the selected Dessert and the step ID into an explicit intent for PlayerActivity
        Intent intentToStartPlayerActivity = new Intent(this, PlayerActivity.class);
        intentToStartPlayerActivity.putExtra("selectedDessert", dessert);
        intentToStartPlayerActivity.putExtra("selectedStepId", stepId);
        startActivity(intentToStartPlayerActivity);
    }

    // ensure that the back button returns the user to the MainActivity
    @Override
    public void onBackPressed() {
        Intent intentToStartMainActivity = new Intent(this, MainActivity.class);
        startActivity(intentToStartMainActivity);
    }
}
