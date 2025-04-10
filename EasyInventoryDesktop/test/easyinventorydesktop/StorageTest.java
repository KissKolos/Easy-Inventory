/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventorydesktop.testutil.TestUtils;
import easyinventoryapi.Storage;
import easyinventoryapi.Warehouse;
import easyinventorydesktop.testutil.LoginUtils;
import static easyinventorydesktop.testutil.StorageUtils.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class StorageTest {
    
    @DataProvider(name = "storage_data")
    public static Object[][] createData() {
        Warehouse WH=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        return new Object[][]{
            {
                new Storage(WH,"test_storage1"+TestUtils.RANDOM,"Storage 1"),
                new Storage(WH,"test_storage2"+TestUtils.RANDOM,"test storage2")
            },
            {
                new Storage(WH,"特别的"+TestUtils.RANDOM,"Storage 1"),
                new Storage(WH,"test_storage2特别的"+TestUtils.RANDOM,"特别的")
            }
        };
    }
    
    @Test
    public void listStorages() {
        Warehouse WH=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        Storage ST1=new Storage(WH,"BAJ0","Baja 1/1");
        
        /*NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("add-button")
                .screenshotWindow("storage.list.png");*/
        
        selectStorageCard(ST1).exists();
    }
    
    @Test(dataProvider = "storage_data", dataProviderClass = StorageTest.class)
    public void addStorage(Storage new_storage,Storage modified_storage) {
        LoginUtils.doLogin();
        createStorage(new_storage,null);
    }
    
    @Test(dependsOnMethods={"addStorage"},dataProvider = "storage_data", dataProviderClass = StorageTest.class)
    public void editStorage(Storage new_storage,Storage modified_storage) {
        LoginUtils.doLogin();
        modifyStorage(new_storage,modified_storage,null);
    }
    
    @Test(dependsOnMethods={"editStorage"},dataProvider = "storage_data", dataProviderClass = StorageTest.class)
    public void deleteStorageTest(Storage new_storage,Storage modified_storage) {
        LoginUtils.doLogin();
        deleteStorage(modified_storage,null);
    }
    
    @Test(dependsOnMethods={"deleteStorageTest"},dataProvider = "storage_data", dataProviderClass = StorageTest.class)
    public void checkArchivedStorage(Storage new_storage,Storage modified_storage) {
        LoginUtils.doLogin();
        gotoStorages(new_storage.warehouse);
        TestUtils.listArchived();
        NodeMatcher card=selectStorageCard(modified_storage);
        card.exists();
        
        card.descendants()
                .withClass("delete-button")
                .doesNotExists();
        
        card.descendants()
                .withClass("edit-button")
                .doesNotExists();
        
        card.descendants()
                .withClass("limit-button")
                .doesNotExists();
        
        card.descendants()
                .withClass("capacity-button")
                .doesNotExists();
    }
    
}
