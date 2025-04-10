package easyinventoryapi;

import java.util.Arrays;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 3041TAN-06
 */
public class Operation implements ToJSON {
    
    public final String id;
    public final String name;
    public final boolean is_add;
    public final OperationItem[] items;
    public final long deleted;
    
    public Operation(String id,String name,boolean is_add,OperationItem[] items,long deleted) {
        this.id=id;
        this.name=name;
        this.is_add=is_add;
        this.items=items;
        this.deleted=deleted;
    }

    public Operation(String id,String name,boolean is_add,OperationItem[] items) {
        this.id=id;
        this.name=name;
        this.is_add=is_add;
        this.items=items;
        this.deleted=Long.MAX_VALUE;
    }
    
    private static OperationItem[] parseItems(JSONArray o) throws JSONException {
        OperationItem[] v=new OperationItem[o.length()];
        for(int i=0;i<o.length();i++) {
            v[i]=new OperationItem(o.getJSONObject(i));
        }
        return v;
    }
    
    public Operation(JSONObject o) throws JSONException {
        this(o.getString("id"),
            o.getString("name"),
            o.getBoolean("is_add"),
            parseItems(o.getJSONArray("items")),
            o.optLong("commited",Long.MAX_VALUE)
        );
    }
    
    @Override
    public JSONObject toJSON() {
        JSONArray a=new JSONArray();
        for(OperationItem i:items)
            a.put(i.toJSON());
        return new JSONObject()
            .put("id", id)
            .put("name", name)
            .put("is_add", is_add)
            .put("items", a)
            .put("commited", deleted==Long.MAX_VALUE?null:deleted);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.id);
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + (this.is_add ? 1 : 0);
        hash = 17 * hash + Arrays.deepHashCode(this.items);
        hash = 17 * hash + (int) (this.deleted ^ (this.deleted >>> 32));
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
        final Operation other = (Operation) obj;
        if (this.is_add != other.is_add) {
            return false;
        }
        if (this.deleted != other.deleted) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Arrays.deepEquals(this.items, other.items);
    }
    
}
