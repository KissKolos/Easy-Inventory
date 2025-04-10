package com.easyinventory.client;

import static com.easyinventory.client.TestSuite.RANDOM;
import static com.easyinventory.client.TestUtils.clickUser;
import static com.easyinventory.client.TestUtils.clickWarehouse;
import static com.easyinventory.client.TestUtils.gotoWarehouses;

import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;
import easyinventoryapi.Storage;
import easyinventoryapi.Unit;
import easyinventoryapi.User;
import easyinventoryapi.Warehouse;

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthorizationTest {

    private final User user;
    private final Warehouse warehouse;
    private final String[] system_authorizations,local_authorizations;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        User admin=new User("admin","admin","",null);
        Warehouse wh=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        return Arrays.asList(new Object[][]{
                {
                        new User("auth_test_user1"+RANDOM,"Test User","password",admin),
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
                        new User("auth_test_user2"+RANDOM,"Test User","password",admin),
                        new String[]{},
                        wh,
                        new String[]{}
                },
                {
                        new User("auth_test_user3"+RANDOM,"Test User","password",admin),
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
        });
    }

    public AuthorizationTest(User user,String[] system_authorizations,Warehouse warehouse,String[] local_authorizations) {
        this.user=user;
        this.system_authorizations=system_authorizations;
        this.warehouse=warehouse;
        this.local_authorizations=local_authorizations;
    }

    public static Matcher<View> nthChildOf(final int childPosition,Matcher<View> parent) {
        return new TypeSafeMatcher<>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return false;
                }
                if (!parent.matches(view.getParent())) {
                    return matchesSafely((ViewGroup)view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return group.getChildAt(childPosition).equals(view);
            }
        };
    }

    public static boolean contains(String[] auth,String a) {
        for (String auth1 : auth)
            if (auth1.equals(a))
                return true;
        return false;
    }

    @Test
    public void AA_createUsers() {
        LoginTest.doLogin();
        UsersTest.createUser(user,null,null);
        UsersTest.setUserSystemAuthorization(user,system_authorizations,null);
        UsersTest.setUserLocalAuthorization(user,warehouse,local_authorizations,null,null);
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

    @Test
    public void BA_canViewWarehouse() {
        LoginTest.loginAs(user);

        Warehouse wh=new Warehouse("WH_oroshaza","Orosházai telephely","Orosháza Leshegy utca 1.");

        gotoWarehouses();

        assertFail(!contains(system_authorizations,"view_warehouses"),()->{
            clickWarehouse(wh);
        });
        assertFail(!contains(local_authorizations,"view"),()->{
            clickWarehouse(warehouse);
        });
    }

    @Test
    public void BB_canCreateWarehouse() {
        LoginTest.loginAs(user);

        Warehouse wh=new Warehouse("WH_authtest_add_"+user.id,"Authtest WH","test");

        assertFail(!contains(system_authorizations,"create_warehouses"),()->{
            WarehouseTest.createWarehouse(wh,null);
        });
    }

    @Test
    public void BC_canRemoveWarehouse1() {
        LoginTest.doLogin();

        Warehouse wh=new Warehouse("WH_authtest_remove_"+user.id,"Authtest WH","test");
        WarehouseTest.createWarehouse(wh,null);
    }

    @Test
    public void BD_canRemoveWarehouse2() {
        LoginTest.loginAs(user);

        Warehouse wh=new Warehouse("WH_authtest_remove_"+user.id,"Authtest WH","test");
        assertFail(!contains(system_authorizations,"delete_warehouses"),()->{
            WarehouseTest.deleteWarehouse(wh,null);
        });
    }

    @Test
    public void CA_canAddUnit() {
        LoginTest.loginAs(user);

        Unit u=new Unit("U_authtest_add_"+user.id,"Authtest Unit");
        assertFail(!contains(system_authorizations,"create_types"),()->{
            UnitTest.createUnit(u,null);
        });
    }

    @Test
    public void CB_canRemoveUnit1() {
        LoginTest.doLogin();

        Unit u=new Unit("U_authtest_remove_"+user.id,"Authtest Unit");
        UnitTest.createUnit(u,null);
    }

    @Test
    public void CC_canRemoveUnit2() {
        LoginTest.loginAs(user);

        Unit u=new Unit("U_authtest_remove_"+user.id,"Authtest Unit");
        assertFail(!contains(system_authorizations,"delete_types"),()->{
            UnitTest.deleteUnit(u,null);
        });
    }

    @Test
    public void DA_canViewUser() {
        LoginTest.loginAs(user);

        User u=new User("alphonse_melendez1989","Alphonse Melendez",null,new User("liliana_levine2003","Liliana Levine",null,null));
        assertFail(!contains(system_authorizations,"view_users"),()->{
            clickUser(u);
        });
    }

    @Test
    public void DB_canAddUser() {
        LoginTest.loginAs(user);

        User u=new User("U_authtest_add_"+user.id,"Authtest User","pass",user);
        assertFail(!contains(system_authorizations,"create_users"),()->{
            UsersTest.createUser(u,null,null);
        });
    }

    @Test
    public void DC_canRemoveUser1() {
        LoginTest.doLogin();

        User u=new User("U_authtest_remove_"+user.id,"Authtest User","pass",user);
        UsersTest.createUser(u,null,null);
    }

    @Test
    public void DD_canRemoveUser2() {
        LoginTest.loginAs(user);

        User u=new User("U_authtest_remove_"+user.id,"Authtest User","pass",user);
        assertFail(!contains(system_authorizations,"delete_users"),()->{
            UsersTest.deleteUser(u,null);
        });
    }

    @Test
    public void EA_canAddStorage() {
        LoginTest.loginAs(user);

        Storage s=new Storage(warehouse,"ST_authtest_add_"+user.id,"Authtest Storage");
        assertFail(!(contains(system_authorizations,"modify_warehouses")||contains(local_authorizations,"configure")),()->{
            StorageTest.createStorage(s,null);
        });
    }

    @Test
    public void FA_canAddInsertionOperation() {
        LoginTest.loginAs(user);

        Operation op=new Operation("OP_authtest_add_"+user.id,"Operation_authtest",true,new OperationItem[]{});
        assertFail(!contains(local_authorizations,"create_add_operation"),()->{
            OperationTest.createOperation(warehouse,op,null,null,null,null);
        });
    }

    @Test
    public void FB_canAddRemoveOperation() {
        LoginTest.loginAs(user);

        Operation op=new Operation("OP_authtest_remove_"+user.id,"Operation_authtest",false,new OperationItem[]{});
        assertFail(!contains(local_authorizations,"create_remove_operation"),()->{
            OperationTest.createOperation(warehouse,op,null,null,null,null);
        });
    }

    @Test
    public void FC_canHandleOperation1() {
        LoginTest.doLogin();
        Operation op=new Operation("OP_authtest_handle_"+user.id,"Operation_authtest",false,new OperationItem[]{});
        OperationTest.createOperation(warehouse, op,null,null,null,null);
    }

    @Test
    public void FD_canHandleOperation2() {
        LoginTest.loginAs(user);

        Operation op=new Operation("OP_authtest_handle_"+user.id,"Operation_authtest",false,new OperationItem[]{});
        assertFail(!contains(local_authorizations,"handle_operation"),()->{
            OperationTest.cancelOperation(warehouse,op,null);
        });
    }



}
