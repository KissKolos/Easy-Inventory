/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.Warehouse;
import static easyinventorydesktop.testutil.TestUtils.confirm;
import static easyinventorydesktop.testutil.TestUtils.sleep;

/**
 *
 * @author 3041TAN-08
 */
public class WarehouseUtils {
    public static void gotoWarehouses() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .selectTab(1);
    }
    
    public static void selectWarehousePopup(Warehouse w) {
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
                .with(m->m.descendants().withClass("warehouse-id").labels(w.id))
                .descendants()
                .withClass("select-button")
                .click();
        
        sleep();
    }
    
    public static NodeMatcher selectWarehouseCard(Warehouse w) {
        gotoWarehouses();
        
        NodeMatcher.allWindowRoots().descendants().withClass("search").replaceText(w.id);
        sleep();
        return NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("warehouse-id").labels(w.id));
    }
    
    public static void createWarehouse(Warehouse w,String form_screenshot) {
        gotoWarehouses();
        TestUtils.listAdd();
        fillEditDialog(w,form_screenshot);
        TestUtils.listReload();
        selectWarehouseCard(w).exists();
    }
    
    public static void modifyWarehouse(Warehouse w,Warehouse new_w,String form_screenshot) {
        gotoWarehouses();
        selectWarehouseCard(w)
                .descendants()
                .withClass("edit-button")
                .clickASync();
        sleep();
        fillEditDialog(new_w,form_screenshot);
        TestUtils.listReload();
        selectWarehouseCard(new_w).exists();
    }
    
    public static void deleteWarehouse(Warehouse w,String popup_screenshot) {
        gotoWarehouses();
        selectWarehouseCard(w)
                .descendants()
                .withClass("delete-button")
                .clickASync();
        if(popup_screenshot!=null)
            TestUtils.waitFor(()->NodeMatcher.allWindowRoots().descendants().withClass("confirm-dialog")).screenshotWindow(popup_screenshot);
        confirm();
        sleep();
        selectWarehouseCard(w).doesNotExists();
    }
    
    private static void fillEditDialog(Warehouse w,String form_screenshot) {
        NodeMatcher dialog=NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog")
                .descendants();
        
        if(form_screenshot!=null)
            dialog.withClass("edit-dialog")
                .screenshotWindow(form_screenshot);
        
        dialog.withClass("edit-field-0").replaceText(w.id);
        dialog.withClass("edit-field-1").replaceText(w.name);
        dialog.withClass("edit-field-2").replaceText(w.address);
        
        dialog.withClass("edit-dialog").acceptDialog();
        sleep();
    }
}
