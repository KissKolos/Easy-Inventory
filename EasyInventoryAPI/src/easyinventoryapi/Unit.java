package easyinventoryapi;

import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 3041TAN-06
 */
public class Unit implements ToJSON {
    public final String id;
    public final String name;
    public final long deleted;
    
    public Unit(String id,String name,long deleted) {
        this.id=id;
        this.name=name;
        this.deleted=deleted;
    }

    public Unit(String id,String name) {
        this.id=id;
        this.name=name;
        this.deleted=Long.MAX_VALUE;
    }
    
    public Unit(JSONObject o) throws JSONException {
        this(o.getString("id"),o.getString("name"),o.optLong("deleted",Long.MAX_VALUE));
    }
    
    @Override
    public JSONObject toJSON() {
        return new JSONObject()
            .put("id", id)
            .put("name", name)
            .put("deleted", deleted==Long.MAX_VALUE?null:deleted);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + (int) (this.deleted ^ (this.deleted >>> 32));
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
        final Unit other = (Unit) obj;
        if (this.deleted != other.deleted) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }
}
