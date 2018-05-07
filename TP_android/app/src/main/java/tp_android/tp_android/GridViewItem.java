package tp_android.tp_android;

public class GridViewItem {

    private String path;
    private String prefix;
    private boolean isDirectory;


    public GridViewItem(String path, boolean isDirectory, String prefix) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.prefix = prefix;
    }

    public String getPath() {
        return path;
    }


    public boolean isDirectory() {
        return isDirectory;
    }


    public String getPrefix() {
        return prefix;
    }
}
