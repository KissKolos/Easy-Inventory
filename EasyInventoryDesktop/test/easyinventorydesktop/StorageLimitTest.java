/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventoryapi.Item;
import easyinventoryapi.Storage;
import easyinventoryapi.Unit;
import easyinventoryapi.Warehouse;
import easyinventorydesktop.testutil.LoginUtils;
import static easyinventorydesktop.testutil.StorageUtils.*;
import static easyinventorydesktop.testutil.TestUtils.sleep;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class StorageLimitTest {
    
    @DataProvider(name = "limit_data")
    public static Object[][] createData() {
        Warehouse WH1=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        Storage ST1=new Storage(WH1,"BAJ1","Baja 1/2");
        Item IT1=new Item("steel_wire_40","Acélkábel 40mm",new Unit("m","Méter"));
        return new Object[][]{
                {
                        ST1,
                        IT1,
                        70
                }
        };
    }
    
    public static void limitStorage(Storage storage, Item item,int limit,String list_screenshot,String edit_screenshot) {
        selectStorageCard(storage)
                .descendants()
                .withClass("limit-button")
                .clickASync();
        
        sleep();
        
        if(list_screenshot!=null)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("limit-view")
                .screenshotWindow(list_screenshot);
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("limit-view")
                .descendants()
                .withClass("search")
                .replaceText(item.id);
        sleep();
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("limit-view")
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("item-name").labels(item.name))
                .descendants()
                .withClass("edit-button")
                .clickASync();
        
        sleep();
        
        if(edit_screenshot!=null)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog")
                .screenshotWindow(edit_screenshot);
        
        NodeMatcher dialog=NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog")
                .descendants();
        
        dialog.withClass("edit-field-0").replaceNumber(limit);
        dialog.withClass("edit-dialog").acceptDialog();
        
        sleep();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("dialog")
                .closeDialogs();
        
        sleep();
        
        selectStorageCard(storage)
                .descendants()
                .withClass("limit-button")
                .clickASync();
        
        sleep();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("limit-view")
                .descendants()
                .withClass("search")
                .replaceText(item.id);
        sleep();
        sleep();
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("limit-view")
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("item-name").labels(item.name))
                .with(m->m.descendants().withClass("item-amount").labels(""+limit))
                .exists();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .closeDialogs();
    }
    
    @Test(dataProvider = "limit_data", dataProviderClass = StorageLimitTest.class)
    public void limitStorage(Storage storage,Item item,int limit) {
        LoginUtils.doLogin();
        limitStorage(storage,item,limit,null,null);
    }
    
}
