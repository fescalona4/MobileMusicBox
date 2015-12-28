package com.musicbox.mobilemusicbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fescalona on 12/24/2015.
 */
public class SongListAdapter extends ArrayAdapter<Song> {

    private List<Song> songs;


    public SongListAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
        songs = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item, parent, false);
        }

        Song song = songs.get(position);

        TextView titleText = (TextView) convertView.findViewById(R.id.titleText1);
        titleText.setText(song.getTitle());

        TextView artistText = (TextView) convertView.findViewById(R.id.artistText);
        artistText.setText(song.getArtist());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
        imageView.setImageDrawable(null);

        String imageUrl = HomeActivity.PHOTOS_BASE_URL + song.getImage();
        Picasso.with(getContext()).load(imageUrl).into(imageView);


        return convertView;
    }

}
