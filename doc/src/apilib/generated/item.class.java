public class Item implements ToJSON {
    public final String id;
    public final String name;
    public final Unit unit;
    public final long deleted;
    public Item(String id,String name,Unit unit,long deleted) { ... }
    public Item(String id,String name,Unit unit) { ... }
    public Item(JSONObject o) throws JSONException { ... }
    @Override
    public JSONObject toJSON() { ... }
    @Override
    public int hashCode() { ... }
    @Override
    public boolean equals(Object obj) { ... }
}
