package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    // simple tag for log messages
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    // member variables
    private Context mContext;
    private Dessert mDessert;

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
        int position = setTitles();

        // method iterates through an ArrayList<Ingredient> and dynamically creates views
        position = setIngredients(mDessert.getIngredients(), position);

        // method iterates through an ArrayList<Step> and dynamically creates views
//        setSteps(mDessert.getSteps());


    }

    // simple method to bind title textviews
    private int setTitles() {

        // get basic attributes of this dessert
        String name = mDessert.getName();
        int servings = mDessert.getServings();

        // set textviews for title and servings
        mNameTv.setText(name);
        mServingsTv.setText(String.valueOf(servings));

        // return the position of the last view
        return R.id.ll_servings_container;
    }

    // bind Ingredient data to dynamically created textviews
    private int setIngredients(ArrayList<Ingredient> ingredients, int position) {

        // loop through all Ingredients in the list
        for (int i = 0; i < ingredients.size(); i++) {

            // get the current Ingredient and extract its basic attributes
            Ingredient ingredient = ingredients.get(i);
            final String name = StringUtils.capitalize(ingredient.getName().toLowerCase());
            final String quantity = String.valueOf(ingredient.getQuantity()) + " ";
            final String measure = ingredient.getMeasure().toLowerCase();

            // create a textview for each attribute; the position of the new view is returned
            position = createTv(name, position, 22, "BELOW", R.color.black);
            position = createTv(quantity, position, 12, "BELOW", R.color.gray);
            position = createTv(measure, position, 12, "RIGHT_OF", R.color.gray);
        }

        return position;
    }

    //
    private int createTv(String name, int position, int textSize, String alignment, int color) {

        // new TextView
        TextView textView = new TextView(mContext);

        // size and position defined in layout parameters
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginStart = Utilities.dpToPx(mContext, getResources().getDimension(R.dimen.detail_spacing));
        layoutParams.setMarginStart(marginStart);

        if (alignment.equals("BELOW")) {
            layoutParams.addRule(RelativeLayout.BELOW, position);
        }
        else if (alignment.equals("RIGHT_OF")) {
            layoutParams.addRule(RelativeLayout.RIGHT_OF, position);
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, position);
            layoutParams.setMarginStart(0);
        }

        // bind these layout parameters to the textview
        textView.setLayoutParams(layoutParams);

        // have the Android system create a unique ID
        textView.setId(View.generateViewId());

        //
        textView.setText(name);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        textView.setTextColor(getResources().getColor(color));

        // custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "adamina.ttf");
        textView.setTypeface(font);

        // add the textview to the layout and return its position
        mRelativeLayout.addView(textView);
        return textView.getId();

    }

}
