/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventorydesktop.testutil.TestUtils;
import easyinventoryapi.Warehouse;
import easyinventorydesktop.testutil.LoginUtils;
import static easyinventorydesktop.testutil.WarehouseUtils.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class WarehouseTest {
    
    
    
    @DataProvider(name = "warehouse_data")
    public static Object[][] createData() {
        return new Object[][]{
            {
                new Warehouse("test_warehouse"+TestUtils.RANDOM,"Test Warehouse","Test street"),
                new Warehouse("test_warehouse2"+TestUtils.RANDOM,"Test Warehouse2","Test street2")
            },
            {
                new Warehouse("特别的"+TestUtils.RANDOM,"特别的 ★★","★"),
                new Warehouse("sss"+TestUtils.RANDOM,"","Test street2")
            }
        };
    }
    
    /*@Test
    public void pageWarehouses() {
        gotoWarehouses();
        
        NodeMatcher.allWindowRoots().descendants().withClass("next-button").click();
        
        sleep();
        
        selectWarehouse(new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.")).exists();
        
        NodeMatcher.allWindowRoots().descendants().withClass("prev-button").click();
        
        sleep();
        
        selectWarehouse(new Warehouse("WH_oroshaza","Orosházai telephely","Orosháza Frankel Leó út 74.")).exists();
    }*/
    
    @Test
    public void listWarehouses() {
        gotoWarehouses();
        
        /*NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("add-button")
                .screenshotWindow("warehouse.list.png");*/
        
        selectWarehouseCard(new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.")).exists();
    }
    
    
    
    @Test(dataProvider = "warehouse_data", dataProviderClass = WarehouseTest.class)
    public void addWarehouse(Warehouse new_warehouse,Warehouse modified_warehouse) {
        LoginUtils.doLogin();
        createWarehouse(new_warehouse,null);
    }
    
    @Test(dependsOnMethods={"addWarehouse"},dataProvider = "warehouse_data", dataProviderClass = WarehouseTest.class)
    public void editWarehouse(Warehouse new_warehouse,Warehouse modified_warehouse) {
        LoginUtils.doLogin();
        modifyWarehouse(new_warehouse,modified_warehouse,null);
    }
    
    @Test(dependsOnMethods={"editWarehouse"},dataProvider = "warehouse_data", dataProviderClass = WarehouseTest.class)
    public void deleteWarehouseTest(Warehouse new_warehouse,Warehouse modified_warehouse) {
        LoginUtils.doLogin();
        deleteWarehouse(modified_warehouse,null);
    }
    
    @Test(dependsOnMethods={"deleteWarehouseTest"},dataProvider = "warehouse_data", dataProviderClass = WarehouseTest.class)
    public void checkArchivedWarehouse(Warehouse new_warehouse,Warehouse modified_warehouse) {
        LoginUtils.doLogin();
        gotoWarehouses();
        TestUtils.listArchived();
        NodeMatcher card=selectWarehouseCard(modified_warehouse);
        card.exists();
        
        card.descendants()
                .withClass("delete-button")
                .doesNotExists();
        
        card.descendants()
                .withClass("edit-button")
                .doesNotExists();
    }
    
}
