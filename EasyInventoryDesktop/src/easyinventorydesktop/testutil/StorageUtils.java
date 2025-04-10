/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.Storage;
import easyinventoryapi.Warehouse;
import static easyinventorydesktop.testutil.TestUtils.confirm;
import static easyinventorydesktop.testutil.TestUtils.sleep;

/**
 *
 * @author 3041TAN-08
 */
public class StorageUtils {
    public static void gotoStorages() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .selectTab(4);
    }
    
    public static void gotoStorages(Warehouse w) {
        gotoStorages();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("warehouse-select")
                .clickASync();
        
        WarehouseUtils.selectWarehousePopup(w);
    }
    
    public static void selectStoragePopup(Storage w) {
        TestUtils.waitFor(()->NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("select-view"))
                .descendants()
                .withClass("search")
                .replaceText(w.id);
        
        sleep();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("select-view")
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("storage-id").labels(w.id))
                .descendants()
                .withClass("select-button")
                .click();
        
        sleep();
    }
    
    public static NodeMatcher selectStorageCard(Storage s) {
        gotoStorages(s.warehouse);
        NodeMatcher.allWindowRoots().descendants().withClass("search").replaceText(s.id);
        sleep();
        return NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("storage-id").labels(s.id));
    }
    
    public static void createStorage(Storage s,String form_screenshot) {
        gotoStorages(s.warehouse);
        TestUtils.listAdd();
        fillEditDialog(s,form_screenshot);
        TestUtils.listReload();
        selectStorageCard(s).exists();
    }
    
    public static void modifyStorage(Storage s,Storage new_s,String form_screenshot) {
        selectStorageCard(s)
                .descendants()
                .withClass("edit-button")
                .clickASync();
        sleep();
        fillEditDialog(new_s,form_screenshot);
        TestUtils.listReload();
        selectStorageCard(new_s).exists();
    }
    
    public static void deleteStorage(Storage s,String popup_screenshot) {
        selectStorageCard(s)
                .descendants()
                .withClass("delete-button")
                .clickASync();
        if(popup_screenshot!=null)
            TestUtils.waitFor(()->NodeMatcher.allWindowRoots().descendants().withClass("confirm-dialog")).screenshotWindow(popup_screenshot);
        confirm();
        sleep();
        
        /*NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("add-button")
                .screenshotWindow("storage.delete.png");*/
        
        selectStorageCard(s).doesNotExists();
    }
    
    private static void fillEditDialog(Storage s,String form_screenshot) {
        NodeMatcher dialog=NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog")
                .descendants();
        
        if(form_screenshot!=null)
            dialog.withClass("edit-dialog")
                .screenshotWindow(form_screenshot);
        
        dialog.withClass("edit-field-0").replaceText(s.id);
        dialog.withClass("edit-field-1").replaceText(s.name);
        
        dialog.withClass("edit-dialog").acceptDialog();
        
        sleep();
    }
}
