/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventoryapi;

import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 3041TAN-06
 */
public class StorageLimit {
    
    public final Item item;
    public final int amount;
    
    public StorageLimit(JSONObject o) throws JSONException {
        this.item=new Item(o.getJSONObject("item"));
        this.amount=o.getInt("amount");
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.item);
        hash = 67 * hash + this.amount;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StorageLimit other = (StorageLimit) obj;
        if (this.amount != other.amount) {
            return false;
        }
        return Objects.equals(this.item, other.item);
    }
    
    
    
}
