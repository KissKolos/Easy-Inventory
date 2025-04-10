package easyinventoryapi;

import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 3041TAN-06
 */
public class Storage implements ToJSON {
    
    public final String id;
    public final String name;
    public final Warehouse warehouse;
    public final long deleted;
    
    public Storage(Warehouse warehouse,String id,String name,long deleted) {
        this.id=id;
        this.name=name;
        this.deleted=deleted;
        this.warehouse=warehouse;
    }

    public Storage(Warehouse warehouse,String id,String name) {
        this(warehouse,id,name,Long.MAX_VALUE);
    }
    
    public Storage(JSONObject o) throws JSONException {
        this(
            new Warehouse(o.getJSONObject("warehouse")),
            o.getString("id"),
            o.getString("name"),
            o.optLong("deleted",Long.MAX_VALUE));
    }
    
    @Override
    public JSONObject toJSON() {
        return new JSONObject()
            .put("id", id)
            .put("name", name)
            .put("warehouse", warehouse.toJSON())
            .put("deleted", deleted==Long.MAX_VALUE?null:deleted);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.warehouse);
        hash = 29 * hash + (int) (this.deleted ^ (this.deleted >>> 32));
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
        final Storage other = (Storage) obj;
        if (this.deleted != other.deleted) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.warehouse, other.warehouse);
    }
    
}
