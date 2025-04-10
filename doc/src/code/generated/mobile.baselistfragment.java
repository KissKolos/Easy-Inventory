public abstract class BaseListFragment<T extends ToJSON> extends Fragment implements LoggedInActivity.ToolbarController {
    protected abstract T[] load(String query,int offset,int length,boolean archived) throws FormattedException;
    protected abstract void delete(String id) throws FormattedException;
    protected void reload() { ... }
    protected void addExtraContext(Intent i) {}
    protected boolean canAdd() { ... }
    protected void initRoot(View root, SelectableArrayAdapter<T> adapter, Function<T,String> id_getter,Class<?> edit_activity) { ... }
    @Override
    public void controlToolbar(TextView title, ImageButton warehouse_button, SearchView search_bar) { ... }
    protected void startActivity(Class<?> activity) { ... }
    protected void startActivityForSelected(Class<?> activity) { ... }
}
