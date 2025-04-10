/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventoryapi;

import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author 3041TAN-08
 */
public abstract class Authorization {
    
    private final String[] granted;
    
    Authorization(JSONArray granted) throws JSONException {
        this.granted=new String[granted.length()];
        for(int i=0;i<granted.length();i++)
            this.granted[i]=granted.getString(i);
    }
    
    public boolean isGranted(String authorization) {
        for (String g : granted)
            if (g.equals(authorization))
                return true;
        return false;
    }
    
    public abstract String[] getAll();
}
