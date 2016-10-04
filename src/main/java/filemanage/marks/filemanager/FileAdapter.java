package filemanage.marks.filemanager;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.view.ViewGroup;
import java.io.File;

/// File Adapter, populates GridView with current directory contents
public class FileAdapter implements ListAdapter
{
    public static int ITEM_TYPE_FILE = 0;
    public static int ITEM_TYPE_DIR = 1;

    public File currentDir;
    MainActivity act;
    LayoutInflater inflator;

    public FileAdapter(File dir, MainActivity act)
    {
        this.act = act;
        currentDir = dir;
        inflator = act.getLayoutInflater();
    }

    public int getCount()
    {
        if (currentDir==null) return 0;
        if (currentDir.listFiles()==null) return 0;
        return currentDir.listFiles().length;
    }

    public int getItemViewType(int position)
    {
        File[] files = currentDir.listFiles();
        File f = files[position];
        if (f.isDirectory()) return ITEM_TYPE_DIR;
        else return ITEM_TYPE_FILE;
    }

    /// returns a view that represents a file or folder in the UI
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View itemView = inflator.inflate(R.layout.file_item_view, null);
        File f = getItem(position);

        TextView label = (TextView) itemView.findViewById(R.id.file_item_label);
        label.setText(f.getName());
        ImageView icon = (ImageView) itemView.findViewById(R.id.file_item_image);

        if (f.isDirectory())
        {
            icon.setImageResource(R.drawable.ic_folder_open_24dp1);
            itemView.setOnClickListener(new FolderListener(position));
        }
        else icon.setImageResource(R.drawable.ic_insert_drive_file_24dp1);
        return itemView;
    }

    public File getItem(int id)
    {
        File[] files = currentDir.listFiles();
        return files[id];
    }

    public long getItemId(int position)
    {
        return position;
    }

    public int getViewTypeCount()
    {
        return 2;
    }

    public boolean hasStableIds()
    {
        return true;
    }

    public boolean isEmpty()
    {
        return  !(getCount() > 0);
    }

    public boolean areAllItemsEnabled()
    {
        return true;
    }

    public boolean isEnabled(int item)
    {
        return true;
    }

    public void registerDataSetObserver(DataSetObserver observer)
    {
    }

    public void unregisterDataSetObserver(DataSetObserver observer)
    {
    }

    /// Handles on click event for folders
    class FolderListener implements View.OnClickListener
    {
        public FolderListener(int idx)
        {
            this.idx=idx;
        }
        int idx;

        public void onClick(View v)
        {
            File f = getItem(idx);
            if (f.isDirectory())
            {
                if (f.listFiles().length > 0) act.refreshGridView(f);
                else act.showEmptyFolder();
            }
        }
    }
}
