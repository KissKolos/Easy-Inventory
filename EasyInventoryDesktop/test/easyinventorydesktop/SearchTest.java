/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.testutil.NodeMatcher;
import easyinventoryapi.Item;
import easyinventoryapi.ItemStack;
import easyinventoryapi.Storage;
import easyinventoryapi.Unit;
import easyinventoryapi.Warehouse;
import easyinventorydesktop.testutil.SearchUtils;
import static easyinventorydesktop.testutil.TestUtils.sleep;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author 3041TAN-08
 */
public class SearchTest {
    
    
    
    public static ObservableList<ItemStack> getSearchResults() {
        return ((TableView<ItemStack>)NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("search-table")
                .get()).getItems();
    }
    
    @DataProvider(name = "search_data")
    public static Object[][] createData() {
        Warehouse WH_ajka=new Warehouse("WH_ajka","Ajkai telephely","Ajka Eötvös út 57.");
        Warehouse WH_fot=new Warehouse("WH_fot","Fóti telephely","Fót Szent István utca 48.");

        Storage ST_fot1=new Storage(WH_fot,"FOT0","Fót 1/1");
        Storage ST_ajka1=new Storage(WH_ajka,"AJK0","Ajka 1/1");
        Storage ST_ajka2=new Storage(WH_ajka,"AJK21","Ajka 4/4");
        Storage ST_ajka3=new Storage(WH_ajka,"AJK47","Ajka 8/6");

        Item IT1=new Item("steel_wire_12","Acélkábel 12mm",new Unit("m","Méter"));
        Item IT2=new Item("steel_wire_3","Acélkábel 3mm",new Unit("m","Méter"));
        Item IT3=new Item("petrol_95","Benzin (95)",new Unit("l","Liter"));
        Item IT4=new Item("beans","Bab",new Unit("tincan","Konzervdoboz"));
        Item IT5=new Item("suv_car","SUV autó",new Unit("unit","Darab"));
        
        return new Object[][]{
                {
                        null,
                        null,
                        "",
                        true,
                        true,
                        true,
                        true,
                        0,0,
                        new ItemStack(WH_ajka,ST_ajka1,IT1,
                                "",null,16,16,0)
                },
                {
                        null,
                        null,
                        "",
                        true,
                        true,
                        true,
                        true,
                        1,3,
                        new ItemStack(WH_ajka,ST_ajka2,IT4,
                                "Lot_9_1",null,21,21,0)
                },
                {
                        null,
                        null,
                        "SUV",
                        true,
                        true,
                        true,
                        true,
                        0,0,
                        new ItemStack(WH_ajka,ST_ajka3,IT5,
                                "","40817",1,1,5)
                },
                {
                        WH_fot,
                        null,
                        "acél",
                        false,
                        false,
                        false,
                        false,
                        0,0,
                        new ItemStack(null,null,IT2,
                                null,null,112,90,0)
                },
                {
                        WH_fot,
                        ST_fot1,
                        "",
                        false,
                        false,
                        false,
                        false,
                        0,0,
                        new ItemStack(null,null,IT3,
                                null,null,21,6,0)
                }
        };
    }
    
    
    
    @Test(dataProvider = "search_data", dataProviderClass = SearchTest.class)
    public void doSearch(Warehouse wh,Storage st,String query,boolean show_warehouse,boolean show_storage,boolean show_lot,boolean show_serial,int page_prev,int page_next,ItemStack out) {
        SearchUtils.search(wh,st,query,show_warehouse,show_storage,show_lot,show_serial,null);
        
        for(int i=0;i<page_next;i++){
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("next-button")
                .click();
            
            sleep();
        }

        for(int i=0;i<page_prev;i++){
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("prev-button")
                .click();
        
            sleep();
        }
        
        sleep();
        
        /*if(screenshot!=null)
            NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("search-button")
                .screenshotWindow(screenshot);*/
        
        for(ItemStack i:getSearchResults())
            System.out.println(i.toJSON().toString());
        System.out.println(out.toJSON().toString());
        
        Assert.assertTrue(getSearchResults().contains(out));
    }
    
    
}
