package com.sommerengineering.recipes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {

    // member variables
    private Context mContext;
    private Dessert mDessert;
    private OnStepClickListener mCallback;

    // bind views using Butterknife library
    @BindView(R.id.tv_name) TextView mNameTv;
    @BindView(R.id.tv_servings) TextView mServingsTv;
    @BindView(R.id.rl_ingredients_container) RelativeLayout mIngredientsContainer;
    @BindView(R.id.rl_steps_container) RelativeLayout mStepsContainer;

    // communication conduit between the host activity and this fragment
    // triggered on the step button clicks
    public interface OnStepClickListener {
        void onStepSelected(Dessert dessert, int stepId);
    }

    // required empty constructor
    public DetailFragment() {}

    // cast check ensures that the host activity has implemented the callback interface
    // Android calls onAttach() before onCreateView()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // attempt to cast the activity context into the interface object
        // as successful cast associates mCallback to the host activity
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle bundle) {

        // reference to activity context
        mContext = getContext();

        // extract the dessert from the passed bundle
        mDessert = (Dessert) getArguments().getSerializable(MainActivity.SELECTED_DESSERT);

        // inflate the fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // initialize the Butterknife library
        ButterKnife.bind(this, rootView);

        // simple method to bind title textviews for dessert name and number of servings
        setTitles();

        // method iterates through an ArrayList<Ingredient> and dynamically creates views
        setIngredients(mDessert.getIngredients(), R.id.ingredients_divider);

        // method iterates through an ArrayList<Step> and dynamically creates views
        setSteps(mDessert.getSteps(), R.id.steps_divider);

        // return the inflated view
        return rootView;
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
            position = createTextView(mIngredientsContainer, name, position,
                    18, "BELOW", 0, R.color.black);
            position = createTextView(mIngredientsContainer, quantity, position,
                    12, "BELOW", 0, R.color.gray);
            position = createTextView(mIngredientsContainer, measure, position,
                    12, "RIGHT_OF", 0, R.color.gray);
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
            final String buttonText = " " + id + ". " + shortDescription + " ";

            // presence of a video file affects the button color
            int buttonDrawable;
            if (videoPath != null && !videoPath.isEmpty()) {
                buttonDrawable = R.drawable.play;
            } else {
                buttonDrawable = R.drawable.play_gray;
            }
            // create a button for this step; the position of the new view is returned
            position = createButton(mStepsContainer, buttonText, position, buttonDrawable, i);
        }
    }

    // creates a new TextView with the given parameters and returns its position (ID)
    private int createTextView(ViewGroup container, String text, int position,
                               int textSize, String alignment, int margin, int color) {

        // new TextView
        TextView textView = new TextView(mContext);

        // layout size and position are defined in LayoutParams
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // set margins
        int marginPx = Utilities.dpToPx(mContext, margin);
        layoutParams.setMargins(0, marginPx, 0, marginPx);

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
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "adamina.ttf");
        textView.setTypeface(font);

        // add the textview to the container layout and return its position for the next View
        container.addView(textView);
        return textView.getId();
    }

    // creates a new Button with the given parameters and returns its position (ID)
    private int createButton(ViewGroup container, String text, int position,
                             int drawableId, final int stepId) {

        // new Button
        Button button = new Button(mContext);

        // layout size and position are defined in LayoutParams
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, position);

        // bind these layout parameters to the button
        button.setLayoutParams(layoutParams);

        // have the Android system create a unique ID
        button.setId(View.generateViewId());

        // set text attributes
        button.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        button.setText(text);
        button.setTextColor(getResources().getColor(R.color.black));
        button.setAllCaps(false);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        // custom font
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "adamina.ttf");
        button.setTypeface(font);

        // define drawable size in dp
        int buttonDimenPx = Utilities.dpToPx(mContext, 45);

        // convert drawable to a scaled bitmap
        Drawable drawable = getResources().getDrawable(drawableId);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap, buttonDimenPx, buttonDimenPx, true);
        drawable = new BitmapDrawable(getResources(), bitmap);

        // set the drawable and ripple effect on button press
        button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        RippleDrawable rippleDrawable = Utilities.getButtonRipple(mContext);
        button.setBackground(rippleDrawable);

        // set padding
        int paddingPx = Utilities.dpToPx(mContext, getResources().getDimension(R.dimen.detail_spacing)) / 2;
        button.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        // set a click listener who's click calls back into RecipeActivity
        // and starts StepFragment with the selected Dessert and the step ID
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onStepSelected(mDessert, stepId);
            }
        });

        // add the button to the container layout and return its position for the next View
        container.addView(button);
        return button.getId();
    }

}
