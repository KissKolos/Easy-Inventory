/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventorydesktop.testutil.TestUtils;
import easyinventoryapi.Operation;
import easyinventoryapi.OperationItem;
import easyinventoryapi.Warehouse;
import easyinventorydesktop.testutil.LoginUtils;
import easyinventorydesktop.testutil.OperationUtils;
import static easyinventorydesktop.testutil.OperationUtils.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class OperationTest {
    
    @Test
    public void listOperations() {
        Warehouse WH1=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        Operation op=new Operation("OP_WH_baja_0","Operation_WH_baja_0",true,new OperationItem[]{});
        
        selectOperationCard(WH1,op);
    }
    
    @DataProvider(name = "operation_data")
    public static Object[][] createData() {
        Warehouse WH1=new Warehouse("WH_baja","Bajai telephely","Baja Piroska u. 70.");
        return new Object[][]{
            {
                WH1,
                new Operation("test_OP"+TestUtils.RANDOM,"test op 1",true,new OperationItem[]{})
            }
        };
    }
    
    @Test(dataProvider = "operation_data", dataProviderClass = OperationTest.class)
    public void addOperation(Warehouse wh,Operation op) {
        LoginUtils.doLogin();
        createOperation(wh,op,null,null,null,null);
    }
    
    @Test(dependsOnMethods={"addOperation"},dataProvider = "operation_data", dataProviderClass = OperationTest.class)
    public void viewOperation(Warehouse wh,Operation op) {
        LoginUtils.doLogin();
        OperationUtils.viewOperation(wh,op,null);
    }
    
    @Test(dependsOnMethods={"viewOperation"},dataProvider = "operation_data", dataProviderClass = OperationTest.class)
    public void deleteOperation(Warehouse wh,Operation op) {
        LoginUtils.doLogin();
        commitOperation(wh,op,null);
    }
    
    @Test(dependsOnMethods={"deleteOperation"},dataProvider = "operation_data", dataProviderClass = OperationTest.class)
    public void checkArchivedItem(Warehouse wh,Operation op) {
        LoginUtils.doLogin();
        gotoOperations(wh);
        TestUtils.listArchived();
        NodeMatcher card=selectOperationCard(wh,op);
        card.exists();
        
        card.descendants()
                .withClass("commit-button")
                .doesNotExists();
        
        card.descendants()
                .withClass("cancel-button")
                .doesNotExists();
    }
    
}
