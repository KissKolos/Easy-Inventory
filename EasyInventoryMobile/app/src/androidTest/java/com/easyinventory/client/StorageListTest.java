package com.easyinventory.client;

import static com.easyinventory.client.TestSuite.screenshot;
import static com.easyinventory.client.TestUtils.clickStorage;
import static com.easyinventory.client.TestUtils.gotoStorages;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import easyinventoryapi.Storage;
import easyinventoryapi.Warehouse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageListTest {

    @Test
    public void AA_list() {
        Warehouse WH=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        Storage ST1=new Storage(WH,"BAJ0","Baja 1/1");

        LoginTest.doLogin();
        gotoStorages(WH);
        clickStorage(ST1);
        screenshot("list-storages.png");
    }

}
