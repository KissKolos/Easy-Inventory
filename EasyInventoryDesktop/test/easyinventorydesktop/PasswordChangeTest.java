/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventorydesktop.testutil.TestUtils;
import easyinventoryapi.User;
import easyinventorydesktop.testutil.LoginUtils;
import static easyinventorydesktop.testutil.UserUtils.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class PasswordChangeTest {
    
    @DataProvider(name = "pw_data")
    public static Object[][] createData() {
        User admin=new User("admin","admin",null,null);
        return new Object[][]{
            {
                new User("pwtest_user1"+TestUtils.RANDOM,"Test User","password",admin),
                "",
                "",
                ""
            },
            {
                new User("pwtest_user2"+TestUtils.RANDOM,"Test User","password",admin),
                "pw21",
                "pw21",
                "password"
            },
            {
                new User("pwtest_user3"+TestUtils.RANDOM,"Test User","password",admin),
                "pw21",
                "pw21",
                "pass"
            },
            {
                new User("pwtest_user4"+TestUtils.RANDOM,"Test User","password",admin),
                "",
                "",
                "password"
            },
            {
                new User("pwtest_user5"+TestUtils.RANDOM,"Test User","password",admin),
                "",
                "pw21",
                "password"
            }
        };
    }
    
    @Test(dataProvider = "pw_data", dataProviderClass = PasswordChangeTest.class)
    public void addPWUser(User user,String pw1,String pw2,String oldpw) {
        LoginUtils.doLogin();
        createUser(user,null,null);
    }
    
    @Test(dependsOnMethods={"addPWUser"},dataProvider = "pw_data", dataProviderClass = PasswordChangeTest.class)
    public void changePassword(User user,String pw1,String pw2,String oldpw) {
        LoginUtils.loginAs(user);
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .selectTab(7);
        
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("pw-change-button")
                .clickASync();
        
        NodeMatcher dialog=TestUtils.waitFor(()->NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("edit-dialog"))
                .descendants();
        
        dialog.withClass("edit-field-0").replaceText(pw1);
        dialog.withClass("edit-field-1").replaceText(pw2);
        dialog.withClass("edit-field-2").replaceText(oldpw);
        dialog.withClass("edit-dialog").acceptDialog();
    }
    
    @Test(dependsOnMethods={"changePassword"},dataProvider = "pw_data", dataProviderClass = PasswordChangeTest.class)
    public void changedLogin(User user,String pw1,String pw2,String oldpw) {
        if(pw1.equals(pw2)&&pw1.length()>0&&oldpw.equals(user.password))
            LoginUtils.loginAs(new User(user.id,user.name,pw1,user.manager));
        else
            LoginUtils.loginAs(user);
    }
}
