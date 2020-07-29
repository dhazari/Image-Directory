package com.example.photoapp54;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.photoapp54.R;
import com.example.photoapp54.model.Photo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Adapter to hold the photo view and its details.
 */
public class PhotoAdaptor extends ArrayAdapter<Photo> {

    private Context context;
    private List items;

    public PhotoAdaptor(Context context, int resourceId, List<Photo> items) {
        super(context, resourceId, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            //this is if it is on the display section
            convertView = inflater.inflate(R.layout.adaptor_view, null);
        }

        ImageView toEnter = (ImageView) convertView.findViewById(R.id.imageDisplay);

        Photo photo = (Photo) items.get(position);
        toEnter.setImageURI(Uri.parse(photo.getPath()));
        return convertView;
    }

    /**
     * Holds the photo and catptions privately.
     */
    private class ViewHolder {
        ImageView photo;
    }
}

