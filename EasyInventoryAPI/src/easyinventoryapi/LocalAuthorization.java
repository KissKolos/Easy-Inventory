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
public class LocalAuthorization extends Authorization {
    
    public static final String[] AUTHORIZATIONS=new String[]{"view","create_add_operation","create_remove_operation","handle_operation","configure"};
    
    public LocalAuthorization(JSONArray o) throws JSONException {
        super(o);
    }

    @Override
    public String[] getAll() {
        return AUTHORIZATIONS;
    }
    
}
