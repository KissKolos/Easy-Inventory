/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

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
public class UserTest {
    
    @Test
    public void listUsers() {
        gotoUsers();
        
        /*NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("add-button")
                .screenshotWindow("user.list.png");*/
        
        selectUserCard(new User("admin","admin",null,null)).exists();
    }
    
    @DataProvider(name = "user_data")
    public static Object[][] createData() {
        return new Object[][]{
            {
                new User("test_user"+TestUtils.RANDOM,"Test User","password",new User("admin","Admin",null,null)),
                new User("test_user2"+TestUtils.RANDOM,"Test User2","password2",new User("admin","Admin",null,null))
            },
            {
                new User("test★user"+TestUtils.RANDOM,"Test 特别的","password",new User("admin","Admin",null,null)),
                new User("特别的"+TestUtils.RANDOM,"","password2",new User("admin","Admin",null,null))
            }
        };
    }
    
    @Test(dataProvider = "user_data", dataProviderClass = UserTest.class)
    public void addUser(User new_user,User modified_user) {
        LoginUtils.doLogin();
        createUser(new_user,null,null);
    }
    
    @Test(dependsOnMethods={"addUser"},dataProvider = "user_data", dataProviderClass = UserTest.class)
    public void editUser(User new_user,User modified_user) {
        LoginUtils.doLogin();
        modifyUser(new_user,modified_user,null,null);
    }
    
    @Test(dependsOnMethods={"editUser"},dataProvider = "user_data", dataProviderClass = UserTest.class)
    public void deleteUserTest(User new_user,User modified_user) {
        LoginUtils.doLogin();
        deleteUser(modified_user,null);
    }
    
}
