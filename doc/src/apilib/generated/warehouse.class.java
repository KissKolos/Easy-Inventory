public class Warehouse implements ToJSON {
    public final String id;
    public final String name;
    public final String address;
    public final long deleted;
    public Warehouse(String id,String name,String address,long deleted) { ... }
    public Warehouse(String id,String name,String address) { ... }
    public Warehouse(JSONObject o) throws JSONException { ... }
    @Override
    public JSONObject toJSON() { ... }
    @Override
    public boolean equals(Object obj) { ... }
    @Override
    public int hashCode() { ... }
}
