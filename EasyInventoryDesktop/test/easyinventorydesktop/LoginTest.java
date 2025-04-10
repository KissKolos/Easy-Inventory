/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import static easyinventorydesktop.testutil.LoginUtils.doLogin;
import static easyinventorydesktop.testutil.TestUtils.launch;
import static easyinventorydesktop.testutil.TestUtils.sleep;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class LoginTest {
    
    public LoginTest() {}

    @Test
    public void loginFail() {
        launch();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-username")
                .replaceText("admin");
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-password")
                .replaceText("admin2");
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-server")
                .replaceText("http://127.0.0.1:8001/api");
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-button")
                .click();
        
        sleep();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("alert-dialog")
                .exists();
    }
    
    @Test
    public void loginSuccess() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("login-username")
                .screenshotWindow("login.png");
        
        doLogin();
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .exists();
    }    
}
