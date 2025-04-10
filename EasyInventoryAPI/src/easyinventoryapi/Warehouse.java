package easyinventoryapi;

import java.util.Objects;
import org.json.*;

/**
 *
 * @author 3041TAN-06
 */
public class Warehouse implements ToJSON {
    
    public final String id;
    public final String name;
    public final String address;
    public final long deleted;
    
    public Warehouse(String id,String name,String address,long deleted) {
        this.id=id;
        this.name=name;
        this.address=address;
        this.deleted=deleted;
    }

    public Warehouse(String id,String name,String address) {
        this.id=id;
        this.name=name;
        this.address=address;
        this.deleted=Long.MAX_VALUE;
    }
    
    public Warehouse(JSONObject o) throws JSONException {
        this(o.getString("id"),
            o.getString("name"),
            o.optString("address",null),
            o.optLong("deleted",Long.MAX_VALUE));
    }
    
    @Override
    public JSONObject toJSON() {
        return new JSONObject()
            .put("id", id)
            .put("name", name)
            .put("address", address)
            .put("deleted", deleted==Long.MAX_VALUE?null:deleted);
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
        final Warehouse other = (Warehouse) obj;
        if (this.deleted != other.deleted) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.address, other.address);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.address);
        hash = 97 * hash + (int) (this.deleted ^ (this.deleted >>> 32));
        return hash;
    }
    
    
}
