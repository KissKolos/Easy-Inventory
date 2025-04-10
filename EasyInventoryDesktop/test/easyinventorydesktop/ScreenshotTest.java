/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventoryapi.*;
import easyinventorydesktop.testutil.ItemUtils;
import easyinventorydesktop.testutil.LoginUtils;
import easyinventorydesktop.testutil.OperationUtils;
import easyinventorydesktop.testutil.SearchUtils;
import easyinventorydesktop.testutil.StorageUtils;
import static easyinventorydesktop.testutil.TestUtils.RANDOM;
import easyinventorydesktop.testutil.UnitUtils;
import easyinventorydesktop.testutil.UserUtils;
import easyinventorydesktop.testutil.WarehouseUtils;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class ScreenshotTest {
    
    private final Item UTP_CABLE=new Item("utp_cable","UTP kábel",new Unit("m","Méter"));

    private final Unit unit=new Unit("inch"+RANDOM,"Hüvelyk");
    private final Item item=new Item("computer"+RANDOM,"Számítógép",new Unit("unit","Darab"));
    private final Warehouse warehouse=new Warehouse("WH_sátoraljaújhely"+RANDOM,"Sátoraljaújhelyi telephely","nekeresd utca 1.");
    private final Warehouse warehouse2=new Warehouse("WH_sopron","Soproni telephely","Sopron Kárpát u. 7.");
    private final Storage storage=new Storage(warehouse,"SAT0","Sátoraljaújhely 0");
    private final Item[] limit_items={
            item,
            UTP_CABLE
    };
    private final int[] limit_amounts={15,100};
    private final Operation operation=new Operation("OP0001","Művelet 1",true,new OperationItem[]{
            new OperationItem(item,1,storage,10001+RANDOM,"SZ0001",""),
            new OperationItem(item,1,storage,10002+RANDOM,"SZ0002",""),
            new OperationItem(item,1,storage,10003+RANDOM,"SZ0003",""),
            new OperationItem(UTP_CABLE,1,storage,0,"",""),
    });
    private final User user=new User("anna"+RANDOM,"Anna","anna",new User("admin","admin","admin",null));
    private final String screenshot_prefix="";
    

    @Test
    public void login() {        
        LoginUtils.loginAs(new User("admin","admin","admin",null), screenshot_prefix+"login.png");
    }
    
    
    @Test
    public void BA_units_list() {
        LoginUtils.doLogin();
        UnitUtils.gotoUnits();
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .screenshotWindow(screenshot_prefix+"unit.list.png");
    }

    @Test
    public void BB_items_list() {
        LoginUtils.doLogin();
        ItemUtils.gotoItems();
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .screenshotWindow(screenshot_prefix+"item.list.png");
    }

    @Test
    public void BC_warehouses_list() {
        LoginUtils.doLogin();
        WarehouseUtils.gotoWarehouses();
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .screenshotWindow(screenshot_prefix+"warehouse.list.png");
    }

    @Test
    public void BD_storages_list() {
        LoginUtils.doLogin();
        StorageUtils.gotoStorages(warehouse2);
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .screenshotWindow(screenshot_prefix+"storage.list.png");
    }

    @Test
    public void BE_operations_list() {
        LoginUtils.doLogin();
        OperationUtils.gotoOperations(warehouse2);
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .screenshotWindow(screenshot_prefix+"operation.list.png");
    }

    @Test
    public void BF_users_list() {
        LoginUtils.doLogin();
        UserUtils.gotoUsers();
        NodeMatcher.allWindowRoots()
                .descendants()
                .tabPanes()
                .screenshotWindow(screenshot_prefix+"user.list.png");
    }



    @Test
    public void CA_unit_add() {
        LoginUtils.doLogin();
        UnitUtils.createUnit(unit,screenshot_prefix+"unit.add.png");
    }

    @Test
    public void CB_item_add() {
        LoginUtils.doLogin();
        ItemUtils.createItem(item,screenshot_prefix+"item.add.png",screenshot_prefix+"unit.select.png");
    }

    @Test
    public void CC_warehouse_add() {
        LoginUtils.doLogin();
        WarehouseUtils.createWarehouse(warehouse,screenshot_prefix+"warehouse.add.png");
    }

    @Test
    public void CD_storage_add() {
        LoginUtils.doLogin();
        StorageUtils.createStorage(storage,screenshot_prefix+"storage.add.png");
    }

    @Test
    public void CE_user_add() {
        LoginUtils.doLogin();
        UserUtils.createUser(user,screenshot_prefix+"user.add.png",screenshot_prefix+"user.select.png");
    }

    @Test
    public void CF_storage_limit() {
        LoginUtils.doLogin();
        for(int i=0;i<limit_items.length;i++)
            StorageLimitTest.limitStorage(storage,limit_items[i],limit_amounts[i],
                    i==0?screenshot_prefix+"storage.limit.png":null,i==0?screenshot_prefix+"storage.limit.edit.png":null);
    }



    @Test
    public void DA_operation_add_cancel() {
        LoginUtils.doLogin();
        OperationUtils.createOperation(warehouse,operation,screenshot_prefix+"operation.add.png",
                "item.select.png","storage.select.png","operation.item.add.png");
        OperationUtils.viewOperation(warehouse,operation,"operation.view.png");
        OperationUtils.cancelOperation(warehouse,operation,"operation.cancel.png");
    }

    @Test
    public void DB_operation_add_commit() {
        LoginUtils.doLogin();
        OperationUtils.createOperation(warehouse,operation,null,null,null,null);
        OperationUtils.commitOperation(warehouse,operation,"operation.commit.png");
    }



    @Test
    public void EA_storage_capacity() {
        LoginUtils.doLogin();
        StorageCapacityTest.checkCapacity(storage,item,"3/15 (20%)","storage.capacity.png");
    }

    @Test
    public void EB_withdraw_items() {
        LoginUtils.doLogin();
        Operation op=new Operation(operation.id+"rev",operation.name,false,operation.items);
        OperationUtils.createOperation(warehouse,op,null,null,null,null);
        OperationUtils.commitOperation(warehouse,op,null);
    }
    
    @Test
    public void EC_user_authorize() {
        LoginUtils.doLogin();
        UserUtils.setUserSystemAuthorization(user, new String[]{"view_warehouses","delete_types"}, screenshot_prefix+"authorizations.system.png");
        UserUtils.setUserLocalAuthorization(user, warehouse, new String[]{"view","create_add_operation"},
                screenshot_prefix+"warehouse.select.png",
                screenshot_prefix+"authorizations.local.png");
    }




    @Test
    public void XA_unit_edit() {
        LoginUtils.doLogin();
        UnitUtils.modifyUnit(unit,new Unit(unit.id+" ",unit.name),screenshot_prefix+"unit.edit.png");
    }

    @Test
    public void XB_item_edit() {
        LoginUtils.doLogin();
        ItemUtils.modifyItem(item,new Item(item.id+" ",item.name,item.unit),screenshot_prefix+"item.edit.png",null);
    }

    @Test
    public void XC_storage_edit() {
        LoginUtils.doLogin();
        StorageUtils.modifyStorage(storage,new Storage(warehouse,storage.id+" ",storage.name),screenshot_prefix+"storage.edit.png");
    }
    
    @Test
    public void XD_warehouse_edit() {
        LoginUtils.doLogin();
        WarehouseUtils.modifyWarehouse(warehouse,new Warehouse(warehouse.id+" ",warehouse.name,warehouse.address),screenshot_prefix+"warehouse.edit.png");
    }

    @Test
    public void XE_user_edit() {
        LoginUtils.doLogin();
        UserUtils.modifyUser(user,new User(user.id+" ",user.name,user.password,user.manager),screenshot_prefix+"user.edit.png",null);
    }




    

    @Test
    public void YB_storage_delete() {
        LoginUtils.doLogin();
        StorageUtils.deleteStorage(new Storage(new Warehouse(warehouse.id+" ",warehouse.name,warehouse.address),storage.id+" ",storage.name),screenshot_prefix+"storage.delete.png");
    }

    @Test
    public void YC_warehouse_delete() {
        LoginUtils.doLogin();
        WarehouseUtils.deleteWarehouse(new Warehouse(warehouse.id+" ",warehouse.name,warehouse.address),screenshot_prefix+"warehouse.delete.png");
    }

    @Test
    public void YD_unit_delete() {
        LoginUtils.doLogin();
        UnitUtils.deleteUnit(new Unit(unit.id+" ",unit.name),screenshot_prefix+"unit.delete.png");
    }

    @Test
    public void YE_item_delete() {
        LoginUtils.doLogin();
        ItemUtils.deleteItem(new Item(item.id+" ",item.name,item.unit),screenshot_prefix+"item.delete.png");
    }

    @Test
    public void YF_user_delete() {
        LoginUtils.doLogin();
        UserUtils.deleteUser(new User(user.id+" ",user.name,null,user.manager),screenshot_prefix+"user.delete.png");
    }
    
    @Test
    public void ZA_search() {
        LoginUtils.doLogin();
        SearchUtils.search(null,null,"",false,false,false,false,screenshot_prefix+"search.png");
    }
    
}
