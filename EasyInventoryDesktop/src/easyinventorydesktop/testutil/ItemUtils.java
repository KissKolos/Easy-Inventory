/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.Item;
import static easyinventorydesktop.testutil.TestUtils.confirm;
import static easyinventorydesktop.testutil.TestUtils.sleep;

/**
 *
 * @author 3041TAN-08
 */
public class ItemUtils {
    public static void gotoItems() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .selectTab(2);
    }
    
    public static NodeMatcher selectItemCard(Item u) {
        gotoItems();
        NodeMatcher.allWindowRoots().descendants().withClass("search").replaceText(u.id);
        sleep();
        return NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("item-id").labels(u.id));
    }
    
    public static void selectItemPopup(Item w) {
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
                .with(m->m.descendants().withClass("item-id").labels(w.id))
                .descendants()
                .withClass("select-button")
                .click();
        
        sleep();
    }
    
    public static void createItem(Item i,String form_screenshot,String select_screenshot) {
        gotoItems();
        TestUtils.listAdd();
        fillEditDialog(i,form_screenshot,select_screenshot);
        TestUtils.listReload();
        selectItemCard(i).exists();
    }
    
    public static void modifyItem(Item i,Item new_i,String form_screenshot,String select_screenshot) {
        selectItemCard(i)
                .descendants()
                .withClass("edit-button")
                .clickASync();
        sleep();
        fillEditDialog(new_i,form_screenshot,select_screenshot);
        TestUtils.listReload();
        selectItemCard(new_i).exists();
    }
    
    public static void deleteItem(Item i,String popup_screenshot) {
        selectItemCard(i)
                .descendants()
                .withClass("delete-button")
                .clickASync();
        if(popup_screenshot!=null)
            TestUtils.waitFor(()->NodeMatcher.allWindowRoots().descendants().withClass("confirm-dialog")).screenshotWindow(popup_screenshot);
        confirm();
        sleep();
        selectItemCard(i).doesNotExists();
    }
    private static void fillEditDialog(Item i,String form_screenshot,String select_screenshot) {
        NodeMatcher dialog=TestUtils.waitFor(()->NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog"))
                .descendants();
        
        if(form_screenshot!=null)
            dialog.withClass("edit-dialog")
                .screenshotWindow(form_screenshot);
        
        dialog.withClass("edit-field-0").replaceText(i.id);
        dialog.withClass("edit-field-1").replaceText(i.name);
        dialog.withClass("edit-field-2").click();
        
        sleep();
        
        if(select_screenshot!=null)
            NodeMatcher.allWindowRoots().descendants().withClass("select-view")
                .screenshotWindow(select_screenshot);
        
        NodeMatcher.allWindowRoots().descendants().withClass("select-view").descendants().withClass("search").replaceText(i.unit.id);
        sleep();
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("select-view")
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("unit-id").labels(i.unit.id))
                .descendants()
                .withClass("select-button")
                .click();
        
        dialog.withClass("edit-dialog").acceptDialog();
        
        sleep();
    }
}
