package com.easyinventory.client;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestUtils.clickStorage;
import static com.easyinventory.client.TestUtils.clickStorageLimit;
import static com.easyinventory.client.TestUtils.clickStorageLimitItem;
import static com.easyinventory.client.TestUtils.gotoStorages;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.Item;
import easyinventoryapi.Storage;
import easyinventoryapi.Unit;
import easyinventoryapi.Warehouse;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageLimitTest {

    private final Storage storage;
    private final Item item;
    private final int limit;


    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        Warehouse WH1=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        Storage ST1=new Storage(WH1,"BAJ1","Baja 1/2");
        Item IT1=new Item("steel_wire_40","Acélkábel 40mm",new Unit("m","Méter"));
        return Arrays.asList(new Object[][]{
                {
                        ST1,
                        IT1,
                        70
                }
        });
    }

    public StorageLimitTest(Storage storage, Item item, int limit) {
        this.storage=storage;
        this.item=item;
        this.limit=limit;
    }

    public static void limitStorage(Storage st, Item item,int limit,String screenshot,String edit_screenshot) {
        LoginTest.doLogin();
        gotoStorages(st.warehouse);
        clickStorage(st);
        onView(withId(R.id.editLimits)).perform(click());

        if(screenshot!=null)
            screenshot(screenshot);

        clickStorageLimitItem(item);
        onView(withId(R.id.edit)).perform(click());

        onView(withId(R.id.limit)).perform(replaceText(""+limit));
        if(edit_screenshot!=null)
            screenshot(edit_screenshot);
        onView(withId(R.id.editConfirm)).perform(click());

        clickStorageLimit(item,limit);
    }

    public static void checkStorageCapacity(Storage st, Item item,int amount,int limit,String screenshot) {
        /*gotoStorages(st.warehouse);
        clickStorage(st);
        onView(withId(R.id.editLimits)).perform(click());

        if(screenshot!=null)
            screenshot(screenshot);

        clickStorageLimitItem(item);
        onView(withId(R.id.edit)).perform(click());

        onView(withId(R.id.limit)).perform(replaceText(""+limit));
        if(edit_screenshot!=null)
            screenshot(edit_screenshot);
        onView(withId(R.id.editConfirm)).perform(click());

        clickStorageLimit(item,limit);*/
    }

    @Test
    public void AA_limit() {
        LoginTest.doLogin();
        limitStorage(storage,item,limit,null,null);
    }

}
