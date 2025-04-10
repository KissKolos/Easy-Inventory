package com.easyinventory.client;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.easyinventory.client.TestSuite.RANDOM;
import static org.hamcrest.CoreMatchers.allOf;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.User;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PasswordChangeTest {

    private final User user;
    private final String pw1,pw2,oldpw;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        User admin=new User("admin","admin",null,null);
        return Arrays.asList(new Object[][]{
                {
                        new User("pwtest_user1"+RANDOM,"Test User","password",admin),
                        "",
                        "",
                        ""
                },
                {
                        new User("pwtest_user2"+RANDOM,"Test User","password",admin),
                        "pw21",
                        "pw21",
                        "password"
                },
                {
                        new User("pwtest_user3"+RANDOM,"Test User","password",admin),
                        "pw21",
                        "pw21",
                        "pass"
                },
                {
                        new User("pwtest_user4"+RANDOM,"Test User","password",admin),
                        "",
                        "",
                        "password"
                },
                {
                        new User("pwtest_user5"+RANDOM,"Test User","password",admin),
                        "",
                        "pw21",
                        "password"
                }
        });
    }

    public PasswordChangeTest(User user, String pw1, String pw2,String oldpw) {
        this.user=user;
        this.pw1=pw1;
        this.pw2=pw2;
        this.oldpw=oldpw;
    }

    @Test
    public void addPWUser() {
        LoginTest.doLogin();
        UsersTest.createUser(user,null,null);
    }

    @Test
    public void changePassword() {
        LoginTest.loginAs(user);

        onView(withId(R.id.drawer_layout)).perform(open());
        onView(allOf(isDescendantOfA(withId(R.id.nav_view)),withText("Profile"))).perform(click());
        onView(withId(R.id.password_change)).perform(click());

        onView(withId(R.id.new_password1)).perform(replaceText(pw1));
        onView(withId(R.id.new_password2)).perform(replaceText(pw2));
        onView(withId(R.id.old_password)).perform(replaceText(oldpw));

        onView(withId(R.id.editConfirm)).perform(click());
    }

    @Test
    public void changedLogin() {
        if(pw1.equals(pw2)&& (!pw1.isEmpty()) &&oldpw.equals(user.password))
            LoginTest.loginAs(new User(user.id,user.name,pw1,user.manager));
        else
            LoginTest.loginAs(user);
    }

}
