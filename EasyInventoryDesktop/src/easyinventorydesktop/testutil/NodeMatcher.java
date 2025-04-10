/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop.testutil;

import com.sun.javafx.robot.impl.FXRobotHelper;
import easyinventorydesktop.testutil.TestUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author 3041TAN-08
 */
public class NodeMatcher {
    
    private final ArrayList<Node> nodes;
    
    private NodeMatcher(ArrayList<Node> nodes) {
        this.nodes=nodes;
    }
    
    private NodeMatcher(Node node) {
        this(new ArrayList<>());
        nodes.add(node);
    }
    
    public static NodeMatcher allWindowRoots() {
        ArrayList<Node> nodes=new ArrayList<>();
        for(Stage s:FXRobotHelper.getStages())
            nodes.add(s.getScene().getRoot());
        return new NodeMatcher(nodes);
    }
    
    private static void descendants(Node n,ArrayList<Node> out) {
        out.add(n);
        if((n instanceof Parent)&&n.isVisible())
            for(Node c:((Parent)n).getChildrenUnmodifiable())
                descendants(c,out);
    }
    
    public NodeMatcher descendants() {
        ArrayList<Node> nnodes=new ArrayList<>();
        for(Node n:nodes)
            descendants(n,nnodes);
        return new NodeMatcher(nnodes);
    }
    
    public NodeMatcher withClass(String classname) {
        ArrayList<Node> nnodes=new ArrayList<>();
        for(Node n:nodes)
            if(n.getStyleClass().contains(classname))
                nnodes.add(n);
        return new NodeMatcher(nnodes);
    }
    
    public NodeMatcher labels(String text) {
        ArrayList<Node> nnodes=new ArrayList<>();
        for(Node n:nodes)
            if((n instanceof Label)&&((Label)n).getText().equals(text))
                nnodes.add(n);
        return new NodeMatcher(nnodes);
    }
    
    public NodeMatcher textFields() {
        ArrayList<Node> nnodes=new ArrayList<>();
        for(Node n:nodes)
            if(n instanceof TextField)
                nnodes.add(n);
        return new NodeMatcher(nnodes);
    }
    
    public NodeMatcher tabPanes() {
        ArrayList<Node> nnodes=new ArrayList<>();
        for(Node n:nodes)
            if(n instanceof TabPane)
                nnodes.add(n);
        return new NodeMatcher(nnodes);
    }
    
    public NodeMatcher buttons() {
        ArrayList<Node> nnodes=new ArrayList<>();
        for(Node n:nodes)
            if(n instanceof Button)
                nnodes.add(n);
        return new NodeMatcher(nnodes);
    }
    
    public NodeMatcher with(Function<NodeMatcher,NodeMatcher> m) {
        ArrayList<Node> nnodes=new ArrayList<>();
        for(Node n:nodes)
            if(!m.apply(new NodeMatcher(n)).nodes.isEmpty())
                nnodes.add(n);
        return new NodeMatcher(nnodes);
    }
    
    public static void sync(Runnable r) {
        Object o=TestUtils.class;
        Platform.runLater(()->{
            r.run();
            synchronized(o) {
                o.notify();
            }
        });
        synchronized(o) {
            try{
                o.wait(1000);
            }catch(InterruptedException e) {}
        }
    }
    
    public Node get() {
        if(nodes.size()>1){
            for(Node n:nodes) {
                n.setStyle("-fx-background: red;");
            }
            
            Thread.dumpStack();
            try{Thread.sleep(10000000);}catch(InterruptedException e){}
            //throw new RuntimeException("more than one nodes matched");
        }
        if(nodes.isEmpty())
            throw new RuntimeException("0 nodes matched");
        
        return nodes.get(0);
    }
    
    public int count() {
        return nodes.size();
    }
    
    public void replaceText(String text) {
        Node n=get();
        
        if(n instanceof TextField)
            sync(()->{
                ((TextField)n).setText(text);
                if(((TextField)n).onKeyReleasedProperty().get()!=null)
                    ((TextField)n).onKeyReleasedProperty().get().handle(null);
            });
        else
            throw new RuntimeException("not a textfield");
    }
    
    public void replaceNumber(int value) {
        Node n=get();
        
        if(n instanceof Spinner)
            sync(()->((Spinner<Integer>)n).getValueFactory().setValue(value));
        else
            throw new RuntimeException("not a spinner");
    }
    
    public void click() {
        Node n=get();
        
        if(n instanceof Button)
            sync(()->((Button)n).fire());
        else if(n instanceof CheckBox)
            sync(()->((CheckBox)n).fire());
        else
            throw new RuntimeException("not a button");
    }
    
    public void clickASync() {
        Node n=get();
        
        if(n instanceof Button)
            Platform.runLater(()->((Button)n).fire());
        else
            throw new RuntimeException("not a button");
    }
    
    public void selectTab(int index) {
        Node n=get();
        
        if(n instanceof TabPane)
            sync(()->((TabPane)n).getSelectionModel().select(index));
        else
            throw new RuntimeException("not a tabpane");
    }
    
    public void exists() {
         if(nodes.isEmpty())
            throw new RuntimeException("0 nodes matched");
    }
    
    public void doesNotExists() {
         if(!nodes.isEmpty())
            throw new RuntimeException("more than 0 nodes matched");
    }
    
    public void acceptDialog() {
        Node n=get();
        
        if(n instanceof DialogPane)
            sync(()->((Button)((DialogPane)n).lookupButton(ButtonType.OK)).fire());
        else
            throw new RuntimeException("not a dialogpane");
    }
    
    public void closeDialogs() {
        for(Node n:nodes) {
            if(n instanceof DialogPane){
                Button close=((Button)((DialogPane)n).lookupButton(ButtonType.CLOSE));
                Button cancel=((Button)((DialogPane)n).lookupButton(ButtonType.CANCEL));
                Button ok=((Button)((DialogPane)n).lookupButton(ButtonType.OK));
                
                if(close!=null)
                    sync(()->close.fire());
                else if(cancel!=null)
                    sync(()->cancel.fire());
                else if(ok!=null)
                    sync(()->ok.fire());
            }
        }
    }
    
    public void print() {
        System.out.println(nodes.size());
        for(Node n:nodes)
            System.out.println(n);
    }
    
    public void screenshotWindow(String out) {
        Node n=get();
        sync(()->{
            try{
                WritableImage img=n.getScene().getRoot().snapshot(null, null);
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File("screenshot_out/"+out));
            }catch(IOException e) {
                e.printStackTrace();
            }
        });
    }
    
}
