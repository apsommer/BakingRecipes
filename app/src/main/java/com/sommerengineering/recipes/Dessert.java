package com.sommerengineering.recipes;

import java.io.Serializable;
import java.util.ArrayList;

// POJO holds metadata for a dessert recipe
public class Dessert implements Serializable {

    // base attributes
    private final int mId;
    private final String mName;
    private final int mServings;
    private final String mImagePath;

    // multiple ingredients
    private final ArrayList<Ingredient> mIngredients;

    // multiple steps
    private final ArrayList<Step> mSteps;

    // constructor
    Dessert(int id, String name, int servings, String imagePath,
            ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {

        // base attributes
        mId = id;
        mName = name;
        mServings = servings;
        mImagePath = imagePath;

        // multiple ingredients
        mIngredients =ingredients;

        // multiple steps
        mSteps = steps;
    }

    // getters
    int getId() {
        return mId;
    }
    String getName() {
        return mName;
    }
    int getServings() {
        return mServings;
    }
    String getImagePath() {
        return mImagePath;
    }
    ArrayList<Ingredient> getIngredients() {
        return mIngredients;
    }
    ArrayList<Step> getSteps() {
        return mSteps;
    }

}
