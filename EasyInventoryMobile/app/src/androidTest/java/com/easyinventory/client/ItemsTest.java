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
import static com.easyinventory.client.TestUtils.clickItem;
import static com.easyinventory.client.TestUtils.clickUnit;
import static com.easyinventory.client.TestUtils.gotoItems;
import static com.easyinventory.client.TestUtils.withItemContent;
import static org.hamcrest.CoreMatchers.not;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.Item;
import easyinventoryapi.Unit;
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemsTest {

    private final Item created;
    private final Item modified;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        Unit m=new Unit("m","Méter");
        return Arrays.asList(new Object[][]{
                {
                        new Item("test_item"+RANDOM,"Test item",m),
                        new Item("test_item2"+RANDOM,"Test item2",m)
                },
                {
                        new Item("特别的"+RANDOM,"Test item",m),
                        new Item("test_item特别的"+RANDOM,"特别的",m)
                }
        });
    }

    public ItemsTest(Item created,Item modified) {
        this.created=created;
        this.modified=modified;
    }


    public static void createItem(Item i,String screenshot,String select_screenshot) {
        gotoItems();
        onView(withId(R.id.add)).perform(click());

        fillEditDialog(i,screenshot,select_screenshot);
        clickItem(i);
    }
    public static void modifyItem(Item i,Item new_i,String screenshot,String select_screenshot) {
        gotoItems();
        clickItem(i);
        onView(withId(R.id.edit)).perform(click());

        fillEditDialog(new_i,screenshot,select_screenshot);
        clickItem(new_i);
    }
    public static void deleteItem(Item i,String screenshot) {
        gotoItems();
        clickItem(i);

        onView(withId(R.id.delete)).perform(click());
        if(screenshot!=null)
            screenshot(screenshot);
        onView(withId(R.id.accept_button)).perform(click());

        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onView(withId(R.id.list)).check(matches(not(withData(withItemContent(i)))));
    }

    public static void fillEditDialog(Item i,String screenshot,String select_screenshot) {
        onView(withId(R.id.editId)).perform(replaceText(i.id));
        onView(withId(R.id.editName)).perform(replaceText(i.name));

        if(screenshot!=null)
            screenshot(screenshot);

        onView(withId(R.id.select_unit)).perform(click());

        sleep();

        clickUnit(i.unit);
        if(select_screenshot!=null)
            screenshot(select_screenshot);

        onView(withId(R.id.selectButton)).perform(click());
        sleep();
        onView(withId(R.id.editConfirm)).perform(click());
    }

    @Test
    public void BA_addItem() {
        LoginTest.doLogin();
        createItem(created,null,null);
    }

    @Test
    public void CA_editItem() {
        LoginTest.doLogin();
        modifyItem(created,modified,null,null);
    }

    @Test
    public void DA_deleteItem() {
        LoginTest.doLogin();
        deleteItem(modified,null);
    }

}
