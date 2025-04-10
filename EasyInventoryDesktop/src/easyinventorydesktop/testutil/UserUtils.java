/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.LocalAuthorization;
import easyinventoryapi.SystemAuthorization;
import easyinventoryapi.User;
import easyinventoryapi.Warehouse;
import static easyinventorydesktop.testutil.TestUtils.confirm;
import static easyinventorydesktop.testutil.TestUtils.sleep;
import static easyinventorydesktop.testutil.WarehouseUtils.*;

/**
 *
 * @author 3041TAN-08
 */
public class UserUtils {
    public static void gotoUsers() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .selectTab(0);
    }
    
    public static NodeMatcher selectUserCard(User u) {
        gotoUsers();
        NodeMatcher.allWindowRoots().descendants().withClass("search").replaceText(u.id);
        sleep();
        return NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("user-id").labels(u.id));
    }
    
    private static void fillEditDialog(User u,String form_screenshot,String select_screenshot) {
        NodeMatcher dialog=TestUtils.waitFor(()->NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog"))
                .descendants();
        
        if(form_screenshot!=null)
            dialog.withClass("edit-dialog")
                .screenshotWindow(form_screenshot);
        
        dialog.withClass("edit-field-0").replaceText(u.id);
        dialog.withClass("edit-field-1").replaceText(u.name);
        dialog.withClass("edit-field-2").replaceText(u.password);
        dialog.withClass("edit-field-3").click();
        
        sleep();
        
        if(select_screenshot!=null)
            NodeMatcher.allWindowRoots().descendants().withClass("select-view")
                .screenshotWindow(select_screenshot);
        
        NodeMatcher.allWindowRoots().descendants().withClass("select-view").descendants().withClass("search").replaceText(u.manager.id);
        sleep();
        
        NodeMatcher.allWindowRoots().descendants()
                .withClass("select-view")
                .descendants()
                .withClass("view-entry")
                .with(m->m.descendants().withClass("user-id").labels(u.manager.id))
                .descendants()
                .withClass("select-button")
                .click();
        
        dialog.withClass("edit-dialog").acceptDialog();
        
        sleep();
    }
    
    public static void createUser(User u,String form_screenshot,String select_screenshot) {
        gotoUsers();
        TestUtils.listAdd();
        fillEditDialog(u,form_screenshot,select_screenshot);
        TestUtils.listReload();
        selectUserCard(u).exists();
    }
    
    public static void modifyUser(User u,User new_u,String form_screenshot,String select_screenshot) {
        selectUserCard(u)
                .descendants()
                .withClass("edit-button")
                .clickASync();
        sleep();
        fillEditDialog(new_u,form_screenshot,select_screenshot);
        TestUtils.listReload();
        selectUserCard(new_u).exists();
    }
    
    public static void deleteUser(User u,String popup_screenshot) {
        selectUserCard(u)
                .descendants()
                .withClass("delete-button")
                .clickASync();
        if(popup_screenshot!=null)
            TestUtils.waitFor(()->NodeMatcher.allWindowRoots().descendants().withClass("confirm-dialog")).screenshotWindow(popup_screenshot);
        confirm();
        sleep();
        selectUserCard(u).doesNotExists();
    }
    
    public static void setUserLocalAuthorization(User user,Warehouse warehouse,String[] local_auth,String select_screenshot,String form_screenshot) {
        selectUserCard(user)
                .descendants()
                .withClass("local-auth-button")
                .clickASync();
        
        sleep();
        
        selectWarehousePopup(warehouse);
        
        NodeMatcher dialog=NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog")
                .descendants();
        
        if(form_screenshot!=null)
            dialog.withClass("edit-dialog")
                .screenshotWindow(form_screenshot);
        
        for(int i=0;i<LocalAuthorization.AUTHORIZATIONS.length;i++)
            if(contains(local_auth,LocalAuthorization.AUTHORIZATIONS[i]))
                dialog.withClass("edit-field-"+i).click();
        
        dialog.withClass("edit-dialog").acceptDialog();
    }
    
    public static void setUserSystemAuthorization(User user,String[] system_auth,String form_screenshot) {
        selectUserCard(user)
                .descendants()
                .withClass("auth-button")
                .clickASync();
        
        sleep();
        
        NodeMatcher dialog=NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog")
                .descendants();
        
        if(form_screenshot!=null)
            dialog.withClass("edit-dialog")
                .screenshotWindow(form_screenshot);
        
        for(int i=0;i<SystemAuthorization.AUTHORIZATIONS.length;i++)
            if(contains(system_auth,SystemAuthorization.AUTHORIZATIONS[i]))
                dialog.withClass("edit-field-"+i).click();
        
        dialog.withClass("edit-dialog").acceptDialog();
    }
    
    private static boolean contains(String[] auth,String a) {
        for (String auth1 : auth)
            if (auth1.equals(a))
                return true;
        return false;
    }
}
