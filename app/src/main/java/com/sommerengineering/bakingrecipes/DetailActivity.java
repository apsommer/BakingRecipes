package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

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
    @BindView(R.id.tv_name) TextView mNameTv;
    @BindView(R.id.tv_servings) TextView mServingsTv;
    @BindView(R.id.rl_ingredients_container) RelativeLayout mIngredientsContainer;
    @BindView(R.id.rl_steps_container) RelativeLayout mStepsContainer;

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
        setTitles();

        // method iterates through an ArrayList<Ingredient> and dynamically creates views
        setIngredients(mDessert.getIngredients(), R.id.ingredients_divider);

        // method iterates through an ArrayList<Step> and dynamically creates views
        setSteps(mDessert.getSteps(), R.id.steps_divider);

    }

    // simple method to bind title textviews
    private void setTitles() {

        // get basic attributes of this dessert
        String name = mDessert.getName();
        int servings = mDessert.getServings();

        // set textviews for title and servings
        mNameTv.setText(name);
        mServingsTv.setText(String.valueOf(servings));
    }

    // bind Ingredient data to dynamically created textviews
    private void setIngredients(ArrayList<Ingredient> ingredients, int position) {

        // loop through all Ingredients in the list
        for (int i = 0; i < ingredients.size(); i++) {

            // get the current Ingredient and extract its basic attributes
            Ingredient ingredient = ingredients.get(i);
            final String name = StringUtils.capitalize(ingredient.getName().toLowerCase());
            final String quantity = String.valueOf(ingredient.getQuantity()) + " ";
            final String measure = ingredient.getMeasure().toLowerCase();

            // create a textview for each attribute; the position of the new view is returned
            position = createTextView(mIngredientsContainer, name, position, 18, "BELOW", R.color.black);
            position = createTextView(mIngredientsContainer, quantity, position, 12, "BELOW", R.color.gray);
            position = createTextView(mIngredientsContainer, measure, position, 12, "RIGHT_OF", R.color.gray);
        }
    }

    // bind Step data to dynamically created textviews
    private void setSteps(ArrayList<Step> steps, int position) {

        // loop through all Ingredients in the list
        for (int i = 0; i < steps.size(); i++) {

            // get the current Ingredient and extract its basic attributes
            Step step = steps.get(i);

            final String id = String.valueOf(step.getId());
            final String shortDescription = step.getShortDescription();
            final String description = step.getDescription();
            final String videoPath = step.getVideoPath();
            final String thumbnailPath = step.getThumbnailPath();

            // concatenate id and shortDescription for button
            final String buttonText = " " + id + ". " + shortDescription;

            // if an image or video exists for this step TODO ...
            if (!videoPath.isEmpty() || !thumbnailPath.isEmpty()) {

                position = createButton(mStepsContainer, buttonText, position, R.drawable.play, step);

            // no image or video exists for this step
            }
            else {

                // create a textview for each attribute; the position of the new view is returned
//                position = createTextView(mStepsContainer, id, position, 18, "BELOW", R.color.black);
//                position = createTextView(mStepsContainer, shortDescription, position, 18, "RIGHT_OF", R.color.black);
            }

        }
    }

    // creates a new TextView with the given parameters and returns its position (ID)
    private int createTextView(ViewGroup container, String text, int position, int textSize, String alignment, int color) {

        // new TextView
        TextView textView = new TextView(mContext);

        // layout size and position are defined in LayoutParams
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (alignment.equals("BELOW")) {
            layoutParams.addRule(RelativeLayout.BELOW, position);
        }
        else if (alignment.equals("RIGHT_OF")) {
            layoutParams.addRule(RelativeLayout.RIGHT_OF, position);
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, position);
        }

        // bind these layout parameters to the textview
        textView.setLayoutParams(layoutParams);

        // have the Android system create a unique ID
        textView.setId(View.generateViewId());

        // set text size and color
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        textView.setTextColor(getResources().getColor(color));

        // custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "adamina.ttf");
        textView.setTypeface(font);

        // add the textview to the container layout and return its position for the next View
        container.addView(textView);
        return textView.getId();
    }

    // creates a new ImageView with the given parameters and returns its position (ID)
    private int createButton(ViewGroup container, String text, int position, int drawable, Step step) {

        // new Button
        Button button = new Button(mContext);

        // layout size and position are defined in LayoutParams
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, position);

        // bind these layout parameters to the imagebutton
        button.setLayoutParams(layoutParams);

        // have the Android system create a unique ID
        button.setId(View.generateViewId());

        // TODO fix ripple and alignment
        button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(drawable), null, null, null);
        button.setText(text);

        button.setAllCaps(false);

        // custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "adamina.ttf");
        button.setTypeface(font);

        // TODO set listener with Step as extra in intent to open new PlayerActivity

        // add the textview to the container layout and return its position for the next View
        container.addView(button);
        return button.getId();

    }

}
