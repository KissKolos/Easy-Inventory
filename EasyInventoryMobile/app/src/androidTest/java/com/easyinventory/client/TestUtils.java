package com.easyinventory.client;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.easyinventory.client.TestSuite.sleep;
import static org.hamcrest.CoreMatchers.allOf;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;


import easyinventoryapi.Item;
import easyinventoryapi.Operation;
import easyinventoryapi.Storage;
import easyinventoryapi.StorageLimit;
import easyinventoryapi.Unit;
import easyinventoryapi.User;
import easyinventoryapi.Warehouse;

public class TestUtils {

    public static Matcher<Object> withUnitContent(Unit u) {
        return new BoundedMatcher<>(Unit.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(Unit item) {
                return item.equals(u);
            }
        };
    }
    public static void gotoUnits() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(allOf(isDescendantOfA(withId(R.id.nav_view)),withText(R.string.tab_units))).perform(click());
    }

    public static Matcher<Object> withItemContent(Item u) {
        return new BoundedMatcher<>(Item.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(Item item) {
                return item.equals(u);
            }
        };
    }

    public static void gotoItems() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(allOf(isDescendantOfA(withId(R.id.nav_view)),withText(R.string.tab_items))).perform(click());
    }

    public static void clickUnit(Unit u) {
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(u.id));
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onData(withUnitContent(u)).perform(click());
    }

    public static Matcher<Object> withWarehouseContent(Warehouse u) {
        return new BoundedMatcher<>(Warehouse.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(Warehouse item) {
                return item.equals(u);
            }
        };
    }

    public static void gotoWarehouses() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(allOf(isDescendantOfA(withId(R.id.nav_view)),withText(R.string.tab_warehouses))).perform(click());
    }

    public static void clickWarehouse(Warehouse w) {
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(w.id));
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onData(withWarehouseContent(w)).perform(click());
    }

    public static void clickStorage(Storage s) {
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(s.id));
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onData(withStorageContent(s)).perform(click());
    }

    public static void gotoStorages() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(allOf(isDescendantOfA(withId(R.id.nav_view)),withText(R.string.tab_storages))).perform(click());
    }

    public static void gotoStorages(Warehouse w) {
        gotoStorages();

        onView(withId(R.id.warehouseSelect)).perform(click());
        sleep();
        clickWarehouse(w);
        onView(withId(R.id.selectButton)).perform(click());
    }

    public static Matcher<Object> withStorageContent(Storage u) {
        return new BoundedMatcher<>(Storage.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(Storage item) {
                return item.equals(u);
            }
        };
    }

    public static void gotoUsers() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(allOf(isDescendantOfA(withId(R.id.nav_view)),withText(R.string.tab_users))).perform(click());
    }

    public static Matcher<Object> withUserContent(User u) {
        return new BoundedMatcher<>(User.class) {
            @Override
            public void describeTo(Description description) {
            }

            private boolean checkWithoutPassword(User a,User b) {
                return a.id.equals(b.id)&&a.name.equals(b.name);
            }

            @Override
            protected boolean matchesSafely(User item) {
                return checkWithoutPassword(u,item)&&
                        ((u.manager==null)==(item.manager==null))&&
                        (u.manager==null||checkWithoutPassword(u.manager,item.manager));
            }
        };
    }

    public static void clickUser(User u) {
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(u.id));
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onData(withUserContent(u)).perform(click());
    }

    public static Matcher<Object> withOperationId(String id) {
        return new BoundedMatcher<>(Operation.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(Operation item) {
                return item.id.equals(id);
            }
        };
    }

    public static void gotoOperations() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(allOf(isDescendantOfA(withId(R.id.nav_view)),withText(R.string.tab_operations))).perform(click());
    }

    public static void gotoOperations(Warehouse w) {
        gotoOperations();

        onView(withId(R.id.warehouseSelect)).perform(click());
        sleep();
        clickWarehouse(w);
        onView(withId(R.id.selectButton)).perform(click());
    }

    public static void clickOperation(Operation o) {
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(o.id));
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onData(withOperationId(o.id)).perform(click());
    }

    public static void clickItem(Item i) {
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(i.id));
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onData(withItemContent(i)).perform(click());
    }

    public static void selectStoragePopup(Storage s) {
        clickStorage(s);
        onView(withId(R.id.selectButton)).perform(click());
        sleep();
    }

    public static void selectItemPopup(Item i) {
        clickItem(i);
        onView(withId(R.id.selectButton)).perform(click());
        sleep();
    }

    public static Matcher<Object> withStorageLimit(Item item,int limit) {
        return new BoundedMatcher<>(StorageLimit.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(StorageLimit sl_item) {
                return sl_item.item.equals(item)&&sl_item.amount==limit;
            }
        };
    }

    public static Matcher<Object> withStorageLimitItem(Item i) {
        return new BoundedMatcher<>(StorageLimit.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(StorageLimit item) {
                return item.item.equals(i);
            }
        };
    }

    public static void clickStorageLimit(Item item,int limit) {
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(item.id));
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onData(withStorageLimit(item,limit)).perform(click());
    }

    public static void clickStorageLimitItem(Item item) {
        onView(withId(R.id.search_bar)).perform(typeSearchViewText(item.id));
        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onData(withStorageLimitItem(item)).perform(click());
    }

    public static ViewAction typeSearchViewText(final String text){
        return new ViewAction(){
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(android.widget.SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((android.widget.SearchView) view).setQuery(text,false);
            }
        };
    }
}
