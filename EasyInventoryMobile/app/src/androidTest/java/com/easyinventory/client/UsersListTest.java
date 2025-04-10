package com.easyinventory.client;

import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestUtils.clickUser;
import static com.easyinventory.client.TestUtils.gotoUsers;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import easyinventoryapi.User;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersListTest {

    @Test
    public void AA_listUsers() {
        User u=new User("admin","admin",null,null);

        LoginTest.doLogin();
        gotoUsers();
        clickUser(u);
        screenshot("list-users.png");
    }

}
