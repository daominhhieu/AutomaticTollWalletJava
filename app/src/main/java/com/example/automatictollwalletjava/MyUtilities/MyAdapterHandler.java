package com.example.automatictollwalletjava.MyUtilities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.automatictollwalletjava.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MyAdapterHandler extends ArrayAdapter<String> {

    Context mcontext = null;
    ArrayList<String> loc_Title = new ArrayList<String>();
    ArrayList<String> loc_Description = new ArrayList<String>();
    ArrayList<Integer> loc_Imgs = new ArrayList<Integer>();


    public MyAdapterHandler(Context context, ArrayList<String> title, ArrayList<String> description, ArrayList<Integer> imgs) {
        super(context, R.layout.row, R.id.HistoryTitle_tv, title);
        mcontext = context;
        loc_Title = title;
        loc_Description = description;
        loc_Imgs = imgs;

    }

    public MyAdapterHandler(Context context, ArrayList<String> title, ArrayList<String> description)
    {
        super(context, R.layout.ble_device_info_row, R.id.device_name_tv, title);
        mcontext = context;
        loc_Title = title;
        loc_Description = description;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
        View row;
        if(loc_Imgs.size() > 0) {
            row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.HistoryTitle_tv);
            TextView myDescription = row.findViewById(R.id.HistoryContent_tv);

            // now set our resources on views
            images.setImageResource(loc_Imgs.get(position));
            myTitle.setText(loc_Title.get(position));
            myDescription.setText(loc_Description.get(position));
        }
        else
        {
            row = layoutInflater.inflate(R.layout.ble_device_info_row, parent, false);
            TextView myTitle = row.findViewById(R.id.device_name_tv);
            TextView myDescription = row.findViewById(R.id.device_MAC_tv);

            // now set our resources on views
            myTitle.setText(loc_Title.get(position));
            myDescription.setText(loc_Description.get(position));
        }
        return row;

    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }

}
