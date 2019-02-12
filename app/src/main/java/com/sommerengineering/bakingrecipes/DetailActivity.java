package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    // simple tag for log messages
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    // member variables
    private Context mContext;
    private Dessert mDessert;
    private int mPosition;

    // bind views using Butterknife library
    @BindView(R.id.rl_container) RelativeLayout mRelativeLayout;
    @BindView(R.id.tv_name) TextView mNameTv;
    @BindView(R.id.tv_servings) TextView mServingsTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // initialize the Butterknife library
        ButterKnife.bind(this);

        // reference to application context
        mContext = getApplicationContext();

        // extract Dessert from intent
        Intent intent = getIntent();
        mDessert = (Dessert) intent.getSerializableExtra("selectedDessert");

        // simple method to bind title textviews for dessert name and number of servings
        mPosition = setTitles(mDessert);

        // method iterates through an ArrayList<Ingredient> and dynamically creates views
        setIngredients(mDessert.getIngredients(), mPosition);

        // method iterates through an ArrayList<Step> and dynamically creates views
//        setSteps(mDessert.getSteps());


    }

    // simple method to bind title textviews
    private int setTitles(Dessert dessert) {

        // get basic attributes of this dessert
        String name = mDessert.getName();
        int servings = mDessert.getServings();

        // set textviews for title and servings
        mNameTv.setText(name);
        mServingsTv.setText(String.valueOf(servings));

        // return the position of the last view
        return mServingsTv.getId();
    }

    // bind Ingredient data to dynamically created textviews
    private void setIngredients(ArrayList<Ingredient> ingredients, int position) {

        // loop through all Ingredients in the list
        for (int i = 0; i < ingredients.size(); i++) {

            // get the current Ingredient and extract its basic attributes
            Ingredient ingredient = ingredients.get(i);
            final String name = ingredient.getName();
            final String quantity = String.valueOf(ingredient.getQuantity());
            final String measure = ingredient.getMeasure();

            // create a textview for each attribute
            position = createNameTv(name, position);
//            createQuantityTv(quantity);
//            createMeasureTv(measure);
        }
    }

    //
    private int createNameTv(String name, int position) {

        // new TextView
        TextView nameTv = new TextView(mContext);

        // size and position defined in layout parameters
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, position);

        // bind these layout parameters to the textview
        nameTv.setLayoutParams(layoutParams);

        // have the Android system create a unique ID
        nameTv.setId(View.generateViewId());

        //
        nameTv.setText(name);
        nameTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        nameTv.setTextColor(getResources().getColor(R.color.black));

        // custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "adamina.ttf");
        nameTv.setTypeface(font);

        // add the textview to the layout and return its position
        mRelativeLayout.addView(nameTv);
        return nameTv.getId();

    }

}
