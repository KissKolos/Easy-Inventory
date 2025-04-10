/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import easyinventoryapi.API;
import easyinventoryapi.APIException;
import easyinventorydesktop.EasyInventoryDesktop;
import easyinventorydesktop.SimpleHTTPConnector;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.application.Platform;

/**
 *
 * @author 3041TAN-06
 */
public class TestUtils {
    
    public static final int RANDOM=Math.abs(new Random().nextInt());
    
    private static void uploadDB() {
        try{
            String save=new String(Files.readAllBytes(Paths.get("testdata.json")), StandardCharsets.UTF_8);
            API.putDB(new SimpleHTTPConnector(), "http://127.0.0.1:8001/api", save, "tester");
        }catch(APIException | IOException e){
            throw new RuntimeException(e);
        }
    }
    
    public static void launch() {
        boolean running=false;
        try{
            Platform.runLater(()->{});
            running=true;
        }catch(IllegalStateException e){}
        
        if(running) {
            NodeMatcher.allWindowRoots()
                    .descendants()
                    .closeDialogs();
            
            try{
                NodeMatcher.allWindowRoots()
                        .descendants()
                        .withClass("logout-button")
                        .click();
            }catch(RuntimeException e){}
        }else{
            //uploadDB();
            new Thread(()->Application.launch(EasyInventoryDesktop.class)).start();
        }
        
        sleep();
    }
    
    public static void sleep() {
        try{
            Thread.sleep(500);
        }catch(InterruptedException e) {}
    }
    
    public static void listAdd() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("add-button")
                .clickASync();
        
        sleep();
    }
    
    public static void listReload() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("reload-button")
                .click();
        
        sleep();
    }
    
    public static void listArchived() {
        NodeMatcher.allWindowRoots()
                .descendants()
                .withClass("archived-button")
                .click();
        
        sleep();
    }
    
    public static NodeMatcher waitFor(Supplier<NodeMatcher> n) {
        for(int i=0;i<5;i++) {
            NodeMatcher m=n.get();
            if(m.count()>0)
                return m;
            sleep();
        }
        throw new RuntimeException("timeout");
    }
    
    public static void confirm() {
        waitFor(()->NodeMatcher.allWindowRoots().descendants().withClass("confirm-dialog"))
                .acceptDialog();
    }
    
}
