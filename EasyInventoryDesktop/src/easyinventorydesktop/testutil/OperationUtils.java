/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;
import easyinventoryapi.Warehouse;
import static easyinventorydesktop.testutil.TestUtils.confirm;
import static easyinventorydesktop.testutil.TestUtils.sleep;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 *
 * @author 3041TAN-08
 */
public class OperationUtils {
    private static void gotoOperations() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .selectTab(6);
    }
    
    public static void gotoOperations(Warehouse w) {
        gotoOperations();
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("warehouse-select")
                .clickASync();
        WarehouseUtils.selectWarehousePopup(w);
    }
    
    public static NodeMatcher selectOperationCard(Warehouse w,Operation op) {
        gotoOperations(w);
        NodeMatcher.allWindowRoots().descendants().withClass("search").replaceText(op.id);
        sleep();
        return NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("operation-id").labels(op.id));
    }
    
    public static void viewOperation(Warehouse wh,Operation op, String form_screenshot) {
        selectOperationCard(wh,op)
                .descendants()
                .withClass("view-button")
                .clickASync();
        sleep();
        
        ObservableList<OperationItem> planned=((TableView<OperationItem>)NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("operation-items-table")
                .get()).getItems();
        
        if(form_screenshot!=null)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("operation-items-table")
                .screenshotWindow(form_screenshot);
        
        for(OperationItem i:op.items) {
            boolean found=false;
            for(OperationItem p:planned)
                if(i.item.id.equals(p.item.id)){
                    found=true;
                    break;
                }
            
            if(!found)
                throw new RuntimeException("operation not found");
        }
    }
    
    public static void createOperation(Warehouse wh,Operation op, String form_screenshot,String item_select_screenshot,String storage_select_screenshot,String item_screenshot) {
        gotoOperations(wh);
        TestUtils.listAdd();
        
        NodeMatcher dialog=NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("operation-dialog")
                .descendants();
        
        if(form_screenshot!=null)
            dialog.withClass("operation-dialog")
                .screenshotWindow(form_screenshot);
        
        dialog.withClass("operation-id").replaceText(op.id);
        dialog.withClass("operation-name").replaceText(op.name);
        if(op.is_add)
            dialog.withClass("operation-isadd").click();
        
        
        for(OperationItem item:op.items) {
            dialog.withClass("operation-item-add").clickASync();
            sleep();
            
            NodeMatcher item_dialog=NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog")
                .descendants();
            
            if(item_screenshot!=null)
                item_dialog.withClass("edit-dialog").screenshotWindow(item_screenshot);
            
            if(item.storage!=null) {
                item_dialog.withClass("edit-field-0").clickASync();
                sleep();
                if(storage_select_screenshot!=null)
                    NodeMatcher.allWindowRoots()
                        .descendants()
                        .withClass("select-view")
                        .screenshotWindow(storage_select_screenshot);
                StorageUtils.selectStoragePopup(item.storage);
            }
            
            item_dialog.withClass("edit-field-1").clickASync();
            sleep();
            if(item_select_screenshot!=null)
                NodeMatcher.allWindowRoots()
                        .descendants()
                        .withClass("select-view")
                        .screenshotWindow(item_select_screenshot);
            ItemUtils.selectItemPopup(item.item);
            
            if(item.global_serial!=0) {
                item_dialog.withClass("edit-field-3").click();
                item_dialog.withClass("edit-field-4").replaceText(""+item.global_serial);
                if(item.manufacturer_serial!=null)
                    item_dialog.withClass("edit-field-5").replaceText(item.manufacturer_serial);
            }
            if(item.lot!=null)
                item_dialog.withClass("edit-field-2").replaceText(item.lot);
            item_dialog.withClass("edit-field-6").replaceNumber(item.amount);
            
            item_dialog.withClass("edit-dialog").acceptDialog();
        }
        
        dialog.withClass("operation-dialog").acceptDialog();
        
        sleep();
        
        selectOperationCard(wh,op).exists();
    }
    
    public static void commitOperation(Warehouse wh,Operation op,String popup_screenshot) {
        selectOperationCard(wh,op)
                .descendants()
                .withClass("commit-button")
                .clickASync();
        if(popup_screenshot!=null)
            TestUtils.waitFor(()->NodeMatcher.allWindowRoots().descendants().withClass("confirm-dialog")).screenshotWindow(popup_screenshot);
        confirm();
        sleep();
        selectOperationCard(wh,op).doesNotExists();
    }
    
    public static void cancelOperation(Warehouse wh,Operation op,String popup_screenshot) {
        selectOperationCard(wh,op)
                .descendants()
                .withClass("cancel-button")
                .clickASync();
        if(popup_screenshot!=null)
            TestUtils.waitFor(()->NodeMatcher.allWindowRoots().descendants().withClass("confirm-dialog")).screenshotWindow(popup_screenshot);
        confirm();
        sleep();
        selectOperationCard(wh,op).doesNotExists();
    }
}
