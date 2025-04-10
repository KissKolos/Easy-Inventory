/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventoryapi;

import org.json.JSONObject;

/**
 *
 * @author 3041TAN-08
 */
public class UserInfo {
    
    public final String user;
    public final String username;

    public UserInfo(String user, String username) {
        this.user = user;
        this.username = username;
    }

    public UserInfo(JSONObject o) {
        this(
            o.getString("user"),
            o.getString("username")
        );
    }
    
}
