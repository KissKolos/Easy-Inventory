package easyinventoryapi;

import java.util.Objects;
import org.json.*;

/**
 *
 * @author 3041TAN-06
 */
public class Item implements ToJSON {
    public final String id;
    public final String name;
    public final Unit unit;
    public final long deleted;
    
    public Item(String id,String name,Unit unit,long deleted) {
        this.id=id;
        this.name=name;
        this.unit=unit;
        this.deleted=deleted;
    }

    public Item(String id,String name,Unit unit) {
        this.id=id;
        this.name=name;
        this.unit=unit;
        this.deleted=Long.MAX_VALUE;
    }
    
    public Item(JSONObject o) throws JSONException {
        this(o.getString("id"),
            o.getString("name"),
            new Unit(o.getJSONObject("unit")),
            o.optLong("deleted",Long.MAX_VALUE));
    }
    
    @Override
    public JSONObject toJSON() {
        return new JSONObject()
            .put("id", id)
            .put("name", name)
            .put("unit", unit.toJSON())
            .put("deleted", deleted==Long.MAX_VALUE?null:deleted);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.unit);
        hash = 53 * hash + (int) (this.deleted ^ (this.deleted >>> 32));
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
        final Item other = (Item) obj;
        if (this.deleted != other.deleted) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.unit, other.unit);
    }
    
    
}
