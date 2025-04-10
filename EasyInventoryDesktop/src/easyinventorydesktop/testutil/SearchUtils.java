/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.Storage;
import easyinventoryapi.Warehouse;
import static easyinventorydesktop.testutil.TestUtils.sleep;

/**
 *
 * @author 3041TAN-08
 */
public class SearchUtils {
    public static void gotoSearch() {
        LoginUtils.doLogin();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .selectTab(5);
    }
    public static void search(Warehouse wh,Storage st,String query,boolean show_warehouse,boolean show_storage,boolean show_lot,boolean show_serial,String search_screenshot) {
        gotoSearch();
        
        if(wh!=null) {
            NodeMatcher.allWindowRoots()
                    .descendants()
                    .withClass("warehouse-select")
                    .clickASync();
            
            sleep();
            
            WarehouseUtils.selectWarehousePopup(wh);
        }
        
        if(st!=null) {
            NodeMatcher.allWindowRoots()
                    .descendants()
                    .withClass("storage-select")
                    .clickASync();
            
            sleep();
            
            StorageUtils.selectStoragePopup(st);
        }
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("search-query")
                .replaceText(query);
        
        if(show_warehouse)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("search-warehouse")
                .click();
        
        if(show_storage)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("search-storage")
                .click();
        
        if(show_lot)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("search-lot")
                .click();
        
        if(show_serial)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("search-serial")
                .click();
        
        NodeMatcher.allWindowRoots()
            .descendants()
            .withClass("search-button")
            .click();
        
        sleep();
        
        if(search_screenshot!=null)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("next-button")
                .screenshotWindow(search_screenshot);
    }
}
