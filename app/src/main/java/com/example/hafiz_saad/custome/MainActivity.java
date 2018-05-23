package com.example.hafiz_saad.custome;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    private AlertDialogHelper alertDialogHelper;
    private ImageAdapter imageAdapter;
    private ArrayList<String> multiSelect;
    private static final int REQUEST_FOR_STORAGE_PERMISSION = 123;
    private boolean isMultiSelect = false;
    private ActionMode mActionMode;
    private boolean checked = false;
    private Menu context_menu;
    private ArrayList<String> imageUrls;
    private File files;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        alertDialogHelper = new AlertDialogHelper(this);
        multiSelect = new ArrayList<String>();
        populateImagesFromGallery();
    }

    public void btnChoosePhotosClick(View v){

        ArrayList<String> selectedItems = imageAdapter.getCheckedItems();

        if (selectedItems!= null && selectedItems.size() > 0) {
            Toast.makeText(MainActivity.this, "Total photos selected: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
            Log.d(MainActivity.class.getSimpleName(), "Selected Items: " + selectedItems.toString());
        }
    }

    private void populateImagesFromGallery() {
        if (!mayRequestGalleryImages()) {
            return;
        }

         imageUrls = loadPhotosFromNativeGallery();
        initializeRecyclerView(imageUrls);
    }

    private boolean mayRequestGalleryImages() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
//            promptStoragePermission();
//            showPermissionRationaleSnackBar();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_FOR_STORAGE_PERMISSION);
        }

        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_FOR_STORAGE_PERMISSION: {

                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        populateImagesFromGallery();
                    } else {
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
////                            showPermissionRationaleSnackBar();
//                        } else {
//                            Toast.makeText(this, "Go to settings and enable permission", Toast.LENGTH_LONG).show();
//                        }
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    }
                }

                break;
            }
        }
    }

    private ArrayList<String> loadPhotosFromNativeGallery() {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");

        ArrayList<String> imageUrls = new ArrayList<String>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));

            Log.d(" Array path ",imageUrls.get(i));
        }

        return imageUrls;
    }

    private void initializeRecyclerView(final ArrayList<String> imageUrls) {
        imageAdapter = new ImageAdapter(this, imageUrls,multiSelect);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,ViewPagerImages.class);
                intent.putStringArrayListExtra("Images",imageUrls);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));
    }

    private void showPermissionRationaleSnackBar() {
//        Snackbar.make(findViewById(R.id.button1), getString(R.string.permission_rationale),
//                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Request the permission
//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{READ_EXTERNAL_STORAGE},
//                        REQUEST_FOR_STORAGE_PERMISSION);
//            }
//        }).show();

    }
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertMessage();
//                    alertDialogHelper.showAlertDialog("Delete Images","Are you sure, you want to delete images?","DELETE","CANCEL",1,false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiSelect = new ArrayList<String>();
            refreshAdapter();
        }
    };

    public void alertMessage(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Delete Images!");
        dialog.setMessage("Are you sure, you want to delete Image");
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImages();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiSelect.contains(imageUrls.get(position)))
                multiSelect.remove(imageUrls.get(position));
            else
                multiSelect.add(imageUrls.get(position));

            if (multiSelect.size() > 0)
                mActionMode.setTitle("" + multiSelect.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }
    public void refreshAdapter()
    {
        imageAdapter.multiSelect=multiSelect;
        imageAdapter.mImagesList=imageUrls;
        imageAdapter.notifyDataSetChanged();
    }


    public void deleteImages(){
        File file;
        File dir;
        boolean delete = false;
        if(multiSelect.size()>0)
        {
            for(int i=0;i<multiSelect.size();i++) {
                file = new File(multiSelect.get(i));
                if(file.exists()){
                    try {
                        if (file.delete()) delete = true;
                        else delete = false;
                        dir = file;
                        deleteDir(dir);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
//                            final Uri contentUri = Uri.fromFile(new File("file://" + Environment.getExternalStorageDirectory()));
//                            scanIntent.setData(contentUri);
//                            sendBroadcast(scanIntent);
//                        } else {
//
                            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                            sendBroadcast(intent);
//                        }
                    }
                    catch (Exception e){

                    }
                }
                imageUrls.remove(multiSelect.get(i));
            }
            if(delete){
                Toast.makeText(this, "Images Successfully Deleted", Toast.LENGTH_SHORT).show();
            }
            imageAdapter.notifyDataSetChanged();

            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
    }
    public void refreshGallery(){
        if(android.os.Build.VERSION.SDK_INT < 17){
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" +  Environment.getExternalStorageDirectory())));
        }
        else{
            MediaScannerConnection.scanFile(this, new String[] { Environment.getExternalStorageDirectory().toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri)
                {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
