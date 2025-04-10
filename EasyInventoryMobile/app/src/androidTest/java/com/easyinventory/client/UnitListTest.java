package com.easyinventory.client;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestSuite.sleep;
import static com.easyinventory.client.TestUtils.gotoUnits;
import static com.easyinventory.client.TestUtils.withUnitContent;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import easyinventoryapi.Unit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnitListTest {

    @Test
    public void AA_listUnits() {
        LoginTest.doLogin();
        gotoUnits();

        onView(withId(R.id.swipe)).perform(swipeDown());

        sleep();

        onData(withUnitContent(new Unit("m","Méter"))).perform(click());
        screenshot("list-units.png");
    }

    @Test
    public void AB_pageUnits() {
        LoginTest.doLogin();
        gotoUnits();

        onView(withId(R.id.swipe)).perform(swipeDown());

        sleep();

        onData(withUnitContent(new Unit("m","Méter"))).perform(click());
        onView(withId(R.id.next_button)).perform(click());
        sleep();
        onData(withUnitContent(new Unit("container","Konténer"))).perform(click());
        onView(withId(R.id.prev_button)).perform(click());
        sleep();
        onData(withUnitContent(new Unit("m","Méter"))).perform(click());
    }

}
