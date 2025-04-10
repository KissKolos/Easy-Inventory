/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.User;
import static easyinventorydesktop.testutil.TestUtils.launch;
import static easyinventorydesktop.testutil.TestUtils.sleep;

/**
 *
 * @author 3041TAN-08
 */
public class LoginUtils {
    public static void loginAs(User u) {
        loginAs(u,null);
    }
    
    public static void loginAs(User u,String screenshot) {
        launch();
        
        TestUtils.waitFor(()->NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-username"));
        
        if(screenshot!=null){
            sleep();
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-username")
                .screenshotWindow(screenshot);
        }
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-username")
                .replaceText(u.id);
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-password")
                .replaceText(u.password);
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-server")
                .replaceText("http://127.0.0.1:8001/api");
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-button")
                .click();
        
        TestUtils.waitFor(()->NodeMatcher.allWindowRoots()
            .descendants()
            .tabPanes());
    }
    
    public static void doLogin() {
        loginAs(new User("admin","admin","admin",null));
    }
}
