package com.easyinventory.client;

import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestUtils.clickItem;
import static com.easyinventory.client.TestUtils.gotoItems;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import easyinventoryapi.Item;
import easyinventoryapi.Unit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemsListTest {

    @Test
    public void AA_listItems() {
        LoginTest.doLogin();
        gotoItems();
        clickItem(new Item("square_pattern_carpet_brown","Barna kockás szőnyeg",new Unit("m2","Négyzetméter")));

        screenshot("list-items.png");
    }

}
