/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventorydesktop.testutil.TestUtils;
import easyinventoryapi.Unit;
import easyinventorydesktop.testutil.LoginUtils;
import static easyinventorydesktop.testutil.UnitUtils.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class UnitTest {
    
    @Test
    public void listUnits() {
        gotoUnits();
        
        /*NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("add-button")
                .screenshotWindow("unit.list.png");*/
        
        selectUnitCard(new Unit("m","Méter")).exists();
    }
    
    @DataProvider(name = "unit_data")
    public static Object[][] createData() {
        return new Object[][]{
            {
                new Unit("test_unit"+TestUtils.RANDOM,"Test Unit"),
                new Unit("test_unit2"+TestUtils.RANDOM,"Test Unit2")
            },
            {
                new Unit("特别的"+TestUtils.RANDOM,"Test Unit"),
                new Unit("test_unit2特别的"+TestUtils.RANDOM,"特别的")
            }
        };
    }
    
    @Test(dataProvider = "unit_data", dataProviderClass = UnitTest.class)
    public void addUnit(Unit new_unit,Unit modified_unit) {
        LoginUtils.doLogin();
        createUnit(new_unit,null);
    }
    
    @Test(dependsOnMethods={"addUnit"},dataProvider = "unit_data", dataProviderClass = UnitTest.class)
    public void editUnit(Unit new_unit,Unit modified_unit) {
        LoginUtils.doLogin();
        modifyUnit(new_unit,modified_unit,null);
    }
    
    @Test(dependsOnMethods={"editUnit"},dataProvider = "unit_data", dataProviderClass = UnitTest.class)
    public void deleteUnitTest(Unit new_unit,Unit modified_unit) {
        LoginUtils.doLogin();
        deleteUnit(modified_unit,null);
    }
    
    @Test(dependsOnMethods={"deleteUnitTest"},dataProvider = "unit_data", dataProviderClass = UnitTest.class)
    public void checkArchivedUnit(Unit new_unit,Unit modified_unit) {
        LoginUtils.doLogin();
        gotoUnits();
        TestUtils.listArchived();
        NodeMatcher card=selectUnitCard(modified_unit);
        card.exists();
        
        card.descendants()
                .withClass("delete-button")
                .doesNotExists();
        
        card.descendants()
                .withClass("edit-button")
                .doesNotExists();
    }
}
