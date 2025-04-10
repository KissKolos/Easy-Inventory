package easyinventoryapi;

import java.util.Objects;
import org.json.*;


public class User implements ToJSON {
    public final String id;
    public final String name;
    public final String password;
    public final User manager;
    
    public User(String id,String name,String password,User manager) {
        this.id=id;
        this.name=name;
        this.password=password;
        this.manager=manager;
    }
    
    public User(JSONObject o) throws JSONException {
        this(o.getString("id"),
            o.getString("name"),
            null,
            o.isNull("manager")?null:new User(o.getJSONObject("manager")));
    }
    
    @Override
    public JSONObject toJSON() {
        return new JSONObject()
            .put("id", id)
            .put("name", name)
            .put("password", password)
            .put("manager", manager==null?null:manager.toJSON());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.password);
        hash = 41 * hash + Objects.hashCode(this.manager);
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
        final User other = (User) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        return Objects.equals(this.manager, other.manager);
    }
}
