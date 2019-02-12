package com.sommerengineering.bakingrecipes;

import java.io.Serializable;

// POJO holds metadata for a ingredient to a dessert
public class Ingredient implements Serializable {

    // base attributes
    private final int mId;
    private final String mName;
    private final int mQuantity;
    private final String mMeasure;

    // constructor
    Ingredient(int id, String name, int quantity, String measure) {

        // base attributes
        mId = id;
        mName = name;
        mQuantity = quantity;
        mMeasure = measure;
    }

    // getters
    int getId() {
        return mId;
    }
    String getName() {
        return mName;
    }
    int getQuantity() {
        return mQuantity;
    }
    String getMeasure() {
        return mMeasure;
    }

}