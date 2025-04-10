package com.easyinventory.client;

import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestUtils.clickWarehouse;
import static com.easyinventory.client.TestUtils.gotoWarehouses;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import easyinventoryapi.Warehouse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WarehouseListTest {


    @Test
    public void AA_list() {
        Warehouse WH=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");

        LoginTest.doLogin();
        gotoWarehouses();
        clickWarehouse(WH);
        screenshot("list-warehouses.png");
    }

}
