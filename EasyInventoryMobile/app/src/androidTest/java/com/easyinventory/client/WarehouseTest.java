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
import static com.easyinventory.client.TestUtils.clickWarehouse;
import static com.easyinventory.client.TestUtils.gotoWarehouses;
import static com.easyinventory.client.TestUtils.withWarehouseContent;
import static org.hamcrest.CoreMatchers.not;

import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.Warehouse;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WarehouseTest {

    private final Warehouse created;
    private final Warehouse modified;


    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {
                    new Warehouse("test_warehouse"+RANDOM,"Test Warehouse","fake street"),
                    new Warehouse("test_modified_warehouse"+RANDOM,"Test Warehouse (modified)","new street")
            },
            {
                    new Warehouse("test warehouse"+RANDOM,"Warehouse with ★special★ characters","★★★★★ hotel"),
                    new Warehouse("test/\\/ /../ warehouse"+RANDOM,"★★ Even more special // ","★")
            },
            {
                    new Warehouse("特别的"+RANDOM,"特别的 ★★","★★★★★ 特别的"),
                    new Warehouse("特别的?q="+RANDOM,"特别的 ★★ 特别的","★ 特别的")
            }
        });
    }

    public WarehouseTest(Warehouse created,Warehouse modified) {
        this.created=created;
        this.modified=modified;
    }

    public static void fillEditDialog(Warehouse w, String screenshot) {
        onView(withId(R.id.editId)).perform(replaceText(w.id));
        onView(withId(R.id.editName)).perform(replaceText(w.name));
        onView(withId(R.id.editAddress)).perform(replaceText(w.address));

        if(screenshot!=null)
            screenshot(screenshot);

        onView(withId(R.id.editConfirm)).perform(click());
    }

    public static void createWarehouse(Warehouse w,String screenshot) {
        gotoWarehouses();
        onView(withId(R.id.add)).perform(click());
        fillEditDialog(w,screenshot);
        clickWarehouse(w);
    }

    public static void modifyWarehouse(Warehouse w,Warehouse new_w,String screenshot) {
        gotoWarehouses();
        clickWarehouse(w);
        onView(withId(R.id.edit)).perform(click());
        fillEditDialog(new_w,screenshot);
        clickWarehouse(new_w);
    }

    public static void deleteWarehouse(Warehouse w,String screenshot) {
        gotoWarehouses();
        clickWarehouse(w);

        onView(withId(R.id.delete)).perform(click());
        if(screenshot!=null)
            screenshot(screenshot);
        onView(withId(R.id.accept_button)).perform(click());

        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onView(withId(R.id.list))
                .check(matches(not(withData(withWarehouseContent(w)))));
    }

    @Test
    public void BA_add() {
        LoginTest.doLogin();
        createWarehouse(created,null);
    }

    @Test
    public void CA_edit() {
        LoginTest.doLogin();
        gotoWarehouses();

        try {
            clickWarehouse(created);
        }catch(Exception e) {
            Assume.assumeTrue(false);
        }

        onView(withId(R.id.edit)).perform(click());
        fillEditDialog(modified,null);
        clickWarehouse(modified);
    }

    @Test
    public void DA_delete() {
        LoginTest.doLogin();
        gotoWarehouses();

        try{
            clickWarehouse(modified);
        }catch(Exception e) {
            Assume.assumeTrue(false);
        }

        deleteWarehouse(modified,null);
    }

}
