package tp_android.tp_android;

import android.graphics.Bitmap;

public class GridViewItem {

    private String path;
    private String prefix;
    private boolean isDirectory;
    private Bitmap image;


    public GridViewItem(String path, boolean isDirectory, Bitmap image, String prefix) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.image = image;
        this.prefix = prefix;
    }


    public String getPath() {
        return path;
    }


    public boolean isDirectory() {
        return isDirectory;
    }


    public Bitmap getImage() {
        return image;
    }

    public String getPrefix() {
        return prefix;
    }
}
