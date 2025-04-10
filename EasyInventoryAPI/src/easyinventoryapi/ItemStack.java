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
public class ItemStack implements ToJSON {
    
    public final Warehouse warehouse;
    public final Storage storage;
    public final Item item;
    public final String lot;
    public final String manufacturer_serial;
    public final int amount;
    public final int available_amount;
    public final int global_serial;
    
    public ItemStack(Warehouse warehouse,Storage storage,Item item,String lot,
            String manufacturer_serial,int amount,int available_amount,int global_serial) {
        this.warehouse=warehouse;
        this.storage=storage;
        this.item=item;
        this.lot=lot;
        this.manufacturer_serial=manufacturer_serial;
        this.amount=amount;
        this.available_amount=available_amount;
        this.global_serial=global_serial;
    }
    
    public ItemStack(JSONObject o) throws JSONException {
        this(
            o.isNull("warehouse")?null:new Warehouse(o.getJSONObject("warehouse")),
            o.isNull("storage")?null:new Storage(o.getJSONObject("storage")),
            new Item(o.getJSONObject("item")),
            o.isNull("lot")?null:o.getString("lot"),
            o.isNull("manufacturer_serial")?null:o.getString("manufacturer_serial"),
            o.getInt("amount"),
            o.getInt("available_amount"),
            o.optInt("global_serial",0)
        );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.warehouse);
        hash = 79 * hash + Objects.hashCode(this.storage);
        hash = 79 * hash + Objects.hashCode(this.item);
        hash = 79 * hash + Objects.hashCode(this.lot);
        hash = 79 * hash + Objects.hashCode(this.manufacturer_serial);
        hash = 79 * hash + this.amount;
        hash = 79 * hash + this.available_amount;
        hash = 79 * hash + this.global_serial;
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
        final ItemStack other = (ItemStack) obj;
        if (this.amount != other.amount) {
            return false;
        }
        if (this.available_amount != other.available_amount) {
            return false;
        }
        if (this.global_serial != other.global_serial) {
            return false;
        }
        if (!Objects.equals(this.warehouse, other.warehouse)) {
            return false;
        }
        if (!Objects.equals(this.storage, other.storage)) {
            return false;
        }
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        if (!Objects.equals(this.lot, other.lot)) {
            return false;
        }
        return Objects.equals(this.manufacturer_serial, other.manufacturer_serial);
    }

    @Override
    public JSONObject toJSON() {
        return new JSONObject()
            .putOpt("warehouse", warehouse==null?null:warehouse.toJSON())
            .putOpt("storage", storage==null?null:storage.toJSON())
            .put("item", item.toJSON())
            .put("lot", lot)
            .put("manufacturer_serial", manufacturer_serial)
            .put("global_serial", global_serial)
            .put("amount", amount)
            .put("available_amount", available_amount);
    }
    
}
