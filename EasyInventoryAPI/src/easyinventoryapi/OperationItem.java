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
public class OperationItem implements ToJSON {
    
    public final Item item;
    public final int amount;
    public final Storage storage;
    public final int global_serial;
    public final String manufacturer_serial;
    public final String lot;
    
    public OperationItem(Item item,int amount,Storage storage,int global_serial,String manufacturer_serial,String lot) {
        this.item=item;
        this.amount=amount;
        this.storage=storage;
        this.global_serial=global_serial;
        this.manufacturer_serial=manufacturer_serial;
        this.lot=lot;
    }
    
    public OperationItem(JSONObject o) throws JSONException {
        this(
            new Item(o.getJSONObject("item")),
            o.getInt("amount"),
            o.isNull("storage")?null:new Storage(o.getJSONObject("storage")),
            o.optInt("global_serial",0),
            o.optString("manufacturer_serial",null),
            o.optString("lot",null)
        );
    }
    
    @Override
    public JSONObject toJSON() {
        return new JSONObject()
            .put("item", item.toJSON())
            .put("amount", amount)
            .put("storage", storage==null?null:storage.toJSON())
            .put("global_serial", global_serial)
            .put("manufacturer_serial", manufacturer_serial)
            .put("lot", lot);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.item);
        hash = 97 * hash + this.amount;
        hash = 97 * hash + Objects.hashCode(this.storage);
        hash = 97 * hash + this.global_serial;
        hash = 97 * hash + Objects.hashCode(this.manufacturer_serial);
        hash = 97 * hash + Objects.hashCode(this.lot);
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
        final OperationItem other = (OperationItem) obj;
        if (this.amount != other.amount) {
            return false;
        }
        if (this.global_serial != other.global_serial) {
            return false;
        }
        if (!Objects.equals(this.manufacturer_serial, other.manufacturer_serial)) {
            return false;
        }
        if (!Objects.equals(this.lot, other.lot)) {
            return false;
        }
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        return Objects.equals(this.storage, other.storage);
    }
}
