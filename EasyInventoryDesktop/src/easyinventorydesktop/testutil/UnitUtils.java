/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.Unit;
import static easyinventorydesktop.testutil.TestUtils.confirm;
import static easyinventorydesktop.testutil.TestUtils.sleep;

/**
 *
 * @author 3041TAN-08
 */
public class UnitUtils {
    public static void gotoUnits() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .selectTab(3);
    }
    
    public static NodeMatcher selectUnitCard(Unit u) {
        gotoUnits();
        
        NodeMatcher.allWindowRoots().descendants().withClass("search").replaceText(u.id);
        sleep();
        return NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("unit-id").labels(u.id));
    }
    
    public static void createUnit(Unit u,String form_screenshot) {
        gotoUnits();
        TestUtils.listAdd();
        fillEditDialog(u,form_screenshot);
        TestUtils.listReload();
        selectUnitCard(u).exists();
    }
    
    public static void modifyUnit(Unit u,Unit new_u,String form_screenshot) {
        selectUnitCard(u)
                .descendants()
                .withClass("edit-button")
                .clickASync();
        sleep();
        fillEditDialog(new_u,form_screenshot);
        TestUtils.listReload();
        selectUnitCard(new_u).exists();
    }
    
    public static void deleteUnit(Unit u,String popup_screenshot) {
        selectUnitCard(u)
                .descendants()
                .withClass("delete-button")
                .clickASync();
        if(popup_screenshot!=null)
            TestUtils.waitFor(()->NodeMatcher.allWindowRoots().descendants().withClass("confirm-dialog")).screenshotWindow(popup_screenshot);
        confirm();
        sleep();
        
        selectUnitCard(u).doesNotExists();
    }
    
    private static void fillEditDialog(Unit u,String form_screenshot) {
        NodeMatcher dialog=TestUtils.waitFor(()->NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog"))
                .descendants();
        
        if(form_screenshot!=null)
            dialog.withClass("edit-dialog")
                .screenshotWindow(form_screenshot);
        
        dialog.withClass("edit-field-0").replaceText(u.id);
        dialog.withClass("edit-field-1").replaceText(u.name);
        
        dialog.withClass("edit-dialog").acceptDialog();
        
        sleep();
    }
}
