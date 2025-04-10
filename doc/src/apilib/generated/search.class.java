public class ItemStack implements ToJSON {
    public final Warehouse warehouse;
    public final Storage storage;
    public final Item item;
    public final String lot;
    public final String manufacturer_serial;
    public final int amount;
    public final int available_amount;
    public final int global_serial;
    public ItemStack(Warehouse warehouse,Storage storage,Item item,String lot,
            String manufacturer_serial,int amount,int available_amount,int global_serial) { ... }
    public ItemStack(JSONObject o) throws JSONException { ... }
    @Override
    public int hashCode() { ... }
    @Override
    public boolean equals(Object obj) { ... }
    @Override
    public JSONObject toJSON() { ... }
}
