/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javafx.scene.Node;
import javafx.scene.control.Alert;

/**
 *
 * @author 3041TAN-08
 */
public class UIRegistry {
    
    private final HashMap<String,List<Node>> nodes=new HashMap<>();
    private Alert error=null;
    
    public void register(String code,Node n) {
        if(!nodes.containsKey(code))
            nodes.put(code, new ArrayList<>());
        nodes.get(code).add(n);
    }
    
    public void registerError(Alert a) {
        error=a;
    }
    
    public List<Node> getNodes(String id) {
        if(!nodes.containsKey(id))
            return new ArrayList<>();
        
        List<Node> ns=nodes.get(id);
        for(int i=0;i<ns.size();i++)
            if(ns.get(i).getScene()==null||!ns.get(i).getScene().getWindow().isShowing()){
                ns.remove(i);
                i--;
            }
        
        return ns;
    }
    
    public boolean checkError() {
        return error!=null;
    }
    
    public void consumeError() {
        error.close();
        error=null;
    }
    
    public void dump() {
        System.err.println("----");
        for(Entry<String,List<Node>> e:nodes.entrySet()) {
            System.err.println(e.getKey()+":");
            for(Node n:e.getValue()) {
                System.err.println(n);
            }
        }
        System.err.println("----");
    }
    
}
