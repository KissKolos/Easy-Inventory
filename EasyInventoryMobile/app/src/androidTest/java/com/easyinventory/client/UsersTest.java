package com.easyinventory.client;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.easyinventory.client.AuthorizationTest.contains;
import static com.easyinventory.client.AuthorizationTest.nthChildOf;
import static com.easyinventory.client.TestSuite.*;
import static com.easyinventory.client.TestUtils.clickUser;
import static com.easyinventory.client.TestUtils.clickWarehouse;
import static com.easyinventory.client.TestUtils.gotoUsers;
import static com.easyinventory.client.TestUtils.withUserContent;


import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.LocalAuthorization;
import easyinventoryapi.SystemAuthorization;
import easyinventoryapi.User;
import easyinventoryapi.Warehouse;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersTest {

    private final User created;
    private final User modified;


    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        User admin=new User("admin","admin",null,null);
        return Arrays.asList(new Object[][]{
                {
                        new User("test_user"+RANDOM,"Test User","password",admin),
                        new User("test_user2"+RANDOM,"Test User2","password2",admin)
                },
                {
                        new User("test★user"+RANDOM,"Test 特别的","password",admin),
                        new User("特别的"+RANDOM,"d","password2",admin)
                }
        });
    }

    public UsersTest(User created,User modified) {
        this.created=created;
        this.modified=modified;
    }

    public static void createUser(User u, String screenshot,String select_screenshot) {
        gotoUsers();
        onView(withId(R.id.add)).perform(click());
        fillEditDialog(u,screenshot,select_screenshot);
        clickUser(u);
    }

    public static void modifyUser(User u,User new_u, String screenshot,String select_screenshot) {
        gotoUsers();
        clickUser(u);
        onView(withId(R.id.edit)).perform(click());

        fillEditDialog(new_u,screenshot,select_screenshot);
        clickUser(new_u);
    }

    public static void deleteUser(User u,String screenshot) {
        gotoUsers();
        clickUser(u);

        onView(withId(R.id.delete)).perform(click());
        if(screenshot!=null)
            screenshot(screenshot);
        onView(withId(R.id.accept_button)).perform(click());

        onView(withId(R.id.swipe)).perform(swipeDown());
        sleep();
        onView(withId(R.id.list))
                .check(matches(not(withData(withUserContent(u)))));
    }

    public static void setUserLocalAuthorization(User user, Warehouse warehouse, String[] local_authorizations, String select_screenshot, String form_screenshot) {
        gotoUsers();
        clickUser(user);
        onView(withId(R.id.editLocalAuthorization)).perform(click());

        clickWarehouse(warehouse);
        if(select_screenshot!=null)
            screenshot(select_screenshot);
        onView(withId(R.id.selectButton)).perform(click());
        sleep();

        if(form_screenshot!=null)
            screenshot(form_screenshot);

        for(int i = 0; i< LocalAuthorization.AUTHORIZATIONS.length; i++) {
            if(contains(local_authorizations, LocalAuthorization.AUTHORIZATIONS[i]))
                onView(allOf(isDescendantOfA(withId(R.id.list)), nthChildOf(i,withId(R.id.list)),withId(R.id.check))).perform(click());
        }
        onView(withId(R.id.editConfirm)).perform(click());
        sleep();
    }

    public static void setUserSystemAuthorization(User user,String[] system_authorizations,String form_screenshot) {
        gotoUsers();
        clickUser(user);
        onView(withId(R.id.editAuthorization)).perform(click());
        sleep();

        if(form_screenshot!=null)
            screenshot(form_screenshot);

        for(int i = 0; i< SystemAuthorization.AUTHORIZATIONS.length; i++) {
            if(contains(system_authorizations, SystemAuthorization.AUTHORIZATIONS[i]))
                onView(allOf(isDescendantOfA(withId(R.id.list)), nthChildOf(i,withId(R.id.list)),withId(R.id.check))).perform(click());
        }
        onView(withId(R.id.editConfirm)).perform(click());
        sleep();
    }

    public static void fillEditDialog(User u, String screenshot,String select_screenshot) {
        onView(withId(R.id.editId)).perform(replaceText(u.id));
        onView(withId(R.id.editName)).perform(replaceText(u.name));
        onView(withId(R.id.editPasswordCheck)).perform(click());
        onView(withId(R.id.editPassword)).perform(replaceText(u.password));

        if(screenshot!=null)
            screenshot(screenshot);

        onView(withId(R.id.editManager)).perform(click());

        sleep();

        clickUser(u.manager);
        if(select_screenshot!=null)
            screenshot(select_screenshot);
        onView(withId(R.id.selectButton)).perform(click());

        sleep();

        onView(withId(R.id.editConfirm)).perform(click());
    }

    @Test
    public void BA_addUser() {
        LoginTest.doLogin();
        createUser(created,null,null);
    }

    @Test
    public void CA_editUser() {
        LoginTest.doLogin();
        modifyUser(created,modified,null,null);
    }

    @Test
    public void DA_deleteUser() {
        LoginTest.doLogin();
        deleteUser(modified,null);
    }

}
