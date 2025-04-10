public class NodeMatcher {
    public static NodeMatcher allWindowRoots() { ... }
    public NodeMatcher descendants() { ... }
    public NodeMatcher withClass(String classname) { ... }
    public NodeMatcher labels(String text) { ... }
    public NodeMatcher textFields() { ... }
    public NodeMatcher tabPanes() { ... }
    public NodeMatcher buttons() { ... }
    public NodeMatcher with(Function<NodeMatcher,NodeMatcher> m) { ... }
    public static void sync(Runnable r) { ... }
    public Node get() { ... }
    public int count() { ... }
    public void replaceText(String text) { ... }
    public void replaceNumber(int value) { ... }
    public void click() { ... }
    public void clickASync() { ... }
    public void selectTab(int index) { ... }
    public void exists() { ... }
    public void doesNotExists() { ... }
    public void acceptDialog() { ... }
    public void closeDialogs() { ... }
    public void print() { ... }
    public void screenshotWindow(String out) { ... }
}
