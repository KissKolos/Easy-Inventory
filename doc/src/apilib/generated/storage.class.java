public class Storage implements ToJSON {
    public final String id;
    public final String name;
    public final Warehouse warehouse;
    public final long deleted;
    public Storage(Warehouse warehouse,String id,String name,long deleted) { ... }
    public Storage(Warehouse warehouse,String id,String name) { ... }
    public Storage(JSONObject o) throws JSONException { ... }
    @Override
    public JSONObject toJSON() { ... }
    @Override
    public int hashCode() { ... }
    @Override
    public boolean equals(Object obj) { ... }
}
