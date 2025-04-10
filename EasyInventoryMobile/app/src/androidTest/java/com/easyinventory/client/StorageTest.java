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
import static com.easyinventory.client.TestUtils.clickStorage;
import static com.easyinventory.client.TestUtils.gotoStorages;
import static com.easyinventory.client.TestUtils.withStorageContent;
import static org.hamcrest.CoreMatchers.not;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.Storage;
import easyinventoryapi.Warehouse;
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageTest {

    private final Storage created;
    private final Storage modified;


    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        Warehouse WH=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        return Arrays.asList(new Object[][]{
                {
                        new Storage(WH,"test_storage1"+RANDOM,"Storage 1"),
                        new Storage(WH,"test_storage2"+RANDOM,"test storage2")
                },
                {
                        new Storage(WH,"特别的"+RANDOM,"Storage 1"),
                        new Storage(WH,"test_storage2特别的"+RANDOM,"特别的")
                }
        });
    }

    public StorageTest(Storage created,Storage modified) {
        this.created=created;
        this.modified=modified;
    }

    public static void createStorage(Storage st, String screenshot) {
        gotoStorages(st.warehouse);
        onView(withId(R.id.add)).perform(click());
        fillEditDialog(st,screenshot);
        clickStorage(st);
    }

    public static void modifyStorage(Storage st,Storage new_st,String screenshot) {
        gotoStorages(st.warehouse);
        clickStorage(st);
        onView(withId(R.id.edit)).perform(click());

        fillEditDialog(new_st,screenshot);
        clickStorage(new_st);
    }

    public static void deleteStorage(Storage st,String screenshot) {
        gotoStorages(st.warehouse);
        clickStorage(st);

        onView(withId(R.id.delete)).perform(click());
        if(screenshot!=null)
            screenshot(screenshot);
        onView(withId(R.id.accept_button)).perform(click());

        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onView(withId(R.id.list))
                .check(matches(not(withData(withStorageContent(st)))));
    }

    public static void fillEditDialog(Storage s, String screenshot) {
        onView(withId(R.id.editId)).perform(replaceText(s.id));
        onView(withId(R.id.editName)).perform(replaceText(s.name));

        if(screenshot!=null)
            screenshot(screenshot);

        onView(withId(R.id.editConfirm)).perform(click());
    }

    @Test
    public void BA_add() {
        LoginTest.doLogin();
        createStorage(created,null);
    }

    @Test
    public void CA_edit() {
        LoginTest.doLogin();
        modifyStorage(created,modified,null);
    }

    @Test
    public void DA_delete() {
        LoginTest.doLogin();
        deleteStorage(modified,null);
    }

}
