package com.easyinventory.client;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.easyinventory.client.TestSuite.RANDOM;
import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestSuite.sleep;
import static com.easyinventory.client.TestSuite.withData;
import static com.easyinventory.client.TestUtils.clickUnit;
import static com.easyinventory.client.TestUtils.gotoUnits;
import static com.easyinventory.client.TestUtils.withUnitContent;
import static org.hamcrest.CoreMatchers.not;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.Unit;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnitTest {

    private final Unit created;
    private final Unit modified;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        new Unit("test_unit"+RANDOM,"Test Unit"),
                        new Unit("test_unit2"+RANDOM,"Test Unit2")
                },
                {
                        new Unit("特别的"+RANDOM,"Test Unit"),
                        new Unit("test_unit2特别的"+RANDOM,"特别的")
                }
        });
    }

    public UnitTest(Unit created,Unit modified) {
        this.created=created;
        this.modified=modified;
    }

    public static void createUnit(Unit u, String screenshot) {
        gotoUnits();
        onView(withId(R.id.add)).perform(click());
        fillEditDialog(u,screenshot);
        clickUnit(u);
    }

    public static void modifyUnit(Unit u,Unit new_u, String screenshot) {
        gotoUnits();
        clickUnit(u);
        onView(withId(R.id.edit)).perform(click());
        fillEditDialog(new_u,screenshot);
        clickUnit(new_u);
    }

    public static void deleteUnit(Unit u,String screenshot) {
        gotoUnits();
        clickUnit(u);

        onView(withId(R.id.delete)).perform(click());
        if(screenshot!=null)
            screenshot(screenshot);
        onView(withId(R.id.accept_button)).perform(click());

        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onView(withId(R.id.list)).check(matches(not(withData(withUnitContent(u)))));
    }

    private static void fillEditDialog(Unit u,String screenshot) {
        onView(withId(R.id.editId)).perform(replaceText(u.id));
        onView(withId(R.id.editName)).perform(replaceText(u.name));

        if(screenshot!=null)
            screenshot(screenshot);

        onView(withId(R.id.editConfirm)).perform(click());

        sleep();
    }

    @Test
    public void BA_addUnit() {
        LoginTest.doLogin();
        createUnit(created,null);
    }

    @Test
    public void CA_editUnit() {
        LoginTest.doLogin();
        modifyUnit(created,modified,null);
    }

    @Test
    public void DA_deleteUnit() {
        LoginTest.doLogin();
        deleteUnit(modified,null);
    }

}
