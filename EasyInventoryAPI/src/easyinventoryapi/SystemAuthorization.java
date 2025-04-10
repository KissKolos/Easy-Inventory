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
 * @author 3041TAN-06
 */
public class SystemAuthorization extends Authorization {
    
    public static final String[] AUTHORIZATIONS=new String[]{
        "view_warehouses",
        "delete_warehouses",
        "create_warehouses",
        "modify_warehouses",
        "delete_types",
        "create_types",
        "modify_types",
        "view_users",
        "delete_users",
        "create_users",
        "modify_users"};
    
    public SystemAuthorization(JSONArray o) throws JSONException {
        super(o);
    }

    @Override
    public String[] getAll() {
        return AUTHORIZATIONS;
    }
    
}
