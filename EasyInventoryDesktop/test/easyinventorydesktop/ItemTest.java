/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventorydesktop.testutil.TestUtils;
import easyinventoryapi.Item;
import easyinventoryapi.Unit;
import static easyinventorydesktop.testutil.ItemUtils.*;
import easyinventorydesktop.testutil.LoginUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class ItemTest {
    
    /*@Test
    public void pageItems() {
        gotoItems();
        
        NodeMatcher.allWindowRoots().descendants().withClass("next-button").click();
        
        sleep();
        
        selectItem(new Item("concrete","Beton",new Unit("m3","Köbméter"))).exists();
        
        NodeMatcher.allWindowRoots().descendants().withClass("prev-button").click();
        
        sleep();
        
        selectItem(new Item("steel","Acél",new Unit("kg","Kilógramm"))).exists();
    }*/
    
    @Test
    public void listItems() {
        gotoItems();
        
        /*NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("add-button")
                .screenshotWindow("item.list.png");*/
        selectItemCard(new Item("square_pattern_carpet_brown","Barna kockás szőnyeg",new Unit("m2","Négyzetméter"))).exists();
    }
    
    @DataProvider(name = "item_data")
    public static Object[][] createData() {
        Unit m=new Unit("m","Méter");
        return new Object[][]{
            {
                new Item("test_item"+TestUtils.RANDOM,"Test item",m),
                new Item("test_item2"+TestUtils.RANDOM,"Test item2",m)
            },
            {
                new Item("特别的"+TestUtils.RANDOM,"Test item",m),
                new Item("test_item特别的"+TestUtils.RANDOM,"特别的",m)
            }
        };
    }
    
    
    
    @Test(dataProvider = "item_data", dataProviderClass = ItemTest.class)
    public void addItem(Item new_item,Item modified_item) {
        LoginUtils.doLogin();
        createItem(new_item,null,null);
    }
    
    @Test(dependsOnMethods={"addItem"},dataProvider = "item_data", dataProviderClass = ItemTest.class)
    public void editItem(Item new_item,Item modified_item) {
        LoginUtils.doLogin();
        modifyItem(new_item,modified_item,null,null);
    }
    
    @Test(dependsOnMethods={"editItem"},dataProvider = "item_data", dataProviderClass = ItemTest.class)
    public void deleteitem(Item new_item,Item modified_item) {
        LoginUtils.doLogin();
        deleteItem(modified_item,null);
    }
    
    @Test(dependsOnMethods={"deleteitem"},dataProvider = "item_data", dataProviderClass = ItemTest.class)
    public void checkArchivedItem(Item new_item,Item modified_item) {
        LoginUtils.doLogin();
        gotoItems();
        TestUtils.listArchived();
        NodeMatcher card=selectItemCard(modified_item);
        card.exists();
        
        card.descendants()
                .withClass("delete-button")
                .doesNotExists();
        
        card.descendants()
                .withClass("edit-button")
                .doesNotExists();
    }
    
}
