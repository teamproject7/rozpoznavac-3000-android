package tp_android.tp_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Display;
import android.view.View;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.io.FileFilter;
import java.io.File;

public class GalleryActivity extends Activity {
    private ArrayList<GridViewItem> gridItems;
    private final int MyVersion = Build.VERSION.SDK_INT;
    private boolean infile = false;
    private String sdpath = "";
    private int Measuredwidth = 0;
    private int Measuredheight = 0;
    private static final String MY_PREFS_NAME = "Setting";
    private SharedPreferences prefs;
    private Boolean help;
    private FloatingActionButton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        help = prefs.getBoolean("help",true);


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

        Point size = new Point();
        WindowManager w = getWindowManager();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
            Measuredwidth = size.x;
            Measuredheight = size.y;
        }else{
            Display d = w.getDefaultDisplay();
            Measuredwidth = d.getWidth();
            Measuredheight = d.getHeight();
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
        if (infile){
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
        gridItems = createGridItems(path, gridItems, "PHONE/");
        if (!sdpath.equals("") && mainDir) {
            gridItems = createGridItemsSD(sdpath, gridItems, "SD/");
        }
        fb = findViewById(R.id.info);
        fb.show();
        if (help && infile) {
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialogHelp();
                }
            });
        } else {
            fb.hide();
        }

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getApplicationContext(),1,false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(Measuredwidth);
        layoutManager.scrollToPositionWithOffset(0, 20);
        recyclerView.setLayoutManager(layoutManager);

        DataAdapter adapter = new DataAdapter(getApplicationContext(),gridItems);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new DataAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, String places_name) {
                if (gridItems.get(position).isDirectory()) {
                    infile=true;
                    setGridAdapter(gridItems.get(position).getPath(),false);
                }
                else {
                    Intent intent = new Intent(getApplication(), GalleryItemViewActivity.class);
                    intent.putExtra("path", gridItems.get(position).getPath());
                    startActivity(intent);
                }
            }
        });

        adapter.setOnLongItemClickListener(new DataAdapter.onRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClickListener(View view, int position, String places_name) {
                if (!gridItems.get(position).isDirectory()){
                    String path = gridItems.get(position).getPath();
                    String[] arrayString = path.split("/");
                    String sufix = arrayString[arrayString.length-1];
                    alertDialog(sufix,path);
                }
            }
        });
    }

    private ArrayList<GridViewItem> createGridItems(String directoryPath, ArrayList<GridViewItem> items, String prefix) {
        File[] files = new File(directoryPath).listFiles(new ImageFileFilter());
        for (File file : files) {
            if ((file.isDirectory()&& file.listFiles(new ImageFileFilter()).length > 0 ) && (  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().equals(file.getAbsolutePath()) || Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath().equals(file.getAbsolutePath()) || file.getAbsolutePath().equals(Environment.getExternalStorageDirectory() + File.separator + "spz_egov")) ) {
                if(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath().equals(file.getAbsolutePath())){
                    if(new File(file.getAbsolutePath()+File.separator+"Camera").exists()){
                        items.add(new GridViewItem(file.getAbsolutePath()+File.separator+"Camera", true, prefix));
                    }
                    if(new File(file.getAbsolutePath()+File.separator+"100ANDRO").exists()){
                        items.add(new GridViewItem(file.getAbsolutePath()+File.separator+"100ANDRO", true, prefix));
                    }
                }
                else if(new File(file.getAbsolutePath()).exists()) {
                        items.add(new GridViewItem(file.getAbsolutePath(), true, prefix));
                }
            }
            else if(isImageFile(file.getAbsolutePath()) && file.getAbsolutePath().replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator,"").contains(File.separator)){
                if(new File(file.getAbsolutePath()).exists()){
                    items.add(new GridViewItem(file.getAbsolutePath(), false,""));
                }

            }
        }
        return items;
    }

    private ArrayList<GridViewItem> createGridItemsSD(String directoryPath,ArrayList<GridViewItem> items, String prefix) {
        File[] files = new File(directoryPath).listFiles(new ImageFileFilter());
        for (File file : files) {
            if ((file.isDirectory()&& file.listFiles(new ImageFileFilter()).length > 0 ) && (  (new File(directoryPath+File.separator+"DCIM")).getAbsolutePath().equals(file.getAbsolutePath()) ||  (new File(directoryPath+File.separator+"Download")).getAbsolutePath().equals(file.getAbsolutePath()) || file.getAbsolutePath().equals(directoryPath + File.separator + "spz_egov")) ) {
                if((new File(directoryPath+File.separator+"DCIM")).getAbsolutePath().equals(file.getAbsolutePath())){
                    if(new File(file.getAbsolutePath()+File.separator+"Camera").exists()){
                        items.add(new GridViewItem(file.getAbsolutePath()+File.separator+"Camera", true, prefix));
                    }
                    if(new File(file.getAbsolutePath()+File.separator+"100ANDRO").exists()){
                        items.add(new GridViewItem(file.getAbsolutePath()+File.separator+"100ANDRO", true, prefix));
                    }
                }
                else if(new File(file.getAbsolutePath()).exists()) {
                    items.add(new GridViewItem(file.getAbsolutePath(), true, prefix));
                }
            }
            else if(isImageFile(file.getAbsolutePath()) && file.getAbsolutePath().replaceAll(new File(directoryPath).getAbsolutePath() + File.separator,"").contains(File.separator)){
                if(new File(file.getAbsolutePath()).exists()){
                    items.add(new GridViewItem(file.getAbsolutePath(), false,""));
                }

            }
        }
        return items;
    }

    private boolean isImageFile(String filePath) {
        return filePath.endsWith(".jpg") || filePath.endsWith(".png") || filePath.endsWith(".jpeg") || filePath.endsWith(".JPG") || filePath.endsWith(".PNG") || filePath.endsWith(".JPEG");
    }

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

    private void alertDialog(String path,String fullPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        File file = new File(fullPath);
        double file_length = Math.round(((double)file.length()/(1024*1024)*100.0))/100.0;
        builder.setTitle("Názov súboru a veľkosť");
        builder.setMessage(path + " - " + file_length + "Mb");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void alertDialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setMessage("Pre otvorenie obrázku kliknite a pre zistenie názvu podržte položku");
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

}

