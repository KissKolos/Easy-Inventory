public abstract class BaseListActivity<T extends ToJSON> extends PaginatedListActivity<T> {
    protected BaseListActivity(Function<Activity,SelectableArrayAdapter<T>> adapter_factory) { ... }
    protected abstract T getNullDisplay();
    @Override
    protected void reload() { ... }
    protected abstract T[] load(String query,int offset,int length,boolean archived) throws FormattedException;
    @Override
    protected void onCreate(Bundle savedInstanceState) { ... }
}
