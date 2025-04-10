/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventoryapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 3041TAN-06
 */
public class StorageCapacity {
    
    public final Item item;
    public final int stored_amount;
    public final int limit;
    
    public StorageCapacity(JSONObject o) throws JSONException {
        this.item=new Item(o.getJSONObject("item"));
        this.stored_amount=o.getInt("stored_amount");
        this.limit=o.getInt("limit");
    }
    
}
