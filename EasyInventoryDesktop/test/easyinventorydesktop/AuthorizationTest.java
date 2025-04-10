/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.TestUtils;
import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;
import easyinventoryapi.Storage;
import easyinventoryapi.Unit;
import easyinventoryapi.User;
import easyinventoryapi.Warehouse;
import easyinventorydesktop.testutil.LoginUtils;
import easyinventorydesktop.testutil.OperationUtils;
import easyinventorydesktop.testutil.StorageUtils;
import static easyinventorydesktop.testutil.TestUtils.sleep;
import easyinventorydesktop.testutil.UnitUtils;
import static easyinventorydesktop.testutil.UserUtils.*;
import static easyinventorydesktop.testutil.WarehouseUtils.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class AuthorizationTest {
    
    @DataProvider(name = "users")
    public static Object[][] createData() {
        User admin=new User("admin","admin","",null);
        Warehouse wh=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        return new Object[][]{
            {
                new User("auth_test_user1"+TestUtils.RANDOM,"Test User","password",admin),
                new String[]{
                    "view_warehouses",
                    "delete_warehouses",
                    "create_warehouses",
                    "modify_warehouses",
                    "delete_types",
                    "create_types",
                    "modify_types",
                    "view_users",
                    "delete_users",
                    "create_users",
                    "modify_users"
                },
                wh,
                new String[]{
                    "view",
                    "create_add_operation",
                    "create_remove_operation",
                    "handle_operation",
                    "configure"
                }
            },
            {
                new User("auth_test_user2"+TestUtils.RANDOM,"Test User","password",admin),
                new String[]{},
                wh,
                new String[]{}
            },
            {
                new User("auth_test_user3"+TestUtils.RANDOM,"Test User","password",admin),
                new String[]{
                    "view_warehouses",
                    "create_warehouses",
                    "modify_warehouses",
                    "delete_types",
                    "modify_types",
                    "delete_users",
                    "modify_users"
                },
                wh,
                new String[]{
                    "view",
                    "create_add_operation",
                    "handle_operation",
                }
            }
        };
    }
    
    public static boolean contains(String[] auth,String a) {
        for (String auth1 : auth)
            if (auth1.equals(a))
                return true;
        return false;
    }
    
    @Test(dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void createUsers(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.doLogin();
        createUser(user,null,null);
        setUserSystemAuthorization(user, system_auth, null);
        sleep();
        setUserLocalAuthorization(user, warehouse, local_auth, null, null);
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canViewWarehouse(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Warehouse wh=new Warehouse("WH_oroshaza","Orosházai telephely","Orosháza Leshegy utca 1.");
        
        Assert.assertEquals(selectWarehouseCard(wh).count()==1,contains(system_auth,"view_warehouses"));
        Assert.assertEquals(selectWarehouseCard(warehouse).count()==1,contains(local_auth,"view"));
    }
    
    private void assertFail(boolean fail,Runnable r) {
        try{
            r.run();
            if(fail)
                Assert.fail("should have failed");
        }catch(RuntimeException e) {
            if(!fail){
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        }
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canAddWarehouse(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Warehouse wh=new Warehouse("WH_authtest_add_"+user.id,"Authtest WH","test");
        
        assertFail(!contains(system_auth,"create_warehouses"),()->{
            createWarehouse(wh,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canRemoveWarehouse1(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.doLogin();
        
        Warehouse wh=new Warehouse("WH_authtest_remove_"+user.id,"Authtest WH","test");
        createWarehouse(wh,null);
    }
    
    @Test(dependsOnMethods={"canRemoveWarehouse1"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canRemoveWarehouse2(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Warehouse wh=new Warehouse("WH_authtest_remove_"+user.id,"Authtest WH","test");
        assertFail(!contains(system_auth,"delete_warehouses"),()->{
            deleteWarehouse(wh,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canAddUnit(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Unit u=new Unit("U_authtest_add_"+user.id,"Authtest Unit");
        assertFail(!contains(system_auth,"create_types"),()->{
            UnitUtils.createUnit(u,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canRemoveUnit1(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.doLogin();
        
        Unit u=new Unit("U_authtest_remove_"+user.id,"Authtest Unit");
        UnitUtils.createUnit(u,null);
    }
    
    @Test(dependsOnMethods={"canRemoveUnit1"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canRemoveUnit2(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Unit u=new Unit("U_authtest_remove_"+user.id,"Authtest Unit");
        assertFail(!contains(system_auth,"delete_types"),()->{
            UnitUtils.deleteUnit(u,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canViewUser(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        User u=new User("alphonse_melendez1989","Alphonse Melendez",null,null);
        Assert.assertEquals(selectUserCard(u).count()==1,contains(system_auth,"view_users"));
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canAddUser(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        User u=new User("U_authtest_add_"+user.id,"Authtest User","pass",user);
        assertFail(!contains(system_auth,"create_users"),()->{
            createUser(u,null,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canRemoveUser1(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.doLogin();
        
        User u=new User("U_authtest_remove_"+user.id,"Authtest User","pass",user);
        createUser(u,null,null);
    }
    
    @Test(dependsOnMethods={"canRemoveUser1"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canRemoveUser2(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        User u=new User("U_authtest_remove_"+user.id,"Authtest User","pass",user);
        assertFail(!contains(system_auth,"delete_users"),()->{
            deleteUser(u,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canAddStorage(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Storage s=new Storage(warehouse,"ST_authtest_add_"+user.id,"Authtest Storage");
        assertFail(!(contains(system_auth,"modify_warehouses")||contains(local_auth,"configure")),()->{
            StorageUtils.createStorage(s,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canAddInsertionOperation(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Operation op=new Operation("OP_authtest_add_"+user.id,"Operation_authtest",true,new OperationItem[]{});
        assertFail(!contains(local_auth,"create_add_operation"),()->{
            OperationUtils.createOperation(warehouse,op,null,null,null,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canAddRemoveOperation(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Operation op=new Operation("OP_authtest_remove_"+user.id,"Operation_authtest",false,new OperationItem[]{});
        assertFail(!contains(local_auth,"create_remove_operation"),()->{
            OperationUtils.createOperation(warehouse,op,null,null,null,null);
        });
    }
    
    @Test(dependsOnMethods={"createUsers"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canHandleOperation1(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.doLogin();
        Operation op=new Operation("OP_authtest_handle_"+user.id,"Operation_authtest",false,new OperationItem[]{});
        OperationUtils.createOperation(warehouse, op,null,null,null,null);
    }
    
    @Test(dependsOnMethods={"canHandleOperation1"},dataProvider = "users", dataProviderClass = AuthorizationTest.class)
    public void canHandleOperation2(User user,String[] system_auth,Warehouse warehouse,String[] local_auth) {
        LoginUtils.loginAs(user);
        
        Operation op=new Operation("OP_authtest_handle_"+user.id,"Operation_authtest",false,new OperationItem[]{});
        assertFail(!contains(local_auth,"handle_operation"),()->{
            OperationUtils.cancelOperation(warehouse,op,null);
        });
    }
    
}
