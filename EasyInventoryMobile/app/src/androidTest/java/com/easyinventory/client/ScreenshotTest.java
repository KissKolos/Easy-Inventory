package com.easyinventory.client;

import static com.easyinventory.client.TestSuite.RANDOM;
import static com.easyinventory.client.TestSuite.screenshot;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import easyinventoryapi.Item;
import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;
import easyinventoryapi.Storage;
import easyinventoryapi.Unit;
import easyinventoryapi.User;
import easyinventoryapi.Warehouse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
    public void AA_login() {
        LoginTest.loginAs(new User("admin","admin","admin",null),screenshot_prefix+"login.png");
    }



    @Test
    public void BA_units_list() {
        LoginTest.doLogin();
        TestUtils.gotoUnits();
        screenshot(screenshot_prefix+"unit.list.png");
    }

    @Test
    public void BB_items_list() {
        LoginTest.doLogin();
        TestUtils.gotoItems();
        screenshot(screenshot_prefix+"item.list.png");
    }

    @Test
    public void BC_warehouses_list() {
        LoginTest.doLogin();
        TestUtils.gotoWarehouses();
        screenshot(screenshot_prefix+"warehouse.list.png");
    }

    @Test
    public void BD_storages_list() {
        LoginTest.doLogin();
        TestUtils.gotoStorages(warehouse2);
        screenshot(screenshot_prefix+"storage.list.png");
    }

    @Test
    public void BE_operations_list() {
        LoginTest.doLogin();
        TestUtils.gotoOperations(warehouse2);
        screenshot(screenshot_prefix+"operation.list.png");
    }

    @Test
    public void BF_users_list() {
        LoginTest.doLogin();
        TestUtils.gotoUsers();
        screenshot(screenshot_prefix+"user.list.png");
    }



    @Test
    public void CA_unit_add() {
        LoginTest.doLogin();
        UnitTest.createUnit(unit,screenshot_prefix+"unit.add.png");
    }

    @Test
    public void CB_item_add() {
        LoginTest.doLogin();
        ItemsTest.createItem(item,screenshot_prefix+"item.add.png",screenshot_prefix+"unit.select.png");
    }

    @Test
    public void CC_warehouse_add() {
        LoginTest.doLogin();
        WarehouseTest.createWarehouse(warehouse,screenshot_prefix+"warehouse.add.png");
    }

    @Test
    public void CD_storage_add() {
        LoginTest.doLogin();
        StorageTest.createStorage(storage,screenshot_prefix+"storage.add.png");
    }

    @Test
    public void CE_user_add() {
        LoginTest.doLogin();
        UsersTest.createUser(user,screenshot_prefix+"user.add.png",screenshot_prefix+"user.select.png");
    }

    @Test
    public void CF_storage_limit() {
        LoginTest.doLogin();
        for(int i=0;i<limit_items.length;i++)
            StorageLimitTest.limitStorage(storage,limit_items[i],limit_amounts[i],
                    i==0?screenshot_prefix+"storage.limit.png":null,i==0?screenshot_prefix+"storage.limit.edit.png":null);
    }



    @Test
    public void DA_operation_add_cancel() {
        LoginTest.doLogin();
        OperationTest.createOperation(warehouse,operation,screenshot_prefix+"operation.add.png",
                "item.select.png","storage.select.png","operation.item.add.png");
        OperationTest.viewOperation(warehouse,operation,"operation.view.png");
        OperationTest.cancelOperation(warehouse,operation,"operation.cancel.png");
    }

    @Test
    public void DB_operation_add_commit() {
        LoginTest.doLogin();
        OperationTest.createOperation(warehouse,operation,null,null,null,null);
        OperationTest.commitOperation(warehouse,operation,"operation.commit.png");
    }



    @Test
    public void EA_storage_capacity() {
        LoginTest.doLogin();
        StorageLimitTest.checkStorageCapacity(storage,item,3,15,"storage.capacity.png");
    }

    @Test
    public void EB_withdraw_items() {
        LoginTest.doLogin();
        Operation op=new Operation(operation.id+"rev",operation.name,false,operation.items);
        OperationTest.createOperation(warehouse,op,null,null,null,null);
        OperationTest.commitOperation(warehouse,op,null);
    }

    @Test
    public void EC_user_authorize() {
        LoginTest.doLogin();
        UsersTest.setUserSystemAuthorization(user, new String[]{"view_warehouses","delete_types"}, screenshot_prefix+"authorizations.system.png");
        UsersTest.setUserLocalAuthorization(user, warehouse, new String[]{"view","create_add_operation"},
                screenshot_prefix+"warehouse.select.png",
                screenshot_prefix+"authorizations.local.png");
    }


    @Test
    public void XA_unit_edit() {
        LoginTest.doLogin();
        UnitTest.modifyUnit(unit,new Unit(unit.id+"2",unit.name),screenshot_prefix+"unit.edit.png");
    }

    @Test
    public void XB_item_edit() {
        LoginTest.doLogin();
        ItemsTest.modifyItem(item,new Item(item.id+"2",item.name,item.unit),screenshot_prefix+"item.edit.png",null);
    }

    @Test
    public void XC_storage_edit() {
        LoginTest.doLogin();
        StorageTest.modifyStorage(storage,new Storage(warehouse,storage.id+"2",storage.name),screenshot_prefix+"storage.edit.png");
    }

    @Test
    public void XD_warehouse_edit() {
        LoginTest.doLogin();
        WarehouseTest.modifyWarehouse(warehouse,new Warehouse(warehouse.id+"2",warehouse.name,warehouse.address),screenshot_prefix+"warehouse.edit.png");
    }

    @Test
    public void XE_user_edit() {
        LoginTest.doLogin();
        UsersTest.modifyUser(user,new User(user.id+"2",user.name,user.password,user.manager),screenshot_prefix+"user.edit.png",null);
    }




    @Test
    public void YB_storage_delete() {
        LoginTest.doLogin();
        StorageTest.deleteStorage(new Storage(new Warehouse(warehouse.id+"2",warehouse.name,warehouse.address),storage.id+"2",storage.name),screenshot_prefix+"storage.delete.png");
    }

    @Test
    public void YC_warehouse_delete() {
        LoginTest.doLogin();
        WarehouseTest.deleteWarehouse(new Warehouse(warehouse.id+"2",warehouse.name,warehouse.address),screenshot_prefix+"warehouse.delete.png");
    }

    @Test
    public void YD_unit_delete() {
        LoginTest.doLogin();
        UnitTest.deleteUnit(new Unit(unit.id+"2",unit.name),screenshot_prefix+"unit.delete.png");
    }

    @Test
    public void YE_item_delete() {
        LoginTest.doLogin();
        ItemsTest.deleteItem(new Item(item.id+"2",item.name,item.unit),screenshot_prefix+"item.delete.png");
    }

    @Test
    public void YF_user_delete() {
        LoginTest.doLogin();
        UsersTest.deleteUser(new User(user.id+"2",user.name,null,user.manager),screenshot_prefix+"user.delete.png");
    }

    @Test
    public void ZA_search() {
        LoginTest.doLogin();
        SearchTest.search(null,null,"",false,false,false,false,screenshot_prefix+"search.png");
    }

}
