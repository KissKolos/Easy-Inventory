public abstract class BaseEditActivity<T> extends AppCompatActivity {
    public BaseEditActivity(int layout) { ... }
    protected abstract void initUI(@Nullable T item);
    protected abstract @NonNull T getItem();
    protected abstract void save(@Nullable T original,@NonNull T item) throws FormattedException;
    protected abstract T readContext(Intent i);
    protected void readExtraContext(Intent i) {}
    @Override
    protected void onCreate(Bundle savedInstanceState) { ... }
    @Override
    public boolean onSupportNavigateUp() { ... }
}
