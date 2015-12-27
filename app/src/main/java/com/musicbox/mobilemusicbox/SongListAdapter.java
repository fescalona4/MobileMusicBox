package com.musicbox.mobilemusicbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by fescalona on 12/24/2015.
 */
public class SongListAdapter extends ArrayAdapter<Song> {

    private List<Song> songs;

    private LruCache<Float, Bitmap> imageCache;

    public SongListAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
        songs = objects;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        imageCache = new LruCache<>(cacheSize);
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


        //display img
        Object bitmap = imageCache.get(song.getId());
        if (bitmap != null) {
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView1);
            iv.setImageBitmap(song.getBitmap());
        } else {
            SongAndView container = new SongAndView();
            container.song = song;
            container.view = convertView;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }


        return convertView;
    }


    class SongAndView {
        public Song song;
        public View view;
        public Bitmap bitmap;

    }

    private class ImageLoader extends AsyncTask<SongAndView, Void, SongAndView> {

        @Override
        protected SongAndView doInBackground(SongAndView... params) {

            SongAndView container = params[0];
            Song song = container.song;

            try {
                String imageUrl = HomeActivity.PHOTOS_BASE_URL + song.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                song.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(SongAndView result) {
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
            image.setImageBitmap(result.bitmap);
            imageCache.put(result.song.getId(), result.bitmap);
        }
    }
}
