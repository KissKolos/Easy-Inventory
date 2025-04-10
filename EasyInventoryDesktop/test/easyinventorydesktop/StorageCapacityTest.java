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
public class StorageCapacityTest {
    
    @DataProvider(name = "capacity_data")
    public static Object[][] createData() {
        Warehouse WH_ajka=new Warehouse("WH_ajka","Ajkai telephely","Ajka Eötvös út 57.");
        Storage ST_ajka1=new Storage(WH_ajka,"AJK0","Ajka 1/1");
        Item IT1=new Item("steel_wire_12","Acélkábel 12mm",new Unit("m","Méter"));
        return new Object[][]{
                {
                        ST_ajka1,
                        IT1,
                        "16/92 (17%)"
                }
        };
    }
    
    public static void checkCapacity(Storage storage,Item item,String text,String form_screenshot) {
        selectStorageCard(storage)
                .descendants()
                .withClass("capacity-button")
                .clickASync();
        
        sleep();
        
        if(form_screenshot!=null)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("storage-capacity-view")
                .screenshotWindow(form_screenshot);
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("storage-capacity-view")
                .descendants()
                .withClass("search")
                .replaceText(item.id);
        sleep();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("storage-capacity-view")
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("item-name").labels(item.name))
                .with(m->m.descendants().withClass("item-capacity").labels(text))
                .exists();
    }
    
    @Test(dataProvider = "capacity_data", dataProviderClass = StorageCapacityTest.class)
    public void checkCapacity(Storage storage,Item item,String text) {
        LoginUtils.doLogin();
        checkCapacity(storage,item,text,null);
    }
    
}
