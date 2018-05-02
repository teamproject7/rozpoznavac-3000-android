package tp_android.tp_android;


import android.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.GridView;


import java.util.List;
import java.util.ArrayList;

import android.os.Environment;

import java.io.FileFilter;
import java.io.File;
import android.util.Log;


public class GalleryActivity extends Activity implements OnItemClickListener {
    private List<GridViewItem> gridItems;
    private final int MyVersion = Build.VERSION.SDK_INT;
    private boolean infile = false;
    private String sdpath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        if(new File("/storage/extSdCard/").exists())
        {
            sdpath="/storage/extSdCard/";
        }
        else if(new File("/storage/sdcard1/").exists())
        {
            sdpath="/storage/sdcard1/";
        }
        else if(new File("/storage/usbcard1/").exists())
        {
            sdpath="/storage/usbcard1/";
        }
        else if(new File("/storage/sdcard0/").exists())
        {
            sdpath="/storage/sdcard0/";
        }

        if (MyVersion >= Build.VERSION_CODES.M) {
            if (!PermissionsAllow.checkIfAlreadyhavePermissionRead(this)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                fill();
            } else {
                fill();
            }
        }
        if (MyVersion < Build.VERSION_CODES.M) {
            fill();
        }
    }

    @Override
    public void onBackPressed() {
        if (infile==true){
            infile=false;
            fill();
        }
        else{
            this.finish();
        }
    }

    private void fill(){
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        setGridAdapter(targetPath,true);
    }


    private void setGridAdapter(String path, boolean mainDir) {
        gridItems = new ArrayList<GridViewItem>();
        gridItems = createGridItems(path,gridItems,"PHONE/");
        if (!sdpath.equals("") && mainDir==true){
            gridItems = createGridItemsSD(sdpath,gridItems,"SD/");
        }

        MyGridAdapter adapter = new MyGridAdapter(this, gridItems);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }


    private List<GridViewItem> createGridItems(String directoryPath,List<GridViewItem> items,String prefix) {
        File[] files = new File(directoryPath).listFiles(new ImageFileFilter());
        for (File file : files) {
            if ((file.isDirectory()&& file.listFiles(new ImageFileFilter()).length > 0 ) && (  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().equals(file.getAbsolutePath()) || Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath().equals(file.getAbsolutePath()) || file.getAbsolutePath().equals(Environment.getExternalStorageDirectory() + File.separator + "spz_app")) ) {
                if(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath().equals(file.getAbsolutePath())){
                    items.add(new GridViewItem(file.getAbsolutePath()+File.separator+"Camera", true, null,prefix));
                }
                else{
                    items.add(new GridViewItem(file.getAbsolutePath(), true, null,prefix));
                }
            }
            else if(isImageFile(file.getAbsolutePath()) && file.getAbsolutePath().replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator,"").contains(File.separator)){
                Bitmap image = BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),50,50);
                items.add(new GridViewItem(file.getAbsolutePath(), false, image,""));

            }
        }
        return items;
    }

    private List<GridViewItem> createGridItemsSD(String directoryPath,List<GridViewItem> items, String prefix) {
        File[] files = new File(directoryPath).listFiles(new ImageFileFilter());
        for (File file : files) {
            Log.d("filead", file.getAbsolutePath());
            if ((file.isDirectory()&& file.listFiles(new ImageFileFilter()).length > 0 ) && (  (new File(directoryPath+File.separator+"DCIM")).getAbsolutePath().equals(file.getAbsolutePath()) ||  (new File(directoryPath+File.separator+"Download")).getAbsolutePath().equals(file.getAbsolutePath()) || file.getAbsolutePath().equals(directoryPath + File.separator + "spz_app")) ) {
                if((new File(directoryPath+File.separator+"DCIM")).getAbsolutePath().equals(file.getAbsolutePath())){
                    items.add(new GridViewItem(file.getAbsolutePath()+File.separator+"Camera", true, null,prefix));
                }
                else{
                    items.add(new GridViewItem(file.getAbsolutePath(), true, null,prefix));
                }
            }
            else if(isImageFile(file.getAbsolutePath()) && file.getAbsolutePath().replaceAll(new File(directoryPath).getAbsolutePath() + File.separator,"").contains(File.separator)){
                Bitmap image = BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),50,50);
                items.add(new GridViewItem(file.getAbsolutePath(), false, image,""));
            }
        }
        return items;
    }

    private boolean isImageFile(String filePath) {
        if (filePath.endsWith(".jpg") || filePath.endsWith(".png") || filePath.endsWith(".jpeg") || filePath.endsWith(".JPG") || filePath.endsWith(".PNG") || filePath.endsWith(".JPEG")){
            return true;
        }
        return false;
    }

    @Override
    public void
    onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (gridItems.get(position).isDirectory()) {
            infile=true;
            setGridAdapter(gridItems.get(position).getPath(),false);
        }
        else {
            Intent intent = new Intent(this, GalleryItemViewActivity.class);
            intent.putExtra("path", gridItems.get(position).getPath());
            startActivity(intent);
        }
    }


    /**
     * This can be used to filter files.
     */
    private class ImageFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            else if (isImageFile(file.getAbsolutePath())) {
                return true;
            }
            return false;
        }
    }

}

