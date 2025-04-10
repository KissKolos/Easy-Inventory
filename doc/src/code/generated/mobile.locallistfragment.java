public abstract class LocalListFragment<T extends ToJSON> extends BaseListFragment<T> {
    @Override
    protected void addExtraContext(Intent i) { ... }
    @Override
    protected boolean canAdd() { ... }
    @Override
    protected void initRoot(View root, SelectableArrayAdapter<T> adapter, Function<T, String> id_getter, Class<?> edit_activity) { ... }
    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) { ... }
    public @Nullable Warehouse getSelectedWarehouse() { ... }
}
