public class Operation implements ToJSON {
    public final String id;
    public final String name;
    public final boolean is_add;
    public final OperationItem[] items;
    public final long deleted;
    public Operation(String id,String name,boolean is_add,OperationItem[] items,long deleted) { ... }
    public Operation(String id,String name,boolean is_add,OperationItem[] items) { ... }
    public Operation(JSONObject o) throws JSONException { ... }
    @Override
    public JSONObject toJSON() { ... }
    @Override
    public int hashCode() { ... }
    @Override
    public boolean equals(Object obj) { ... }
}
