package com.easyinventory.client;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.easyinventory.client.TestSuite.RANDOM;
import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestSuite.sleep;
import static com.easyinventory.client.TestSuite.withData;
import static com.easyinventory.client.TestUtils.clickOperation;
import static com.easyinventory.client.TestUtils.gotoOperations;
import static com.easyinventory.client.TestUtils.selectItemPopup;
import static com.easyinventory.client.TestUtils.selectStoragePopup;
import static com.easyinventory.client.TestUtils.withOperationId;
import static org.hamcrest.CoreMatchers.not;

import org.junit.Assume;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;
import easyinventoryapi.Warehouse;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OperationTest {

    private final Warehouse warehouse;
    private final Operation operation;

    /** @noinspection unused*/
    public OperationTest(Warehouse warehouse, Operation operation) {
        this.warehouse = warehouse;
        this.operation = operation;
    }

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        Warehouse WH1=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");

        return Arrays.asList(new Object[][]{
                {
                        WH1,
                        new Operation("test_OP"+RANDOM,"test op 1",true,new OperationItem[]{})
                }
        });
    }

    public static void viewOperation(Warehouse wh,Operation o, String screenshot) {
        gotoOperations(wh);
        clickOperation(o);
        onView(withId(R.id.view)).perform(click());
        if(screenshot!=null)
            screenshot(screenshot);

        onView(isRoot()).perform(pressBack());
    }

    public static void createOperation(Warehouse wh,Operation o, String screenshot,String item_select_screenshot,String storage_select_screenshot,String item_screenshot) {
        gotoOperations(wh);
        onView(withId(R.id.add)).perform(click());
        fillEditDialog(o,screenshot,item_select_screenshot,storage_select_screenshot,item_screenshot);
        clickOperation(o);
    }

    public static void cancelOperation(Warehouse wh,Operation o,String screenshot) {
        gotoOperations(wh);
        clickOperation(o);
        onView(withId(R.id.delete)).perform(click());
        if(screenshot!=null)
            screenshot(screenshot);
        onView(withId(R.id.accept_button)).perform(click());
        sleep();
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onView(withId(R.id.list))
                .check(matches(not(withData(withOperationId(o.id)))));
    }

    public static void commitOperation(Warehouse wh,Operation o,String screenshot) {
        gotoOperations(wh);
        clickOperation(o);
        onView(withId(R.id.commit)).perform(click());
        if(screenshot!=null)
            screenshot(screenshot);
        onView(withId(R.id.accept_button)).perform(click());
        sleep();
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onView(withId(R.id.list))
                .check(matches(not(withData(withOperationId(o.id)))));
    }

    public static void fillEditDialog(Operation o,String screenshot,String item_select_screenshot,String storage_select_screenshot,String item_screenshot) {
        if(screenshot!=null)
            screenshot(screenshot);

        onView(withId(R.id.editId)).perform(replaceText(o.id));
        onView(withId(R.id.editName)).perform(replaceText(o.name));
        if(o.is_add)
            onView(withId(R.id.is_add)).perform(click());

        for(OperationItem i:o.items) {
            onView(withId(R.id.add)).perform(click());
            sleep();

            if(item_screenshot!=null) {
                screenshot(item_screenshot);
                item_screenshot=null;
            }

            onView(withId(R.id.select_type)).perform(click());
            sleep();
            if(item_select_screenshot!=null) {
                screenshot(item_select_screenshot);
                item_select_screenshot=null;
            }
            selectItemPopup(i.item);

            if(i.storage!=null) {
                onView(withId(R.id.select_storage)).perform(click());
                sleep();
                if(storage_select_screenshot!=null) {
                    screenshot(storage_select_screenshot);
                    storage_select_screenshot=null;
                }
                selectStoragePopup(i.storage);
            }

            if(i.global_serial!=0) {
                onView(withId(R.id.serial_enable)).perform(click());
                onView(withId(R.id.manufacturer_serial)).perform(replaceText(i.manufacturer_serial == null ? "" : i.manufacturer_serial));
                onView(withId(R.id.serial)).perform(replaceText("" + i.global_serial));
            }else {
                onView(withId(R.id.amount)).perform(replaceText("" + i.amount));
            }

            onView(withId(R.id.lot)).perform(replaceText(i.lot==null?"":i.lot));

            onView(withId(R.id.editConfirm)).perform(click());
            sleep();
        }
        sleep();
        onView(withId(R.id.editConfirm)).perform(click());
        sleep();
        clickOperation(o);
    }

    @Test
    public void AA_add() {
        LoginTest.doLogin();
        createOperation(warehouse,operation,null,null,null,null);
    }

    @Test
    public void BA_cancel() {
        LoginTest.doLogin();
        gotoOperations(warehouse);

        try{
            clickOperation(operation);
        }catch(Exception e) {
            Assume.assumeTrue(false);
        }

        cancelOperation(warehouse,operation,null);
    }
}
