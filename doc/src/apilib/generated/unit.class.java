public class Unit implements ToJSON {
    public final String id;
    public final String name;
    public final long deleted;
    public Unit(String id,String name,long deleted) { ... }
    public Unit(String id,String name) { ... }
    public Unit(JSONObject o) throws JSONException { ... }
    @Override
    public JSONObject toJSON() { ... }
    @Override
    public int hashCode() { ... }
    @Override
    public boolean equals(Object obj) { ... }
}
