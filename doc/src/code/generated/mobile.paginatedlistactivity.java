public abstract class PaginatedListActivity<T> extends AppCompatActivity {
    public static final int PAGE_SIZE=20;
    protected int offset=0;
    protected SearchView search;
    protected SelectableArrayAdapter<T> adapter;
    protected SwipeRefreshLayout sw;
    protected PaginatedListActivity(int activity,Function<Activity,SelectableArrayAdapter<T>> adapter_factory) { ... }
    protected void reload() { ... }
    protected abstract T[] load(String query,int offset,int length,boolean archived) throws FormattedException;
    protected void readExtraContext(Intent i) {}
    @Override
    protected void onCreate(Bundle savedInstanceState) { ... }
    @Override
    public boolean onSupportNavigateUp() { ... }
}
