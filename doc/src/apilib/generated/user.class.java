public class User implements ToJSON {
    public final String id;
    public final String name;
    public final String password;
    public final User manager;
    public User(String id,String name,String password,User manager) { ... }
    public User(JSONObject o) throws JSONException { ... }
    @Override
    public JSONObject toJSON() { ... }
    @Override
    public int hashCode() { ... }
    @Override
    public boolean equals(Object obj) { ... }
}
