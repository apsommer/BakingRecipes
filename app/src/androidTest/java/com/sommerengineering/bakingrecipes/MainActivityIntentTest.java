package com.sommerengineering.bakingrecipes;

import static android.app.Instrumentation.ActivityResult;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

import android.app.Activity;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// AndroidJUnitRunner is the default test runner
@RunWith(AndroidJUnit4.class)
public class MainActivityIntentTest {

    // rule provides functional testing of an Intent for a single activity
    @Rule
    public IntentsTestRule<MainActivity> mMainActivityRule =
            new IntentsTestRule<>(MainActivity.class);

    // block all external intents
    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(
                new ActivityResult(Activity.RESULT_OK, null));
    }

    // test that clicking on a recycler item generates an Intent for the DetailActivity
    @Test
    public void clickNextArrow_SendsIntentForPlayerActivity() {
        onView(withId(R.id.rv_recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(hasComponent(DetailActivity.class.getName()));
    }
}
