package com.easyinventory.client;

import androidx.test.core.app.ActivityScenario;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static com.easyinventory.client.TestSuite.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import easyinventoryapi.User;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest {
    public static void loginAs(User u) {
        loginAs(u,null);
    }

    public static void loginAs(User u,String screenshot) {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.editUserName)).perform(replaceText(u.id));
        onView(withId(R.id.editServer)).perform(replaceText("http://10.0.2.2:84/api"));
        onView(withId(R.id.editPassword)).perform(replaceText(u.password));

        if(screenshot!=null)
            screenshot(screenshot);

        onView(withId(R.id.loginButton)).perform(click());

        sleep();
    }

    @Test
    public void AA_loginFail() {
        ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.editUserName)).perform(replaceText("admin2"));
        onView(withId(R.id.editServer)).perform(replaceText("http://10.0.2.2:84/api"));
        onView(withId(R.id.editPassword)).perform(replaceText("admin"));

        onView(withId(R.id.loginButton)).perform(click());

        sleep();

        onView(withId(R.id.errorMessage))
                .check(matches(withText("Login failed")));

        screenshot("login-error.png");
    }
    public static void doLogin() {
        loginAs(new User("admin","admin","admin",null));
    }
    @Test
    public void AB_loginSuccess() {
        doLogin();

        onView(withId(R.id.nav_host_fragment_content_logged_in)).check(matches(isDisplayed()));

        screenshot("logged-in.png");
    }

}
