package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
    private Step mStep;

    // bind views using Butterknife library
//    @BindView(R.id.tv_name)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // initialize the Butterknife library
        ButterKnife.bind(this);

        // reference to application context
        mContext = getApplicationContext();

        // extract Dessert from intent
        Intent intent = getIntent();
        mStep = (Step) intent.getSerializableExtra("selectedStep");

        // simple method to bind title textviews for dessert name and number of servings
//        setTitles();

        // method iterates through an ArrayList<Ingredient> and dynamically creates views
//        setIngredients(mDessert.getIngredients(), R.id.ingredients_divider);

        // method iterates through an ArrayList<Step> and dynamically creates views
//        setSteps(mDessert.getSteps(), R.id.steps_divider);

    }
}
