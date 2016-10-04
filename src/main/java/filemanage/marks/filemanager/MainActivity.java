package filemanage.marks.filemanager;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener
{
    final static int PERM_READ_SD_CARD = 7077; // app defined prmission code
    GridView fgv;  /// GridView to display files and folders
    TextView err;
    File currentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fgv = (GridView) findViewById(R.id.file_grid_view);
        err = (TextView) findViewById(R.id.error_txt);

        ///// implement new permission checks for Android M
        int permCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //// check if the permission has already been granted
        if ( permCheck == PackageManager.PERMISSION_GRANTED)
        {
             if ( isExternalStorageReadable() )
              {
                  currentDir = getSDCardTopDir();;
                  if (currentDir != null) fgv.setAdapter(new FileAdapter(currentDir, this));
              }
              else err.setText(R.string.no_access);
        }
        //// if the permission is not granted, request it.
        else
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_READ_SD_CARD);
        }
    }

    //// refresh the grid view with a new file set.
    public void refreshGridView(File newDir)
    {
        currentDir=newDir;
        setContentView(R.layout.activity_main);
        fgv = (GridView) findViewById(R.id.file_grid_view);
        fgv.setAdapter(new FileAdapter(currentDir, this));
        fgv.invalidate();
        err = (TextView) findViewById(R.id.error_txt);
        err.setText(currentDir.getAbsolutePath());
        err.invalidate();
        findViewById(R.id.main_back_button).setOnClickListener(this);
    }

    public void showEmptyFolder()
    {
        setContentView(R.layout.empty_folder);
        findViewById(R.id.back_button).setOnClickListener(this);
    }

    /// checks to see if external storage is readable and available.
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) return true;

        return false;
    }

    public void onClick(View v)
    {
        /// handle back button click for empty folder view
        if (v.getId()==R.id.back_button)
        {
            refreshGridView(currentDir);
            return;
        }
        /// handle back button click for non-empty folder view
        if (v.getId()==R.id.main_back_button)
        {
            if (currentDir.toString().equals(getSDCardTopDir().toString())) return;
            File above = currentDir.getParentFile();
            if (above != null) refreshGridView(above);
        }
    }

    /// permission callback for new M permission model. Callback fires after permission request.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERM_READ_SD_CARD:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if ( isExternalStorageReadable() )
                    {
                        currentDir = getSDCardTopDir();
                        if (currentDir != null) fgv.setAdapter(new FileAdapter(currentDir, this));
                    }
                    else Toast.makeText(this, R.string.no_access, Toast.LENGTH_LONG);

                }
                else Toast.makeText(this, R.string.no_permission, Toast.LENGTH_LONG);
            }
        }
    }

    public File getSDCardTopDir()
    {
        return Environment.getExternalStorageDirectory();
    }
}
