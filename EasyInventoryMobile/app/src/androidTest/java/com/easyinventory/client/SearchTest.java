package com.easyinventory.client;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestSuite.sleep;
import static com.easyinventory.client.TestUtils.clickStorage;
import static com.easyinventory.client.TestUtils.clickWarehouse;
import static org.hamcrest.CoreMatchers.allOf;

import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.Item;
import easyinventoryapi.ItemStack;
import easyinventoryapi.Storage;
import easyinventoryapi.Unit;
import easyinventoryapi.Warehouse;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchTest {

    private final Warehouse warehouse;
    private final Storage storage;
    private final String query;
    private final boolean warehouse_q;
    private final boolean storage_q;
    private final boolean lot_q;
    private final boolean serial_q;

    private final int page_next;

    private final int page_prev;

    private final ItemStack target;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        Warehouse WH_ajka=new Warehouse("WH_ajka","Ajkai telephely","Ajka Eötvös út 57.");
        Warehouse WH_fot=new Warehouse("WH_fot","Fóti telephely","Fót Szent István utca 48.");

        Storage ST_fot1=new Storage(WH_fot,"FOT0","Fót 1/1");
        Storage ST_ajka1=new Storage(WH_ajka,"AJK0","Ajka 1/1");
        Storage ST_ajka2=new Storage(WH_ajka,"AJK21","Ajka 4/4");
        Storage ST_ajka3=new Storage(WH_ajka,"AJK47","Ajka 8/6");

        Item IT1=new Item("steel_wire_12","Acélkábel 12mm",new Unit("m","Méter"));
        Item IT2=new Item("steel_wire_3","Acélkábel 3mm",new Unit("m","Méter"));
        Item IT3=new Item("petrol_95","Benzin (95)",new Unit("l","Liter"));
        Item IT4=new Item("beans","Bab",new Unit("tincan","Konzervdoboz"));
        Item IT5=new Item("suv_car","SUV autó",new Unit("unit","Darab"));

        return Arrays.asList(new Object[][]{
                {
                        null,
                        null,
                        "",
                        true,
                        true,
                        true,
                        true,
                        0,0,
                        new ItemStack(WH_ajka,ST_ajka1,IT1,
                                null,null,16,16,0)
                },
                {
                        null,
                        null,
                        "",
                        true,
                        true,
                        true,
                        true,
                        2,3,
                        new ItemStack(WH_ajka,ST_ajka2,IT4,
                                "Lot_9_1",null,21,21,0)
                },
                {
                        null,
                        null,
                        "SUV",
                        true,
                        true,
                        true,
                        true,
                        0,0,
                        new ItemStack(WH_ajka,ST_ajka3,IT5,
                                null,"40817",1,1,5)
                },
                {
                        WH_fot,
                        null,
                        "acél",
                        false,
                        false,
                        false,
                        false,
                        0,0,
                        new ItemStack(null,null,IT2,
                                null,null,112,90,0)
                },
                {
                        WH_fot,
                        ST_fot1,
                        "",
                        false,
                        false,
                        false,
                        false,
                        0,0,
                        new ItemStack(null,null,IT3,
                                null,null,21,6,0)
                }
        });
    }

    /** @noinspection unused*/
    public SearchTest(Warehouse warehouse, Storage storage, String query, boolean warehouseQ, boolean storageQ, boolean lotQ, boolean serialQ,int page_prev,int page_next, ItemStack target) {
        this.warehouse = warehouse;
        this.storage = storage;
        this.query = query;
        warehouse_q = warehouseQ;
        storage_q = storageQ;
        lot_q = lotQ;
        serial_q = serialQ;
        this.page_prev=page_prev;
        this.page_next=page_next;
        this.target = target;
    }

    public static void gotoSearch() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(allOf(isDescendantOfA(withId(R.id.nav_view)),withText(R.string.tab_search))).perform(click());
    }

    static Matcher<View> withItemStackRow(ItemStack i) {
        return new TypeSafeMatcher<>(){
            @Override
            public void describeTo(Description description) {}

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TableRow)) {
                    return false;
                }
                TableRow currentRow = (TableRow) view;

                String wh=((TextView)currentRow.findViewById(R.id.warehouse)).getText().toString();
                String st=((TextView)currentRow.findViewById(R.id.storage)).getText().toString();
                String item=((TextView)currentRow.findViewById(R.id.item)).getText().toString();
                String lot=((TextView)currentRow.findViewById(R.id.lot)).getText().toString();
                String serial=((TextView)currentRow.findViewById(R.id.serial)).getText().toString();
                String amount=((TextView)currentRow.findViewById(R.id.amount)).getText().toString();
                String available=((TextView)currentRow.findViewById(R.id.available)).getText().toString();

                return wh.equals(i.warehouse==null?"":i.warehouse.name)&&
                        st.equals(i.storage==null?"":i.storage.name)&&
                        item.equals(i.item.name)&&
                        lot.equals(i.lot==null?"":i.lot)&&
                        serial.equals(i.global_serial==0?"":""+i.global_serial)&&
                        amount.equals(""+i.amount)&&
                        available.equals(""+i.available_amount);
            }};
    }

    public static void search(Warehouse warehouse,
                              Storage storage,
                              String query,
                              boolean warehouse_q,
                              boolean storage_q,
                              boolean lot_q,
                              boolean serial_q,
                              String screenshot) {
        gotoSearch();

        if(warehouse!=null) {
            onView(withId(R.id.select_warehouse)).perform(click());
            clickWarehouse(warehouse);
            onView(withId(R.id.selectButton)).perform(click());
        }
        if(storage!=null) {
            onView(withId(R.id.select_storage)).perform(click());
            clickStorage(storage);
            onView(withId(R.id.selectButton)).perform(click());
        }
        onView(withId(R.id.query)).perform(replaceText(query));

        if(warehouse_q)
            onView(withId(R.id.toggle_warehouse)).perform(click());

        if(storage_q)
            onView(withId(R.id.toggle_storage)).perform(click());

        if(lot_q)
            onView(withId(R.id.toggle_lot)).perform(click());

        if(serial_q)
            onView(withId(R.id.toggle_serial)).perform(click());

        onView(withId(R.id.search_button)).perform(click());

        sleep();

        if(screenshot!=null)
            screenshot(screenshot);
    }

    @Test
    public void AA_search() {
        LoginTest.doLogin();
        search(warehouse,storage,query,warehouse_q,storage_q,lot_q,serial_q,null);
        for(int i=0;i<page_next;i++) {
            onView(withId(R.id.next_button)).perform(click());
        }

        for(int i=0;i<page_prev;i++) {
            onView(withId(R.id.prev_button)).perform(click());
        }

        onView(withItemStackRow(target)).perform(click());
    }
}
