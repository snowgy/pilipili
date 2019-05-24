package com.example.pilipili;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AlbumTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void albumTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.user_name),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText.perform(scrollTo(), replaceText("root"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.input_password),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.design.widget.TextInputLayout")),
                                        0),
                                0)));
        appCompatEditText2.perform(scrollTo(), replaceText("1234"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataInteraction relativeLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.gridView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)))
                .atPosition(0);
        relativeLayout.perform(click());

        ViewInteraction fixedBottomNavigationTab = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.bottom_navigation_bar_item_container),
                                childAtPosition(
                                        withId(R.id.bottom_navigation_bar_container),
                                        1)),
                        1),
                        isDisplayed()));
        fixedBottomNavigationTab.perform(click());

        pressBack();

        DataInteraction relativeLayout2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.gridView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)))
                .atPosition(1);
        relativeLayout2.perform(click());

        ViewInteraction fixedBottomNavigationTab2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.bottom_navigation_bar_item_container),
                                childAtPosition(
                                        withId(R.id.bottom_navigation_bar_container),
                                        1)),
                        1),
                        isDisplayed()));
        fixedBottomNavigationTab2.perform(click());

        pressBack();

        DataInteraction relativeLayout3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.gridView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)))
                .atPosition(8);
        relativeLayout3.perform(click());

        ViewInteraction fixedBottomNavigationTab3 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.bottom_navigation_bar_item_container),
                                childAtPosition(
                                        withId(R.id.bottom_navigation_bar_container),
                                        1)),
                        1),
                        isDisplayed()));
        fixedBottomNavigationTab3.perform(click());

        ViewInteraction fixedBottomNavigationTab4 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.bottom_navigation_bar_item_container),
                                childAtPosition(
                                        withId(R.id.bottom_navigation_bar_container),
                                        1)),
                        0),
                        isDisplayed()));
        fixedBottomNavigationTab4.perform(click());

        ViewInteraction shiftingBottomNavigationTab = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.bottom_navigation_bar_item_container),
                                childAtPosition(
                                        withId(R.id.bottom_navigation_bar_container),
                                        1)),
                        4),
                        isDisplayed()));
        shiftingBottomNavigationTab.perform(click());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                        childAtPosition(
                                withClassName(is("android.widget.FrameLayout")),
                                0)))
                .atPosition(1);
        appCompatTextView.perform(click());

        ViewInteraction shiftingBottomNavigationTab2 = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.bottom_navigation_bar_item_container),
                                childAtPosition(
                                        withId(R.id.bottom_navigation_bar_container),
                                        1)),
                        4),
                        isDisplayed()));
        shiftingBottomNavigationTab2.perform(click());

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                        childAtPosition(
                                withClassName(is("android.widget.FrameLayout")),
                                0)))
                .atPosition(0);
        appCompatTextView2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
